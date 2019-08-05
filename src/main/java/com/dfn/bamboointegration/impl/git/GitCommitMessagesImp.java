package com.dfn.bamboointegration.impl.git;

import com.dfn.bamboointegration.api.CommitMessages;
import com.dfn.bamboointegration.api.Message;

import java.util.ArrayList;

public class GitCommitMessagesImp implements CommitMessages {
    private ArrayList<String> commitMessages = new ArrayList<>();
    private ArrayList<Message> commitMessageInfo = new ArrayList<>();
    private ArrayList<Message> changeRequests = new ArrayList<>();
    private ArrayList<Message> defects = new ArrayList<>();
    private ArrayList<Message> improvements = new ArrayList<>();
    private ArrayList<Message> removedFeatures  = new ArrayList<>();
    private boolean commitMessagesAvailable = false;

    @Override
    public ArrayList<String> getCommitMessages() {
        return commitMessages;
    }

    @Override
    public ArrayList<Message> getCommitMessagesInfo() {
        return commitMessageInfo;
    }

    @Override
    public ArrayList<Message> getChangeRequests() {
        return changeRequests;
    }

    @Override
    public ArrayList<Message> getDefects() {
        return defects;
    }

    @Override
    public ArrayList<Message> getImprovements() {
        return improvements;
    }

    @Override
    public ArrayList<Message> getRemovedFeatures() {
        return removedFeatures;
    }

    @Override
    public void addCommitMessage(String cMessage) {
        this.commitMessages.add(cMessage);
    }

    @Override
    public void addCommitMessageInfo(Message messageInfo) {
        this.commitMessageInfo.add(messageInfo);
    }

    @Override
    public void addChangeRequest(Message cr) {
        this.changeRequests.add(cr);
    }

    @Override
    public void addImprovement(Message improvement) {
        this.improvements.add(improvement);
    }

    @Override
    public void addDefect(Message defect) {
        this.defects.add(defect);
    }

    @Override
    public void addRemovedFeature(Message removedFeature) {
        this.removedFeatures.add(removedFeature);
    }

    @Override
    public boolean isCommitMessagesAvailable() {
        return commitMessagesAvailable;
    }

    @Override
    public void setCommitMessagesAvailable(boolean commitMessagesAvailable) {
        this.commitMessagesAvailable = commitMessagesAvailable;
    }
}
