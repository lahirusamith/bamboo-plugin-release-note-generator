package com.dfn.bamboointegration.api;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.task.TaskContext;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;


public abstract class RepoCommitManager {
    protected TaskContext taskContext;
    protected BuildLogger buildLogger;
    protected Repository repo;

    public RepoCommitManager(final TaskContext taskContext) {
        this.taskContext = taskContext;
        this.buildLogger = taskContext.getBuildLogger();
    }

    abstract public CommitMessages getCommitMessageList(final String revision, final String preRevision,
                                                        CommitMessages commitMessages, final boolean tagReceived);

    protected ObjectId getActualRefObjectId(Ref ref) {
        final Ref repoPeeled = repo.peel(ref);
        if(repoPeeled.getPeeledObjectId() != null) {
            return repoPeeled.getPeeledObjectId();
        }
        return ref.getObjectId();
    }

}
