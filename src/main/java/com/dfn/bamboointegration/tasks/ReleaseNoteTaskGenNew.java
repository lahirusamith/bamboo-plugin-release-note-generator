package com.dfn.bamboointegration.tasks;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.task.*;
import com.dfn.bamboointegration.api.CommitMessages;
import com.dfn.bamboointegration.api.ReleaseContentManager;
import com.dfn.bamboointegration.api.RepoCommitManager;
import com.dfn.bamboointegration.impl.git.GitCommitMessagesImp;
import com.dfn.bamboointegration.impl.git.GitReleaseContentManagerImp;
import com.dfn.bamboointegration.impl.git.GitRepoCommitManagerImp;
import org.jetbrains.annotations.NotNull;

public class ReleaseNoteTaskGenNew implements TaskType {
    @NotNull
    @Override
    public TaskResult execute(@NotNull TaskContext taskContext) throws TaskException {
        final TaskResultBuilder builder = TaskResultBuilder.newBuilder(taskContext).success();
        final BuildLogger buildLogger = taskContext.getBuildLogger();
        boolean tagReceived = false;
        welcomeMessage(buildLogger);
        final String buildRevision = taskContext.getConfigurationMap().get("buildRevision");
        final String buildPreviousRevision;
        String commitId = taskContext.getBuildContext().getVariableContext().
                getEffectiveVariables().get("oldCommitIdForReleaseNote").getValue().trim();
        String tag = taskContext.getBuildContext().getVariableContext().
                getEffectiveVariables().get("oldTagForReleaseNote").getValue().trim();
        if (commitId.length() > 6 || tag.length() > 6) {
            buildLogger.addBuildLogEntry("Customized plan run for Release note generation detected.");
            if(commitId.isEmpty()) {
                tagReceived = true;
                buildPreviousRevision = tag;
            } else if(tag.isEmpty()){
                buildPreviousRevision = commitId.substring(0, 6) + "^";
            } else {
                buildPreviousRevision = taskContext.getConfigurationMap().get("buildPreviousRevision");
            }
        } else {
            buildPreviousRevision = taskContext.getConfigurationMap().get("buildPreviousRevision");
        }
        buildLogger.addBuildLogEntry("Latest commitId/tag for the build : " + buildRevision);
        buildLogger.addBuildLogEntry("Previous commitId/tag for the build : " + buildPreviousRevision);
        if (buildRevision.substring(0, 6).equals(buildPreviousRevision.substring(0, 6))
                || buildRevision.equals(buildPreviousRevision)) {
            buildLogger.addBuildLogEntry("Latest and previous commits/tags are same. release note generation omitting");
            goodbyeMessage(buildLogger);
            return builder.success().build();
        } else {
            buildLogger.addBuildLogEntry("Release note generation started");
            startGeneratingReleaseNote(taskContext, buildRevision, buildPreviousRevision, buildLogger,tagReceived);
        }
        goodbyeMessage(buildLogger);
        return builder.success().build();
    }

    private void startGeneratingReleaseNote(final TaskContext taskContext, final String buildRevision,
                                            final String buildPreviousRevision, final BuildLogger buildLogger,
                                            final boolean tagReceived) {
        buildLogger.addBuildLogEntry("Starting reading GIT repo commit messages");
        //Git Repo Manager
        RepoCommitManager gitRepoManagerImp = new GitRepoCommitManagerImp(taskContext);
        CommitMessages commitMessages = new GitCommitMessagesImp();
        /*gitRepoManagerImp.getCommitMessageList(buildRevision.substring(0, 7),
                buildPreviousRevision.substring(0, 7), commitMessages);*/
        gitRepoManagerImp.getCommitMessageList(buildRevision, buildPreviousRevision, commitMessages, tagReceived);
        buildLogger.addBuildLogEntry("Finished reading GIT repo commit messages");
        if (!commitMessages.isCommitMessagesAvailable()) {
            buildLogger.addBuildLogEntry("Commit Messages not available. Exit without Release Note Generation");
            return;
        }
        //Git Content Manager
        ReleaseContentManager gitReleaseContentManagerImp = new GitReleaseContentManagerImp(taskContext);
        buildLogger.addBuildLogEntry("Starting Release note file creation");
        try {
            gitReleaseContentManagerImp.generateReleaseNoteTemp(commitMessages);
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
