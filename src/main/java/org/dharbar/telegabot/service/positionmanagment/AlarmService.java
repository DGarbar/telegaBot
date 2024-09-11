package org.dharbar.telegabot.service.positionmanagment;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.repository.AlarmRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;

    public void deleteAlarm(UUID id, UUID positionId) {
        alarmRepository.deleteByPositionIdAndId(positionId, id);
    }
}
