package org.dharbar.telegabot.service.positionmanagment;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.repository.PositionRepository;
import org.dharbar.telegabot.repository.entity.OrderEntity;
import org.dharbar.telegabot.repository.entity.PositionEntity;
import org.dharbar.telegabot.service.positionmanagment.dto.PositionDto;
import org.dharbar.telegabot.service.positionmanagment.mapper.PositionServiceMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PositionService {

    private final PositionRepository positionRepository;

    private final PositionServiceMapper positionMapper;

    public PositionDto get(UUID positionId) {
        return positionRepository.findById(positionId)
                .map(positionMapper::toDto)
                .orElseThrow();
    }

    public Page<PositionDto> getOpenPositions(PageRequest pageRequest) {
        return positionRepository.findAllByIsClosedIsFalse(pageRequest)
                .map(positionMapper::toDto);
    }

    public Page<PositionDto> getPositions(PageRequest pageRequest) {
        return positionRepository.findAll(pageRequest)
                .map(positionMapper::toDto);
    }

    public PositionDto saveNewPosition(PositionDto positionDto) {
        Set<OrderEntity> orders = positionMapper.toEntities(positionDto.getOrders());
        PositionEntity position = positionMapper.toNewEntity(positionDto, orders);
        PositionEntity savedPosition = positionRepository.save(position);

        return positionMapper.toDto(savedPosition);
    }

    public PositionDto savePosition(PositionDto positionDto) {
        Set<OrderEntity> orders = positionMapper.toEntities(positionDto.getOrders());
        PositionEntity position = positionMapper.toEntity(positionDto, orders);
        PositionEntity savedPosition = positionRepository.save(position);

        return positionMapper.toDto(savedPosition);
    }
}
