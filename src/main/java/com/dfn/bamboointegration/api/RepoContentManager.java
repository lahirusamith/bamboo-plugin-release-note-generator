package com.dfn.bamboointegration.api;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.task.TaskContext;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.maven.model.Model;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public abstract class RepoContentManager {
    protected TaskContext taskContext;
    protected BuildLogger buildLogger;

    public RepoContentManager(final TaskContext taskContext) {
        this.taskContext = taskContext;
        this.buildLogger = taskContext.getBuildLogger();
    }

    protected XWPFDocument ReadReleaseDocument(String path) throws IOException {
        this.buildLogger.addBuildLogEntry(path);
        FileInputStream fileInputStream = new FileInputStream(path);
        return new XWPFDocument(fileInputStream);
    }

    protected void organizeCommitMessageTables(XWPFDocument document, CommitMessages commitMessages) {
        List<XWPFTable> tables = document.getTables();
        for (XWPFTable table : tables) {
            buildLogger.addBuildLogEntry(table.getRow(0).getCell(1).getText());
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
        for (int i = 0; i < dataArray.size(); i++) {
            if (dataArray.get(i) != null) {
                Message message = dataArray.get(i);
                if (i == 0) {
                    table.removeRow(1);
                }
                XWPFTableRow tableRowTwo = table.createRow();
                String[] rowData = message.getCommitMessage().split(Pattern.quote("|"));
                if (rowData.length == 2) {
                    tableRowTwo.getCell(0).setText(rowData[1]);
                }
                if (rowData.length > 2) {
                    tableRowTwo.getCell(0).setText(rowData[1]);
                    tableRowTwo.getCell(1).setText(rowData[2]);
                }
            }
        }
    }

    protected void replaceDocValuesFromPomFile(XWPFDocument document, Model model) {
        for (XWPFParagraph p : document.getParagraphs()) {
            List<XWPFRun> runs = p.getRuns();
            if (runs != null) {
                for (XWPFRun r : runs) {
                    String text = r.getText(0);
                    if (text != null) {
                        switch (text) {
                            case "RN_DOC_TITLE":
                                text = text.replace("RN_DOC_TITLE", model.getArtifactId() +
                                        model.getVersion());
                                r.setText(text, 0);
                                break;
                            case "RN_RC_LABEL":
                                text = text.replace("RN_RC_LABEL", model.getId());
                                r.setText(text, 0);
                                break;
                            case "RN_RC_LASTLABEL":
                                break;
                            default:
                        }
                    }
                }
            }
        }
        List<XWPFTable> tables = document.getTables();
        for (XWPFTable table : tables) {
            buildLogger.addBuildLogEntry(table.getRow(0).getCell(1).getText());
            switch (table.getRow(0).getCell(1).getText()) {
                case "Version":
                    table.getRow(1).getCell(0).removeParagraph(0);
                    table.getRow(1).getCell(1).removeParagraph(0);
                    table.getRow(1).getCell(0).setText(new Date().toString());
                    table.getRow(1).getCell(1).setText(model.getId());
                    break;
                case "Baseline":
                    break;
                default:
            }
        }
    }

    abstract public void generateReleaseNoteTemp(final CommitMessages commitMessages) throws Exception;
}
