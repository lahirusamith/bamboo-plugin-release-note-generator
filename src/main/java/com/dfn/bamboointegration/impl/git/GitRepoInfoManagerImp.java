package com.dfn.bamboointegration.impl.git;

import com.atlassian.bamboo.task.TaskContext;
import com.dfn.bamboointegration.api.CommitMessages;
import com.dfn.bamboointegration.api.Message;
import com.dfn.bamboointegration.api.RepoInfoManager;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.util.Calendar;
import java.util.Iterator;
import java.util.regex.Pattern;

public class GitRepoInfoManagerImp extends RepoInfoManager {
    private Repository repo;

    public GitRepoInfoManagerImp(final TaskContext taskContext) {
        super(taskContext);
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        String path = this.taskContext.getWorkingDirectory().getPath() + "/.git";
        buildLogger.addBuildLogEntry("accessing git repo local path : " + path);
        File newFile = new File(path);
        try {
            this.repo = repositoryBuilder.setGitDir(newFile).readEnvironment().findGitDir().setMustExist(true).build();
        } catch (Exception e) {
            buildLogger.addErrorLogEntry("GIT Access error :" + e.getMessage() + " _ " + e.getCause());
        }
    }

    public CommitMessages getCommitMessageList(String revision, String preRevision, CommitMessages commitMessages) {
        if (this.repo != null) {
            try {
                Git git = new Git(this.repo);
                Iterable<RevCommit> log = git.log().addRange(this.repo.resolve(preRevision),
                        this.repo.resolve(revision)).call();
                Message message;
                for (Iterator<RevCommit> iterator = log.iterator(); iterator.hasNext(); ) {
                    RevCommit rev = iterator.next();
                    String sComment = rev.getFullMessage();
                    buildLogger.addBuildLogEntry("commit Message : " + sComment);
                    commitMessages.addCommitMessage(sComment);
                    message = new GitMessageImp();
                    message.setAuthor(rev.getAuthorIdent().getName());
                    message.setCommitMessage(sComment);
                    message.setCommitDate(new Calendar.Builder().setInstant((long) rev.getCommitTime() * 1000).build());
                    commitMessages.addCommitMessageInfo(message);
                    if (sComment != null && !sComment.isEmpty()) {
                        String[] commitSplit = sComment.split(Pattern.quote("|"));
                        if (commitSplit.length > 1) {
                            String code = commitSplit[0].trim().toUpperCase();
                            switch (code) {
                                case "BUG":
                                    commitMessages.addDefect(message);
                                    buildLogger.addBuildLogEntry("Bug fix identified :" + sComment);
                                    break;
                                case "CHG":
                                case "CHNG":
                                    commitMessages.addChangeRequest(message);
                                    buildLogger.addBuildLogEntry("Change in code identified :" + sComment);
                                    break;
                                case "NEW":
                                    commitMessages.addImprovement(message);
                                    buildLogger.addBuildLogEntry("New Feature identified :" + sComment);
                                    break;
                                case "REM":
                                    commitMessages.addRemovedFeature(message);
                                    buildLogger.addBuildLogEntry("Remove in code identified :" + sComment);
                                    break;
                                default:
                                    buildLogger.addBuildLogEntry("Unidentified commit type detected");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                buildLogger.addBuildLogEntry("Error when getting comments :" + e.getMessage() +
                        " _ " + e.getCause());
            }
        } else {
            buildLogger.addBuildLogEntry("Git repo access error. repo is null.");
        }
        return commitMessages;
    }
}
