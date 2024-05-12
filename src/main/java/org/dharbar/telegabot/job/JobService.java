package org.dharbar.telegabot.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dharbar.telegabot.job.jobs.AlertPriceJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.stereotype.Service;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;
import static org.quartz.SimpleScheduleBuilder.repeatSecondlyForTotalCount;
import static org.quartz.TriggerBuilder.newTrigger;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {

    private final Scheduler scheduler;

    public void watchTargetPrice(AlertPriceJob.AlertPriceJobData alertPriceJobData, int secondsRetry) {
        long chatId = alertPriceJobData.getChatId();
        JobKey jobKey = jobKey("alert-job-USDT-" + chatId);

        removeJob(jobKey);

        JobDataMap dataMap = alertPriceJobData.toDataMap();
        JobDetail job = newJob(AlertPriceJob.class)
                .withIdentity(jobKey)
                .usingJobData(dataMap)
                .build();

        Trigger trigger = newTrigger()
                .withIdentity("trigger-alert-job-USDT-" + chatId)
                .startNow()
                .withSchedule(repeatSecondlyForTotalCount(10, secondsRetry))
                .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    public void watchEnd(Long chatId) {
        JobKey jobKey = jobKey("alert-job-USDT-" + chatId);
        removeJob(jobKey);
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
