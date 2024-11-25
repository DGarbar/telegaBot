package org.dharbar.telegabot.service.position;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.controller.filter.PositionFilter;
import org.dharbar.telegabot.repository.PositionRepository;
import org.dharbar.telegabot.repository.entity.DateTriggerEntity;
import org.dharbar.telegabot.repository.entity.OrderEntity;
import org.dharbar.telegabot.repository.entity.PositionEntity;
import org.dharbar.telegabot.repository.entity.PositionType;
import org.dharbar.telegabot.repository.entity.PriceTriggerEntity;
import org.dharbar.telegabot.repository.specification.PositionSpec;
import org.dharbar.telegabot.repository.util.ChangeComparator;
import org.dharbar.telegabot.repository.util.ChangeResult;
import org.dharbar.telegabot.service.position.PositionCalculationService.PositionCalculation;
import org.dharbar.telegabot.service.position.dto.DateTriggerDto;
import org.dharbar.telegabot.service.position.dto.OrderDto;
import org.dharbar.telegabot.service.position.dto.PositionDto;
import org.dharbar.telegabot.service.position.dto.PriceTriggerDto;
import org.dharbar.telegabot.service.position.mapper.PositionServiceMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.dharbar.telegabot.service.position.PositionCalculationService.calculatePositionValues;

@Service
@RequiredArgsConstructor
public class PositionService {

    private final PositionDateTriggerService dateTriggerService;

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
                                     Set<PriceTriggerDto> priceTriggerDtos,
                                     Set<DateTriggerDto> dateTriggerDtos) {
        PositionCalculation positionCalculation = calculatePositionValues(positionType, orderDtos);
        Set<OrderEntity> orders = positionMapper.toEntityOrders(orderDtos);
        Set<PriceTriggerEntity> priceTriggers = positionMapper.toEntityPriceTriggers(priceTriggerDtos);
        Set<DateTriggerEntity> dateTriggers = positionMapper.toEntityDateTriggers(dateTriggerDtos);
        PositionEntity position = positionMapper.toNewEntity(name,
                ticker,
                positionType,
                portfolioId,
                comment,
                positionCalculation,
                orders,
                priceTriggers,
                dateTriggers);

        PositionEntity savedPosition = positionRepository.save(position);

        PositionDto positionDto = positionMapper.toDto(savedPosition);
        dateTriggerService.createPositionDateTrigger(positionDto.getId(), positionDto.getName(), positionDto.getDateTriggers());

        return positionDto;
    }

    @Transactional
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

        Set<DateTriggerEntity> dateTriggers = positionMapper.toEntityDateTriggers(positionDto.getDateTriggers());
        ChangeResult<DateTriggerEntity> dateTriggerChange = ChangeComparator.compare(position.getDateTriggers(), dateTriggers);
        dateTriggerChange.getRemoved().forEach(position::removeDateTrigger);
        dateTriggerChange.getPresent().forEach(positionMapper::updateEntity);

        positionMapper.updateEntity(position,
                positionDto.getName(),
                positionDto.getType(),
                positionDto.getComment(),
                orderChange.getAdded(),
                priceTriggerChange.getAdded(),
                dateTriggerChange.getAdded(),
                positionCalculation);
        PositionEntity savedPosition = positionRepository.save(position);

        PositionDto updatedPositionDto = positionMapper.toDto(savedPosition);

        List<UUID> removedDateTriggers = dateTriggerChange.getRemoved().stream().map(DateTriggerEntity::getId).toList();
        dateTriggerService.updatePositionDateTrigger(updatedPositionDto.getId(),
                updatedPositionDto.getName(),
                positionDto.getDateTriggers(),
                removedDateTriggers);
        return updatedPositionDto;
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

    @Transactional
    public void delete(UUID positionId) {
        PositionEntity position = positionRepository.findByIdForUpdate(positionId).orElseThrow();
        positionRepository.delete(position);
    }

    public void deleteAlarm(UUID alarmId, UUID positionId) {
        PositionEntity position = positionRepository.findByIdForUpdate(positionId).orElseThrow();
        position.getAlarms().removeIf(alarm -> alarm.getId().equals(alarmId));
        positionRepository.save(position);
    }
}
