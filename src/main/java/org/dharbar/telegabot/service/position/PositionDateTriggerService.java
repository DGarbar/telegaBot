package org.dharbar.telegabot.service.position;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.job.DateNotificationJobService;
import org.dharbar.telegabot.job.jobs.DateNotificationJob;
import org.dharbar.telegabot.service.position.dto.DateTriggerDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PositionDateTriggerService {

    @Value("${telegram.my.chat-id}")
    Long myChatId;

    private final DateNotificationJobService dateNotificationJobService;

    @Transactional
    public void createPositionDateTrigger(UUID positionId, String positionName, Set<DateTriggerDto> dateTriggers) {
        dateTriggers.stream()
                .map(dateTriggerDto -> toData(positionName, dateTriggerDto))
                .forEach(dateNotificationJobService::scheduleNotification);
    }

    @Transactional
    public void updatePositionDateTrigger(UUID positionId, String positionName, Set<DateTriggerDto> dateTriggers, List<UUID> removedDateTriggers) {
        dateTriggers.stream()
                .map(dateTriggerDto -> toData(positionName, dateTriggerDto))
                .forEach(dateNotificationJobService::rescheduleNotification);

        removedDateTriggers.forEach(dateNotificationJobService::cancelNotification);
    }

    private DateNotificationJob.DateNotificationJobData toData(String positionName, DateTriggerDto dateTriggerDto) {
        return DateNotificationJob.DateNotificationJobData.builder()
                .chatId(myChatId)
                .id(dateTriggerDto.getId())
                .triggerDate(dateTriggerDto.getTriggerDate())
                .message(toMessage(positionName, dateTriggerDto.getComment()))
                .build();
    }

    private static String toMessage(String positionName, String comment) {
        return positionName + " : " + comment;
    }
}
