package org.dharbar.telegabot.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dharbar.telegabot.job.jobs.DateNotificationJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;
import static org.quartz.TriggerBuilder.newTrigger;

// Simple service that will send the notification to the user when the date comes.
@Slf4j
@Service
@RequiredArgsConstructor
public class DateNotificationJobService {

    private static final String JOB_KEY_PREFIX = "date-notification-job-";

    private final Scheduler scheduler;

    public void scheduleNotification(DateNotificationJob.DateNotificationJobData dateNotificationJobData) {
        JobKey jobKey = toJobKey(dateNotificationJobData.getId());

        JobDataMap dataMap = dateNotificationJobData.toDataMap();
        JobDetail job = newJob(DateNotificationJob.class)
                .withIdentity(jobKey)
                .usingJobData(dataMap)
                .build();

        Trigger trigger = newTrigger()
                .withIdentity("trigger-date-notification-job-" + dateNotificationJobData.getId())
                .startAt(Date.from(dateNotificationJobData.getTriggerDate().atZone(ZoneId.systemDefault()).toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    public void rescheduleNotification(DateNotificationJob.DateNotificationJobData dateNotificationJobData) {
        JobKey jobKey = toJobKey(dateNotificationJobData.getId());
        removeJob(jobKey);
        scheduleNotification(dateNotificationJobData);
    }

    public void cancelNotification(UUID id) {
        JobKey jobKey = toJobKey(id);
        removeJob(jobKey);
    }

    private static JobKey toJobKey(UUID id) {
        return jobKey(JOB_KEY_PREFIX + id);
    }

    private void removeJob(JobKey jobKey) {
        try {
            boolean result = scheduler.deleteJob(jobKey);
            if (result) log.info("Job deleted: {}", jobKey);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }
}
