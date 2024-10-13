package org.dharbar.telegabot.service.position;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.controller.filter.PositionFilter;
import org.dharbar.telegabot.repository.PositionRepository;
import org.dharbar.telegabot.repository.entity.OrderEntity;
import org.dharbar.telegabot.repository.entity.PositionEntity;
import org.dharbar.telegabot.repository.entity.PositionType;
import org.dharbar.telegabot.repository.entity.PriceTriggerEntity;
import org.dharbar.telegabot.repository.specification.PositionSpec;
import org.dharbar.telegabot.repository.util.ChangeComparator;
import org.dharbar.telegabot.repository.util.ChangeResult;
import org.dharbar.telegabot.service.position.PositionCalculationService.PositionCalculation;
import org.dharbar.telegabot.service.position.dto.OrderDto;
import org.dharbar.telegabot.service.position.dto.PositionDto;
import org.dharbar.telegabot.service.position.dto.PriceTriggerDto;
import org.dharbar.telegabot.service.position.mapper.PositionServiceMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.dharbar.telegabot.service.position.PositionCalculationService.calculatePositionValues;

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

    public PositionDto cretePosition(String name,
                                     String ticker,
                                     PositionType positionType,
                                     UUID portfolioId,
                                     String comment,
                                     Set<OrderDto> orderDtos,
                                     Set<PriceTriggerDto> priceTriggerDtos) {
        PositionCalculation positionCalculation = calculatePositionValues(positionType, orderDtos);
        Set<OrderEntity> orders = positionMapper.toEntityOrders(orderDtos);
        Set<PriceTriggerEntity> priceTriggers = positionMapper.toEntityPriceTriggers(priceTriggerDtos);
        PositionEntity position = positionMapper.toNewEntity(name, ticker, positionType, portfolioId, comment, positionCalculation, orders, priceTriggers);

        PositionEntity savedPosition = positionRepository.save(position);

        return positionMapper.toDto(savedPosition);
    }

    public PositionDto updatePosition(PositionDto positionDto) {
        PositionEntity position = positionRepository.findByIdForUpdate(positionDto.getId()).orElseThrow();

        Set<OrderDto> orderDtos = positionDto.getOrders();
        PositionCalculation positionCalculation = calculatePositionValues(position.getType(), orderDtos);

        Set<OrderEntity> orders = positionMapper.toEntityOrders(orderDtos);
        ChangeResult<OrderEntity> orderChange = ChangeComparator.compare(position.getOrders(), orders);
        orderChange.getRemoved().forEach(position::removeOrder);
        orderChange.getPresent().forEach(positionMapper::updateEntity);

        Set<PriceTriggerEntity> priceTriggers = positionMapper.toEntityPriceTriggers(positionDto.getPriceTriggers());
        ChangeResult<PriceTriggerEntity> priceTriggerChange = ChangeComparator.compare(position.getPriceTriggers(), priceTriggers);
        priceTriggerChange.getRemoved().forEach(position::removePriceTrigger);
        priceTriggerChange.getPresent().forEach(positionMapper::updateEntity);

        positionMapper.updateEntity(position,
                positionDto.getName(),
                positionDto.getType(),
                orderChange.getAdded(),
                priceTriggerChange.getAdded(),
                positionCalculation);
        PositionEntity savedPosition = positionRepository.save(position);

        return positionMapper.toDto(savedPosition);
    }

    public PositionDto recalculate(UUID id) {
        PositionEntity position = positionRepository.findByIdForUpdate(id).orElseThrow();

        PositionEntity savedPosition = recalculate(position);
        return positionMapper.toDto(savedPosition);
    }

    private PositionEntity recalculate(PositionEntity position) {
        Set<OrderDto> orderDtos = positionMapper.toDtos(position.getOrders());
        PositionCalculation positionCalculation = calculatePositionValues(position.getType(), orderDtos);

        positionMapper.updateCalculatedEntity(position, positionCalculation);
        return positionRepository.save(position);
    }

    public PositionDto addOrder(UUID positionId, OrderDto orderDto) {
        PositionEntity position = positionRepository.findByIdForUpdate(positionId).orElseThrow();
        return updateAndSavePosition(orderDto, position);
    }

    public PositionDto updatePositionOrder(UUID positionId, OrderDto orderDto) {
        PositionEntity position = positionRepository.findByIdForUpdate(positionId).orElseThrow();
        Optional<OrderEntity> orderInPassedPosition = position.getOrders().stream()
                .filter(o -> orderDto.getId().equals(o.getId()))
                .findFirst();

        // Order stayed in the same position
        if (orderInPassedPosition.isPresent()) {
            positionMapper.updateEntity(orderInPassedPosition.get(), orderDto);

            return updateAndSavePosition(orderDto, position);
		} else {
            // order changing its positions
            PositionEntity orderOldPosition = positionRepository.findByOrderIdForUpdate(orderDto.getId()).orElseThrow();
            OrderEntity order = orderOldPosition.getOrders().stream().filter(o -> o.getId().equals(orderDto.getId())).findFirst().orElseThrow();

            orderOldPosition.removeOrder(order);
            recalculate(orderOldPosition);

            positionMapper.updateEntity(order, orderDto);
            return updateAndSavePosition(orderDto, position);
		}
	}

    private PositionDto updateAndSavePosition(OrderDto orderDto, PositionEntity position) {
        Set<OrderDto> orderDtos = positionMapper.toDtos(position.getOrders());
        orderDtos.add(orderDto);

        // (optimization)TODO can calculate based only on new order + existing values
        PositionCalculation positionCalculation = calculatePositionValues(position.getType(), orderDtos);
        Set<OrderEntity> orders = positionMapper.toEntityOrders(orderDtos);

        positionMapper.updatePositionOrdersEntity(position, orders, positionCalculation);
        PositionEntity savedPosition = positionRepository.save(position);

        return positionMapper.toDto(savedPosition);
    }


    public void deleteAlarm(UUID alarmId, UUID positionId) {
        PositionEntity position = positionRepository.findByIdForUpdate(positionId).orElseThrow();
        position.getAlarms().removeIf(alarm -> alarm.getId().equals(alarmId));
        positionRepository.save(position);
    }
}
