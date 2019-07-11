package com.dfn.bamboointegration.api;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.task.TaskContext;


public abstract class RepoInfoManager {
    protected TaskContext taskContext;
    protected BuildLogger buildLogger;

    public RepoInfoManager(final TaskContext taskContext) {
        this.taskContext = taskContext;
        this.buildLogger = taskContext.getBuildLogger();
    }

    abstract public CommitMessages getCommitMessageList(final String revision, final String preRevision, CommitMessages commitMessages);
}
