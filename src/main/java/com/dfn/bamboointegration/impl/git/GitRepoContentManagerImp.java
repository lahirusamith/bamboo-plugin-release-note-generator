package com.dfn.bamboointegration.impl.git;

import com.atlassian.bamboo.task.TaskContext;
import com.dfn.bamboointegration.api.CommitMessages;
import com.dfn.bamboointegration.api.Message;
import com.dfn.bamboointegration.api.RepoContentManager;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class GitRepoContentManagerImp extends RepoContentManager {

    public GitRepoContentManagerImp(final TaskContext taskContext){
        super(taskContext);
    }

    @Override
    public void generateReleaseNoteTemp(final CommitMessages commitMessages) throws Exception {
        buildLogger.addBuildLogEntry("Starting reading release note file template");
        String path = taskContext.getWorkingDirectory().getPath() + "/Release_Template.docx";
        XWPFDocument doc = ReadReleaseDocument(path);
        List<XWPFTable> tables = doc.getTables();
        for (XWPFTable table : tables) {
            if(table.getText().contains("New Features Description")) {
                buildLogger.addBuildLogEntry("New feature table.");
                ArrayList<Message> newFeatures = commitMessages.getImprovements();
                populateTable(newFeatures, table);
            } else if(table.getText().contains("Features Changed Description")) {
                buildLogger.addBuildLogEntry("Was Existing Feature table.");
                ArrayList<Message> crs = commitMessages.getChangeRequests();
                populateTable(crs, table);
            } else if(table.getText().contains("Features Removed Description")) {
                buildLogger.addBuildLogEntry("Removed Feature table.");
                buildLogger.addBuildLogEntry(table.getText());
                ArrayList<Message> rmFeatures = commitMessages.getRemovedFeatures();
                populateTable(rmFeatures, table);
            } else if(table.getText().contains("Defects Fixed Description")) {
                buildLogger.addBuildLogEntry("Defect table.");
                ArrayList<Message> defects = commitMessages.getDefects();
                populateTable(defects, table);
            }
        }
        buildLogger.addBuildLogEntry("Starting writing release note generated file");
        String path1 = taskContext.getWorkingDirectory().getPath() + "/Release_Template_Generated.docx";
        try{
            OutputStream out = new FileOutputStream(path1);
            doc.write(out);
            buildLogger.addBuildLogEntry("writing file is done");
        } catch (Exception ex){
            buildLogger.addErrorLogEntry(path1);
        }
    }
}
