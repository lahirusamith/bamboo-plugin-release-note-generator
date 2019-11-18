package com.dfn.bamboointegration.utils.DbDrivers;

import com.atlassian.bamboo.build.logger.BuildLogger;

import java.sql.*;

public abstract class DbDriver {
    protected BuildLogger logger;
    protected boolean isAvailable;

    public boolean isAvailable() {
        return isAvailable;
    }

    DbDriver(BuildLogger buildLogger) {
       this.logger = buildLogger;
    }

    public Connection getConnection(String url,String username, String password) throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    public void closeConnection(Connection conn, PreparedStatement statement, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                logger.addBuildLogEntry("Cannot close result set: " + e.getCause());
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                logger.addBuildLogEntry("Cannot close statement: " + e.getCause());
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.addBuildLogEntry("Cannot close connection: "+ e.getCause());
            }
        }
    }
}

