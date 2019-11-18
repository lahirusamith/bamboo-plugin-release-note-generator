package com.dfn.bamboointegration.impl.persistence;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.task.TaskContext;
import com.dfn.bamboointegration.api.OldLabelPersist;
import com.dfn.bamboointegration.config.PluginConstants;
import com.dfn.bamboointegration.utils.DbConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

public class OldLabelFromDB implements OldLabelPersist {
    protected TaskContext taskContext;
    protected BuildLogger buildLogger;
    protected DbConnector dbConnector;

    public OldLabelFromDB(final TaskContext taskContext) {
        this.taskContext = taskContext;
        this.buildLogger = taskContext.getBuildLogger();
        this.dbConnector = new DbConnector("MYSQL", buildLogger);
    }

    @Override
    public String getOldLabel() throws Exception {
        String oldLabel = null;
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            conn = dbConnector.getDbDriver().getConnection(PluginConstants.DB_URL, PluginConstants.DB_USER, PluginConstants.DB_PASSWORD);
            String sql = "select planId,releaseLabel,lastUpdatedDate from DFN_RELEASE_NOTE_PLUGIN_DATA where planId = ?";
            statement = conn.prepareStatement(sql);
            statement.setString(1, taskContext.getBuildContext().getTypedPlanKey().getKey());
            rs = statement.executeQuery();
            if (rs.first()) {
                oldLabel = rs.getString(2);
            } else {
                oldLabel = "INITIAL RELEASE";
                statement.close();
                rs.close();
                String sqlInsert = "insert into DFN_RELEASE_NOTE_PLUGIN_DATA (planId, project, planName, component, " +
                        "releaseLabel, lastUpdatedDate) values (?,?,?,?,?,?)";
                statement = conn.prepareStatement(sqlInsert);
                statement.setString(1, taskContext.getBuildContext().getTypedPlanKey().getKey());
                statement.setString(2, taskContext.getBuildContext().getProjectName());
                statement.setString(3, taskContext.getBuildContext().getShortName());
                statement.setString(4, null);
                statement.setString(5, "INITIAL RELEASE");
                Calendar calendar = Calendar.getInstance();
                java.sql.Date startDate = new java.sql.Date(calendar.getTime().getTime());
                statement.setDate(6, startDate);
                statement.execute();
            }
        } catch (SQLException e) {
            buildLogger.addBuildLogEntry("issue in database Connection " + e.getCause());
            for (StackTraceElement t : e.getStackTrace()) {
                buildLogger.addBuildLogEntry(t.toString());
            }
        } finally {
            dbConnector.getDbDriver().closeConnection(conn, statement, rs);
        }
        return oldLabel;
    }

    @Override
    public void setOldLabel(String oldLabel) throws Exception {
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            conn = dbConnector.getDbDriver().getConnection(PluginConstants.DB_URL, PluginConstants.DB_USER, PluginConstants.DB_PASSWORD);
            String sqlUpdate = "update DFN_RELEASE_NOTE_PLUGIN_DATA set releaseLabel = ?, lastUpdatedDate = ? where planId = ?";
            statement = conn.prepareStatement(sqlUpdate);
            statement.setString(1, oldLabel);
            Calendar calendar = Calendar.getInstance();
            java.sql.Date startDate = new java.sql.Date(calendar.getTime().getTime());
            statement.setDate(2, startDate);
            statement.setString(3, taskContext.getBuildContext().getTypedPlanKey().getKey());
            statement.executeUpdate();
        } catch (SQLException e) {
            buildLogger.addBuildLogEntry("issue in database Connection " + e.getCause());
            for (StackTraceElement t : e.getStackTrace()) {
                buildLogger.addBuildLogEntry(t.toString());
            }
        } finally {
            dbConnector.getDbDriver().closeConnection(conn, statement, rs);
        }
    }
}
