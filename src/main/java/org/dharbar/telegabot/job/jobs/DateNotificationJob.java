package org.dharbar.telegabot.job.jobs;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.log4j.Log4j2;
import org.dharbar.telegabot.bot.TelegramBot;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Log4j2
@RequiredArgsConstructor
public class DateNotificationJob extends BotJob {

    public static final String ID_PARAMETER = "id";
    public static final String TRIGGER_DATE_PARAMETER = "triggerDate";
    public static final String MESSAGE_PARAMETER = "message";

    private final TelegramBot telegramBot;

    @Override
    public void execute(JobExecutionContext context) {
        DateNotificationJobData data = DateNotificationJobData.from(context.getMergedJobDataMap());

        sendMessage(data.chatId, data.message);
    }

    private void sendMessage(long chatId, String message) {
        telegramBot.sendMessage(chatId, message);
    }

    @Value
    @Builder
    public static class DateNotificationJobData {
        long chatId;
        UUID id;
        LocalDateTime triggerDate;
        String message;

        public static DateNotificationJobData from(JobDataMap jobDataMap) {
            return new DateNotificationJobData(
                    jobDataMap.getLongValue(CHAT_ID_PARAMETER),
                    UUID.fromString(jobDataMap.getString(ID_PARAMETER)),
                    LocalDateTime.parse(jobDataMap.getString(TRIGGER_DATE_PARAMETER)),
                    jobDataMap.getString(MESSAGE_PARAMETER));
        }

        public JobDataMap toDataMap() {
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put(CHAT_ID_PARAMETER, getChatId());
            jobDataMap.put(ID_PARAMETER, getId().toString());
            jobDataMap.put(TRIGGER_DATE_PARAMETER, getTriggerDate().toString());
            jobDataMap.put(MESSAGE_PARAMETER, getMessage());
            return jobDataMap;
        }
    }
}

