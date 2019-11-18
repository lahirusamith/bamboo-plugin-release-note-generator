package com.dfn.bamboointegration.utils.DbDrivers;

import com.atlassian.bamboo.build.logger.BuildLogger;

public class OracleDriverInit extends DbDriver {
    public OracleDriverInit(BuildLogger buildLogger) {
        super(buildLogger);
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            isAvailable = true;
        } catch (ClassNotFoundException e) {
            logger.addBuildLogEntry("Driver class could not find.");
            isAvailable = false;
        }
    }
}
