package org.dharbar.telegabot.job.jobs;

import org.quartz.Job;

public abstract class BotJob implements Job {

    public static final String CHAT_ID_PARAMETER = "chatId";
}
