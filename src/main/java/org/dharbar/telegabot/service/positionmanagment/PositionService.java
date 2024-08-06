package org.dharbar.telegabot.service.positionmanagment;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.repository.PositionRepository;
import org.dharbar.telegabot.repository.entity.OrderEntity;
import org.dharbar.telegabot.repository.entity.PositionEntity;
import org.dharbar.telegabot.service.positionmanagment.dto.OrderDto;
import org.dharbar.telegabot.service.positionmanagment.dto.PositionDto;
import org.dharbar.telegabot.service.positionmanagment.mapper.PositionMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PositionService {

    private final PositionRepository positionRepository;

    private final PositionMapper positionMapper;

    public PositionDto getPosition(UUID positionId) {
        return positionRepository.findById(positionId)
                .map(positionMapper::toDto)
                .orElseThrow();
    }

    public List<PositionDto> getOpenPositions(PageRequest pageRequest) {
        return positionRepository.findAllByIsClosedIsFalse(pageRequest).stream()
                .map(positionMapper::toDto)
                .toList();
    }

    public List<PositionDto> getPositions(PageRequest pageRequest) {
        return positionRepository.findAll(pageRequest).stream()
                .map(positionMapper::toDto)
                .toList();
    }

    public PositionDto saveNewPosition(OrderDto orderDto) {
        OrderEntity order = positionMapper.toEntity(orderDto);
        PositionEntity newPosition = positionMapper.toNewEntity(order);
        // TODO fix with mapper
        newPosition.addOrder(order);

        PositionEntity savedPosition = positionRepository.save(newPosition);
        return positionMapper.toDto(savedPosition);
    }

    public PositionDto savePosition(PositionDto positionDto) {
        Set<OrderEntity> orders = positionMapper.toEntities(positionDto.getOrders());
        PositionEntity position = positionMapper.toEntity(positionDto, orders);
        PositionEntity savedPosition = positionRepository.save(position);

        return positionMapper.toDto(savedPosition);
    }
}
