package com.dfn.bamboointegration.api;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.vcs.configuration.PlanRepositoryDefinition;
import com.dfn.bamboointegration.impl.persistence.OldLabelFromDB;
import org.apache.poi.xwpf.usermodel.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.Date;
import java.util.regex.Pattern;

public abstract class ReleaseContentManager {
    protected TaskContext taskContext;
    protected BuildLogger buildLogger;
    protected OldLabelPersist oldLabelPersist;

    public ReleaseContentManager(final TaskContext taskContext) {
        this.taskContext = taskContext;
        this.buildLogger = taskContext.getBuildLogger();
        this.oldLabelPersist = new OldLabelFromDB(taskContext);
    }

    protected XWPFDocument ReadReleaseDocument(String path) throws IOException {
        this.buildLogger.addBuildLogEntry(path);
        FileInputStream fileInputStream = new FileInputStream(path);
        return new XWPFDocument(fileInputStream);
    }

    protected void organizeCommitMessageTables(XWPFDocument document, CommitMessages commitMessages) {
        List<XWPFTable> tables = document.getTables();
        for (XWPFTable table : tables) {
            switch (table.getRow(0).getCell(1).getText()) {
                case "New Features Description":
                    ArrayList<Message> newFeatures = commitMessages.getImprovements();
                    populateTable(newFeatures, table);
                    break;
                case "Features Changed Description":
                    ArrayList<Message> crs = commitMessages.getChangeRequests();
                    populateTable(crs, table);
                    break;
                case "Features Removed Description":
                    ArrayList<Message> rmFeatures = commitMessages.getRemovedFeatures();
                    populateTable(rmFeatures, table);
                    break;
                case "Defects Fixed Description":
                    ArrayList<Message> defects = commitMessages.getDefects();
                    populateTable(defects, table);
                    break;
                default:
            }
        }
    }

    private void populateTable(ArrayList<Message> dataArray, XWPFTable table) {
        Map<String, Boolean> refId = new HashMap<>();
        for (int i = 0; i < dataArray.size(); i++) {
            if (dataArray.get(i) != null) {
                Message message = dataArray.get(i);
                if (i == 0) {
                    table.removeRow(1);
                }
                String[] rowData = message.getCommitMessage().split(Pattern.quote("|"));
                if (rowData.length == 1 || (refId.containsKey(rowData[1].trim()) && refId.get(rowData[1].trim()))) {
                    continue;
                }
                XWPFTableRow tableRowTwo = table.createRow();
                if (rowData.length == 2) {
                    tableRowTwo.getCell(0).setText(rowData[1]);
                }
                if (rowData.length > 2) {
                    tableRowTwo.getCell(0).setText(rowData[1]);
                    tableRowTwo.getCell(1).setText(rowData[2]);
                }
                refId.put(rowData[1].trim(), rowData[1].trim().length() > 1);
            }
        }
    }

    protected void replaceOtherDocValues(XWPFDocument document, Map<String, String> fields) throws Exception {
        String newLabel = taskContext.getConfigurationMap().get("releaseLabel");
        /*String oldLabel = getOldLabel(taskContext.getConfigurationMap().get("dbUrl"),
                taskContext.getConfigurationMap().get("dbUser"),
                taskContext.getConfigurationMap().get("dbPassword"));*/
        String oldLabel = oldLabelPersist.getOldLabel();
        Iterator<XWPFParagraph> itr = document.getParagraphsIterator();
        while (itr.hasNext()) {
            XWPFParagraph temp = itr.next();
            buildLogger.addBuildLogEntry(":" + temp.getParagraphText() + ":");
        }
        for (XWPFParagraph p : document.getParagraphs()) {
            List<XWPFRun> runs = p.getRuns();
            if (runs != null) {
                for (XWPFRun r : runs) {
                    String text = r.getText(0);
                    if (text != null) {
                        switch (text) {
                            case "RN_DOC_TITLE":
                                text = text.replace("RN_DOC_TITLE", newLabel);
                                r.setText(text, 0);
                                break;
                            case "RN_RC_LABEL":
                                text = text.replace("RN_RC_LABEL", newLabel);
                                r.setText(text, 0);
                                break;
                            case "RN_RC_LASTLABEL":
                                text = text.replace("RN_RC_LASTLABEL", oldLabel == null ? "" : oldLabel);
                                r.setText(text, 0);
                                break;
                            default:
                        }
                    }
                }
            }
        }
        List<XWPFTable> tables = document.getTables();
        PlanRepositoryDefinition repoInfo = taskContext.getBuildContext().getVcsRepositories().get(0);
        for (XWPFTable table : tables) {
            switch (table.getRow(0).getCell(1).getText()) {
                case "Version":
                    table.getRow(1).getCell(0).removeParagraph(0);
                    table.getRow(1).getCell(1).removeParagraph(0);
                    table.getRow(1).getCell(0).setText(new Date().toString());
                    table.getRow(1).getCell(1).setText(fields.get("version"));
                    break;
                case "Baseline":
                    table.getRow(1).getCell(0).removeParagraph(0);
                    table.getRow(1).getCell(2).removeParagraph(0);
                    table.getRow(1).getCell(0).setText(taskContext.getBuildContext().getPlanName());
                    table.getRow(1).getCell(2).setText(Objects.requireNonNull(repoInfo.getBranch()).
                            getVcsBranch().getName());
                    break;
                default:
            }
        }
    }

    abstract public void generateReleaseNoteTemp(final CommitMessages commitMessages) throws Exception;
}
