package com.dfn.bamboointegration.impl.git;

import com.atlassian.bamboo.task.TaskContext;
import com.dfn.bamboointegration.api.CommitMessages;
import com.dfn.bamboointegration.api.RepoContentManager;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class GitRepoContentManagerImp extends RepoContentManager {
    private boolean isPomAvailable = true;
    private boolean isJsonAvailable = true;


    public GitRepoContentManagerImp(final TaskContext taskContext) {
        super(taskContext);
    }

    @Override
    public void generateReleaseNoteTemp(final CommitMessages commitMessages) throws Exception{
        buildLogger.addBuildLogEntry("Starting reading release note file template");
        String path = taskContext.getWorkingDirectory().getPath() + "/Release_Note_Template.docx";
        XWPFDocument doc = ReadReleaseDocument(path);
        buildLogger.addBuildLogEntry("commit messages inserting to release note");
        organizeCommitMessageTables(doc, commitMessages);
        Map<String, String> fields = new HashMap<>();
        //todo: create a way to identify the project category (java, Web, etc..)
        if (isPomAvailable) {
            String pomPath = taskContext.getWorkingDirectory().getPath() + "/pom.xml";
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(new FileReader(pomPath));
            fields.put("version", model.getArtifactId() + model.getVersion());
            buildLogger.addBuildLogEntry("pom file details inserting to field map");
        }
        if (isJsonAvailable) {
            buildLogger.addBuildLogEntry("JSON file details inserting to field map");
        }
        replaceOtherDocValues(doc, fields);
        buildLogger.addBuildLogEntry("Starting writing release note generated file");
        String destinationPath = taskContext.getConfigurationMap().get("destinationFileLocation");
        if (destinationPath == null || destinationPath.isEmpty()) {
            buildLogger.addBuildLogEntry("release note destination path is not set.changing to default location");
            destinationPath = taskContext.getWorkingDirectory().getPath();
        }
        String path1 = destinationPath + "/Release_Note_" + taskContext.getConfigurationMap().get("releaseLabel") + ".docx";
        try {
            OutputStream out = new FileOutputStream(path1);
            doc.write(out);
            setOldLabel(taskContext.getConfigurationMap().get("releaseLabel"));
            buildLogger.addBuildLogEntry("writing file is done");
        } catch (Exception ex) {
            buildLogger.addErrorLogEntry(path1);
        }
    }
}