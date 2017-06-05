package dk.ralu.k8s.fragments.k8s.fragments.restapi;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DatabaseEndpoint {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping("/useDataSource")
    public String useDataSource() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            return metaData.getDatabaseProductName() + " v. " + metaData.getDatabaseProductVersion();
        }
    }

    @RequestMapping("/useJdbcTemplate")
    public List<String> useJdbcTemplate() throws SQLException {
        return jdbcTemplate.queryForList("SELECT first_name FROM PERSON", String.class);
    }
}
