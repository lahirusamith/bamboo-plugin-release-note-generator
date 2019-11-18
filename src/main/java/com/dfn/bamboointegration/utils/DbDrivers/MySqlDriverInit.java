package com.dfn.bamboointegration.utils.DbDrivers;

import com.atlassian.bamboo.build.logger.BuildLogger;

public class MySqlDriverInit extends DbDriver {

    public MySqlDriverInit(BuildLogger buildLogger) {
        super(buildLogger);
        try {
            Class.forName("com.mysql.jdbc.Driver");
            isAvailable = true;
        } catch (ClassNotFoundException e) {
            logger.addBuildLogEntry("Driver class could not find.");
            isAvailable = false;
        }
    }
}