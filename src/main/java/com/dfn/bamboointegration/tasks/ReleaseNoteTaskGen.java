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
                getEffectiveVariables().get("preRevision").getValue();
        if (manualBuildRevision.length() > 5) {
            buildLogger.addBuildLogEntry("Customized plan run detected. changing previous build revision");
            buildPreviousRevision = manualBuildRevision;
        } else {
            buildPreviousRevision = taskContext.getConfigurationMap().get("buildPreviousRevision");
        }
        buildLogger.addBuildLogEntry("Build Revision : " + buildRevision);
        buildLogger.addBuildLogEntry("Previous Build Revision : " + buildPreviousRevision);
        if (buildRevision.equals(buildPreviousRevision)) {
            buildLogger.addBuildLogEntry("build revisions are same. release note generation omitting");
            goodbyeMessage(buildLogger);
            return builder.success().build();
        } else {
            buildLogger.addBuildLogEntry("Starting generating release note");
            startGeneratingReleaseNote(taskContext, buildRevision, buildPreviousRevision, buildLogger);
        }
        goodbyeMessage(buildLogger);
        return builder.success().build();
    }

    private void startGeneratingReleaseNote(final TaskContext taskContext, final String buildRevision,
                                            final String buildPreviousRevision, final BuildLogger buildLogger) {
        buildLogger.addBuildLogEntry("Starting reading GIT repo commit messages");
        RepoInfoManager gitRepoManagerImp = new GitRepoInfoManagerImp(taskContext);
        CommitMessages commitMessages = new GitCommitMessagesImp();
        gitRepoManagerImp.getCommitMessageList(buildRevision.substring(0, 6),
                buildPreviousRevision.substring(0, 6), commitMessages);
        buildLogger.addBuildLogEntry("Finished reading GIT repo commit messages");
        RepoContentManager gitRepoContentManagerImp = new GitRepoContentManagerImp(taskContext);
        buildLogger.addBuildLogEntry("Starting Release note file creation");
        try {
            gitRepoContentManagerImp.generateReleaseNoteTemp(commitMessages);
        } catch (Exception e) {
            buildLogger.addBuildLogEntry("creating Template stopped :" + e);
        }
    }

    private void welcomeMessage(BuildLogger buildLogger){
        buildLogger.addBuildLogEntry("=============================================");
        buildLogger.addBuildLogEntry("======== DFN Release note generator =========");
        buildLogger.addBuildLogEntry("=============================================");
    }

    private void goodbyeMessage(BuildLogger buildLogger){
        buildLogger.addBuildLogEntry("=============================================");
        buildLogger.addBuildLogEntry("==== Thank you for using DFN release note ===");
        buildLogger.addBuildLogEntry("=============================================");
    }
}
