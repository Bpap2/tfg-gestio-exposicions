package cat.cccb.tfg.exposicions.importer;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@ConditionalOnProperty(name = "dump.enabled", havingValue = "true")
@Configuration
public class DumpDataSourceConfig {

    @Bean(name = "dumpDataSourceProperties")
    @ConfigurationProperties(prefix = "app.dump.datasource")
    public DataSourceProperties dumpDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "dumpDataSource")
    public DataSource dumpDataSource(
            @Qualifier("dumpDataSourceProperties") DataSourceProperties props
    ) {
        return props.initializeDataSourceBuilder().build();
    }

    @Bean(name = "dumpJdbcTemplate")
    public JdbcTemplate dumpJdbcTemplate(@Qualifier("dumpDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }
}
