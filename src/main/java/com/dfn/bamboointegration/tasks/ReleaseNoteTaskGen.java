package com.dfn.bamboointegration.tasks;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.task.*;

import com.dfn.bamboointegration.api.CommitMessages;
import com.dfn.bamboointegration.api.RepoContentManager;
import com.dfn.bamboointegration.api.RepoInfoManager;
import com.dfn.bamboointegration.impl.git.GitCommitMessagesImp;
import com.dfn.bamboointegration.impl.git.GitRepoContentManagerImp;
import com.dfn.bamboointegration.impl.git.GitRepoInfoManagerImp;
import org.jetbrains.annotations.NotNull;

public class ReleaseNoteTaskGen implements TaskType {
    @NotNull
    @Override
    public TaskResult execute(@NotNull TaskContext taskContext) throws TaskException {
        final TaskResultBuilder builder = TaskResultBuilder.newBuilder(taskContext).success();
        final BuildLogger buildLogger = taskContext.getBuildLogger();
        welcomeMessage(buildLogger);
        final String buildRevision = taskContext.getConfigurationMap().get("buildRevision");
        final String buildPreviousRevision;
        String manualBuildRevision = taskContext.getBuildContext().getVariableContext().
                getEffectiveVariables().get("oldCommitIdForReleaseNote").getValue().trim();
        if (manualBuildRevision.length() > 5) {
            buildLogger.addBuildLogEntry("Customized plan run for Release note generation detected.");
            buildPreviousRevision = manualBuildRevision;
        } else {
            buildPreviousRevision = taskContext.getConfigurationMap().get("buildPreviousRevision");
        }
        buildLogger.addBuildLogEntry("Latest commit Revision for the build : " + buildRevision);
        buildLogger.addBuildLogEntry("Previous commit Revision for the build : " + buildPreviousRevision);
        if (buildRevision.substring(0,6).equals(buildPreviousRevision.substring(0,6))) {
            buildLogger.addBuildLogEntry("Latest and previous revisions are same. release note generation omitting");
            goodbyeMessage(buildLogger);
            return builder.success().build();
        } else {
            buildLogger.addBuildLogEntry("Release note generation started");
            startGeneratingReleaseNote(taskContext, buildRevision, buildPreviousRevision, buildLogger);
        }
        goodbyeMessage(buildLogger);
        return builder.success().build();
    }

    private void startGeneratingReleaseNote(final TaskContext taskContext, final String buildRevision,
                                            final String buildPreviousRevision, final BuildLogger buildLogger) {
        buildLogger.addBuildLogEntry("Starting reading GIT repo commit messages");
        //Git Repo Manager
        RepoInfoManager gitRepoManagerImp = new GitRepoInfoManagerImp(taskContext);
        CommitMessages commitMessages = new GitCommitMessagesImp();
        gitRepoManagerImp.getCommitMessageList(buildRevision.substring(0, 6),
                buildPreviousRevision.substring(0, 6), commitMessages);
        buildLogger.addBuildLogEntry("Finished reading GIT repo commit messages");
        if (!commitMessages.isCommitMessagesAvailable()) {
            buildLogger.addBuildLogEntry("Commit Messages not available. Exit without Release Note Generation");
            return;
        }
        //Git Content Manager
        RepoContentManager gitRepoContentManagerImp = new GitRepoContentManagerImp(taskContext);
        buildLogger.addBuildLogEntry("Starting Release note file creation");
        try {
            gitRepoContentManagerImp.generateReleaseNoteTemp(commitMessages);
        } catch (Exception e) {
            buildLogger.addBuildLogEntry("creating Template stopped :" + e.getCause());
            for (StackTraceElement t : e.getStackTrace()) {
                buildLogger.addBuildLogEntry(t.toString());
            }
        }
    }

    private void welcomeMessage(BuildLogger buildLogger) {
        buildLogger.addBuildLogEntry("=============================================");
        buildLogger.addBuildLogEntry("======== DFN Release note generator =========");
        buildLogger.addBuildLogEntry("=============================================");
    }

    private void goodbyeMessage(BuildLogger buildLogger) {
        buildLogger.addBuildLogEntry("=============================================");
        buildLogger.addBuildLogEntry("==== Thank you for using DFN release note ===");
        buildLogger.addBuildLogEntry("=============================================");
    }
}
