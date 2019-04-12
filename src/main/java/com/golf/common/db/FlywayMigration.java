package com.golf.common.db;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

/**
 * 数据库合并
 * @author ph
 */
public class FlywayMigration {

    private static Logger log = LoggerFactory.getLogger(FlywayMigration.class);

    private DataSource dataSource;

    public void migrate() {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        // 设置flyway扫描sql升级脚本、java升级脚本的目录路径或包路径（表示是src/main/resources/flyway下面，前缀默认为src/main/resources，因为这个路径默认在classpath下面）
        flyway.setLocations("db/migration");
        // 设置sql脚本文件的编码
        flyway.setEncoding("UTF-8");
//        flyway.setOutOfOrder(true);
        //flyway.setValidationMode(ValidationMode.ALL); // 设置执行migrate操作之前的validation行为
        //flyway.setValidationErrorMode(ValidationErrorMode.FAIL); // 设置当validation失败时的系统行为
        try {
            flyway.setBaselineOnMigrate(true);
            flyway.migrate();
        } catch (FlywayException e) {
            flyway.repair();
            log.error("数据库合并失败", e);
        }
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

}
