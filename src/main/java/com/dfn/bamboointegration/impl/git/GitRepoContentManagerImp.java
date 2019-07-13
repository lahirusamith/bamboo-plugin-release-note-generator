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

public class GitRepoContentManagerImp extends RepoContentManager {

    public GitRepoContentManagerImp(final TaskContext taskContext) {
        super(taskContext);
    }

    @Override
    public void generateReleaseNoteTemp(final CommitMessages commitMessages) throws Exception {
        buildLogger.addBuildLogEntry("Starting reading release note file template");
        String path = taskContext.getWorkingDirectory().getPath() + "/Release_Template.docx";
        XWPFDocument doc = ReadReleaseDocument(path);
        buildLogger.addBuildLogEntry("commit messages inserting to release note");
        organizeCommitMessageTables(doc, commitMessages);
        String pomPath = taskContext.getWorkingDirectory().getPath() + "/pom.xml";
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(new FileReader(pomPath));
        buildLogger.addBuildLogEntry("pom file details inserting to release note");
        replaceDocValuesFromPomFile(doc, model);
        buildLogger.addBuildLogEntry("Starting writing release note generated file");
        String path1 = taskContext.getWorkingDirectory().getPath() + "/Release_Template_Generated.docx";
        try {
            OutputStream out = new FileOutputStream(path1);
            doc.write(out);
            buildLogger.addBuildLogEntry("writing file is done");
        } catch (Exception ex) {
            buildLogger.addErrorLogEntry(path1);
        }
    }
}
