package org.dharbar.telegabot.service.positionmanagment;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.controller.filter.PositionFilter;
import org.dharbar.telegabot.repository.PositionRepository;
import org.dharbar.telegabot.repository.entity.OrderEntity;
import org.dharbar.telegabot.repository.entity.PositionEntity;
import org.dharbar.telegabot.repository.specification.PositionSpec;
import org.dharbar.telegabot.service.positionmanagment.PositionCalculationService.PositionCalculation;
import org.dharbar.telegabot.service.positionmanagment.dto.OrderDto;
import org.dharbar.telegabot.service.positionmanagment.dto.PositionDto;
import org.dharbar.telegabot.service.positionmanagment.mapper.PositionServiceMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public PositionDto cretePosition(String ticker, UUID portfolioId, String comment, List<OrderDto> orderDtos) {
        PositionCalculation positionCalculation = calculatePositionValues(orderDtos);
        Set<OrderEntity> orders = positionMapper.toEntities(orderDtos);
        PositionEntity position = positionMapper.toNewEntity(ticker, portfolioId, comment, positionCalculation, orders);

        PositionEntity savedPosition = positionRepository.save(position);

        return positionMapper.toDto(savedPosition);
    }

    public PositionDto addOrder(UUID positionId, OrderDto orderDto) {
        PositionEntity position = positionRepository.findById(positionId).orElseThrow();
        Set<OrderDto> orderDtos = positionMapper.toDtos(position.getOrders());
        orderDtos.add(orderDto);

        // (optimization)TODO can calculate based only on new order + existing value
        PositionCalculation positionCalculation = calculatePositionValues(orderDtos);
        Set<OrderEntity> orders = positionMapper.toEntities(orderDtos);

        positionMapper.updateEntity(position, orders, positionCalculation);
        PositionEntity savedPosition = positionRepository.save(position);

        return positionMapper.toDto(savedPosition);
    }
}
