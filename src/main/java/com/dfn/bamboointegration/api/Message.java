package com.dfn.bamboointegration.api;

import java.util.Calendar;

public interface Message {
    String getAuthor();
    String getCommitMessage();
    Calendar getCommitDate();
    void setAuthor(String author);
    void setCommitMessage(String commitMessage);
    void setCommitDate(Calendar commitDate);
}
