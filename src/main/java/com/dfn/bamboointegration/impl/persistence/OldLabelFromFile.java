package com.dfn.bamboointegration.impl.persistence;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.task.TaskContext;
import com.dfn.bamboointegration.api.OldLabelPersist;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class OldLabelFromFile implements OldLabelPersist {
    protected TaskContext taskContext;
    protected BuildLogger buildLogger;

    public OldLabelFromFile(final TaskContext taskContext) {
        this.taskContext = taskContext;
        this.buildLogger = taskContext.getBuildLogger();
    }

    @Override
    public String getOldLabel() throws Exception {
        String oldLabel = null;
        Properties props = new Properties();
        try {
            FileInputStream in = new FileInputStream(taskContext.getWorkingDirectory().getPath() + "/bamboo.properties");
            props.load(in);
            oldLabel = props.getProperty("old_rel_ver");
            in.close();
        } catch (FileNotFoundException e) {
            FileOutputStream out = new FileOutputStream(taskContext.getWorkingDirectory().getPath() + "/bamboo.properties");
            props.setProperty("old_rel_ver", "");
            props.store(out, null);
            out.close();
        } catch (IOException e) {
            buildLogger.addBuildLogEntry("IO Exception. sending null");
            oldLabel = null;
        }
        return oldLabel;
    }

    @Override
    public void setOldLabel(String oldLabel) throws Exception {
        Properties props = new Properties();
        FileOutputStream out = new FileOutputStream(taskContext.getWorkingDirectory().getPath() + "/bamboo.properties");
        props.setProperty("old_rel_ver", oldLabel);
        props.store(out, null);
        out.close();
    }
}
