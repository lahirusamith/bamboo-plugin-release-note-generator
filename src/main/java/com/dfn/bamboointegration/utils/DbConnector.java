package com.dfn.bamboointegration.utils;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.dfn.bamboointegration.utils.DbDrivers.DbDriver;
import com.dfn.bamboointegration.utils.DbDrivers.MySqlDriverInit;
import com.dfn.bamboointegration.utils.DbDrivers.OracleDriverInit;

public class DbConnector {
    private DbDriver dbDriver;
    private String db = "";

    public DbConnector(String driver, BuildLogger buildLogger){
        if (!db.equalsIgnoreCase(driver) && dbDriver == null) {
            switch (driver){
                case "ORACLE":
                    dbDriver = new OracleDriverInit(buildLogger);
                    break;
                default:
                    dbDriver =new MySqlDriverInit(buildLogger);
            }
        }
    }

    public DbDriver getDbDriver(){
        return dbDriver;
    }

    public boolean isAvailable(){
        return dbDriver.isAvailable();
    }
}
