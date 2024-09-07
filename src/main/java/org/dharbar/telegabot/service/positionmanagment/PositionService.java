package org.dharbar.telegabot.service.positionmanagment;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.controller.filter.PositionFilter;
import org.dharbar.telegabot.repository.PositionRepository;
import org.dharbar.telegabot.repository.entity.OrderEntity;
import org.dharbar.telegabot.repository.entity.PositionEntity;
import org.dharbar.telegabot.repository.entity.PriceTriggerEntity;
import org.dharbar.telegabot.repository.specification.PositionSpec;
import org.dharbar.telegabot.repository.util.ChangeComparator;
import org.dharbar.telegabot.repository.util.ChangeResult;
import org.dharbar.telegabot.service.positionmanagment.PositionCalculationService.PositionCalculation;
import org.dharbar.telegabot.service.positionmanagment.dto.OrderDto;
import org.dharbar.telegabot.service.positionmanagment.dto.PositionDto;
import org.dharbar.telegabot.service.positionmanagment.dto.PriceTriggerDto;
import org.dharbar.telegabot.service.positionmanagment.mapper.PositionServiceMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

import static org.dharbar.telegabot.service.positionmanagment.PositionCalculationService.calculatePositionValues;

@Service
@RequiredArgsConstructor
public class PositionService {

    private final PositionRepository positionRepository;

    private final PositionServiceMapper positionMapper;

    public Page<PositionDto> getPositions(PositionFilter filter, Pageable pageRequest) {
        Specification<PositionEntity> spec = PositionSpec.toSpec(filter);
        return positionRepository.findAll(spec, pageRequest)
                .map(positionMapper::toDto);
    }

    public PositionDto get(UUID positionId) {
        return positionRepository.findById(positionId)
                .map(positionMapper::toDto)
                .orElseThrow();
    }

    public PositionDto cretePosition(String ticker,
                                     UUID portfolioId,
                                     String comment,
                                     Set<OrderDto> orderDtos,
                                     Set<PriceTriggerDto> priceTriggerDtos) {
        PositionCalculation positionCalculation = calculatePositionValues(orderDtos);
        Set<OrderEntity> orders = positionMapper.toEntityOrders(orderDtos);
        Set<PriceTriggerEntity> priceTriggers = positionMapper.toEntityPriceTriggers(priceTriggerDtos);
        PositionEntity position = positionMapper.toNewEntity(ticker, portfolioId, comment, positionCalculation, orders, priceTriggers);

        PositionEntity savedPosition = positionRepository.save(position);

        return positionMapper.toDto(savedPosition);
    }

    public PositionDto updatePosition(PositionDto positionDto) {
        PositionEntity position = positionRepository.findByIdForUpdate(positionDto.getId()).orElseThrow();

        Set<OrderDto> orderDtos = positionDto.getOrders();
        PositionCalculation positionCalculation = calculatePositionValues(orderDtos);

        Set<OrderEntity> orders = positionMapper.toEntityOrders(orderDtos);
        ChangeResult<OrderEntity> orderChange = ChangeComparator.compare(position.getOrders(), orders);
        orderChange.getRemoved().forEach(position::removeOrder);
        orderChange.getPresent().forEach(positionMapper::updateEntity);

        Set<PriceTriggerEntity> priceTriggers = positionMapper.toEntityPriceTriggers(positionDto.getPriceTriggers());
        ChangeResult<PriceTriggerEntity> priceTriggerChange = ChangeComparator.compare(position.getPriceTriggers(), priceTriggers);
        priceTriggerChange.getRemoved().forEach(position::removePriceTrigger);
        priceTriggerChange.getPresent().forEach(positionMapper::updateEntity);

        positionMapper.updateEntity(position, orderChange.getAdded(), priceTriggerChange.getAdded(), positionCalculation);
        PositionEntity savedPosition = positionRepository.save(position);

        return positionMapper.toDto(savedPosition);
    }

    public PositionDto addOrder(UUID positionId, OrderDto orderDto) {
        PositionEntity position = positionRepository.findByIdForUpdate(positionId).orElseThrow();
        return updateAndSavePosition(orderDto, position);
    }

    public PositionDto updatePositionOrder(UUID positionId, OrderDto orderDto) {
        PositionEntity position = positionRepository.findByIdForUpdate(positionId).orElseThrow();
        OrderEntity savedOrder = position.getOrders().stream()
                .filter(order -> order.getId().equals(orderDto.getId()))
                .findFirst()
                .orElseThrow();

        positionMapper.updateEntity(savedOrder, orderDto);

        return updateAndSavePosition(orderDto, position);
    }

    private PositionDto updateAndSavePosition(OrderDto orderDto, PositionEntity position) {
        Set<OrderDto> orderDtos = positionMapper.toDtos(position.getOrders());
        orderDtos.add(orderDto);

        // (optimization)TODO can calculate based only on new order + existing values
        PositionCalculation positionCalculation = calculatePositionValues(orderDtos);
        Set<OrderEntity> orders = positionMapper.toEntityOrders(orderDtos);

        positionMapper.updateEntity(position, orders, position.getPriceTriggers(), positionCalculation);
        PositionEntity savedPosition = positionRepository.save(position);

        return positionMapper.toDto(savedPosition);
    }
}
