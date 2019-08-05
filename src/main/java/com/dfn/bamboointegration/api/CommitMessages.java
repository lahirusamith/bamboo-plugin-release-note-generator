package com.dfn.bamboointegration.api;

import java.util.ArrayList;

public interface CommitMessages {
    ArrayList<String> getCommitMessages();
    ArrayList<Message> getCommitMessagesInfo();
    ArrayList<Message> getChangeRequests();
    ArrayList<Message> getDefects();
    ArrayList<Message> getImprovements();
    ArrayList<Message> getRemovedFeatures();
    boolean isCommitMessagesAvailable();
    void addCommitMessage(String cMessage);
    void addCommitMessageInfo(Message messageInfo);
    void addChangeRequest(Message cr);
    void addImprovement(Message improvement);
    void addDefect(Message defect);
    void addRemovedFeature(Message removedFeature);
    void setCommitMessagesAvailable(boolean commitMessagesAvailable);
}