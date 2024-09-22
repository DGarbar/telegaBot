package org.dharbar.telegabot.service.alarm;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.repository.AlarmRepository;
import org.dharbar.telegabot.repository.PositionRepository;
import org.dharbar.telegabot.repository.entity.AlarmEntity;
import org.dharbar.telegabot.repository.entity.TriggerType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

// Use only for specific cases out of position as a main entity
@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final PositionRepository positionRepository;

    // Not using bidirectional is ok?
    @Transactional
    public void createAlarm(UUID positionId, TriggerType type) {
        AlarmEntity alarmEntity = new AlarmEntity();
        alarmEntity.setPosition(positionRepository.getReferenceById(positionId));
        alarmEntity.setType(type);

        alarmRepository.save(alarmEntity);
    }
}
