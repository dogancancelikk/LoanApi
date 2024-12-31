package com.example.creditmodule;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class DBUnitConfig {

    @Bean
    public DatabaseDataSourceConnection dbUnitDatabaseConnection(DataSource dataSource) throws SQLException {
        DatabaseDataSourceConnection connection = new DatabaseDataSourceConnection(dataSource);
        DatabaseConfig dbConfig = connection.getConfig();
        dbConfig.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new H2DataTypeFactory());
        dbConfig.setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);
        return connection;
    }
}
