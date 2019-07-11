package com.dfn.bamboointegration.impl.git;

import com.dfn.bamboointegration.api.Message;

import java.util.Calendar;

public class GitMessageImp implements Message {
    private String sAuthor;
    private String sCommit;
    private Calendar commitDate;

    @Override
    public String getAuthor() {
        return this.sAuthor;
    }

    @Override
    public String getCommitMessage() {
        return this.sCommit;
    }

    @Override
    public Calendar getCommitDate() {
        return this.commitDate;
    }

    @Override
    public void setAuthor(final String author) {
        this.sAuthor = author;
    }

    @Override
    public void setCommitMessage(final String commitMessage) {
        this.sCommit = commitMessage;
    }

    @Override
    public void setCommitDate(final Calendar commitDate) {
        this.commitDate = commitDate;
    }
}
