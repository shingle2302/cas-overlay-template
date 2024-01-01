import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.acct.AccountRegistrationRequest;
import org.apereo.cas.config.CasPalantirConfiguration;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.sql.PreparedStatement;
import java.util.HashMap;

@Slf4j
@SpringBootTest(classes = {RefreshAutoConfiguration.class, WebMvcAutoConfiguration.class, ServletWebServerFactoryAutoConfiguration.class, CasPalantirConfiguration.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties(CasConfigurationProperties.class)
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@EnableWebSecurity
public class JdbcTemplateTest {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Test
    public void update() {
        AccountRegistrationRequest request = new AccountRegistrationRequest();
        HashMap<String, Object> objectHashMap = new HashMap<>();
        objectHashMap.put("username", "jang675953@163.com");
        objectHashMap.put("password", "123456");
        objectHashMap.put("firstname", "jang");
        objectHashMap.put("lastname", "675953");
        objectHashMap.put("email", "jang675953@163.com");
        objectHashMap.put("phone", "1234567890");
        request.putProperties(objectHashMap);
        String sql = """
                    
                insert into pm_table_accounts (username, password, firstname, lastname, email, phone)
                    values (?, ?, ?, ?, ?, ?);
                   
                    """;
        KeyHolder keyHolder = new

                GeneratedKeyHolder();

        this.jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"id"});
            preparedStatement.setString(1, request.getUsername());
            preparedStatement.setString(2, request.getPassword());
            preparedStatement.setString(3, request.getFirstName());
            preparedStatement.setString(4, request.getLastName());
            preparedStatement.setString(5, request.getEmail());
            preparedStatement.setString(6, request.getPhone());
            return preparedStatement;
        }, keyHolder);
        LOGGER.error("keyHolder: {}", keyHolder.getKey().longValue());

        int updated = this.jdbcTemplate.update(sql, request.getUsername(), request.getPassword(), request.getFirstName(), request.getLastName(), request.getEmail(), request.getPhone());
        System.out.println(updated);
    }
}

