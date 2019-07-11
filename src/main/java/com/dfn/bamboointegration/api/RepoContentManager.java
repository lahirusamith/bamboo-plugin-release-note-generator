package com.dfn.bamboointegration.api;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.task.TaskContext;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
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

    protected void populateTable(ArrayList<Message> dataArray, XWPFTable table) {
        for(int i = 0; i < dataArray.size(); i++) {
            if (dataArray.get(i) != null) {
                Message message = dataArray.get(i);
                List<XWPFTableRow> rows = table.getRows();
                if (i == 0) {
                    rows.clear();
                }
                XWPFTableRow tableRowTwo = table.createRow();
                String[] rowData = message.getCommitMessage().split(Pattern.quote("|"));
                if (rowData != null && rowData.length > 1) {
                    tableRowTwo.getCell(0).setText(rowData[1]);
                    buildLogger.addBuildLogEntry("After casting :" + rowData[1]);
                } else {
                    buildLogger.addBuildLogEntry("rowData length is not greater than 2 :" + rowData.length);
                }
                if (rowData != null && rowData.length > 2) {
                    tableRowTwo.getCell(1).setText(rowData[2]);
                    buildLogger.addBuildLogEntry("After casting :" + rowData[2]);
                } else {
                    buildLogger.addBuildLogEntry("rowData length is not greater than 3:" + rowData.length);
                }
            }
        }
    }

    abstract public void generateReleaseNoteTemp(final CommitMessages commitMessages) throws Exception;
}
