package org.apereo.cas.config;

import com.google.common.base.Throwables;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.acct.AccountRegistrationRequest;
import org.apereo.cas.acct.AccountRegistrationResponse;
import org.apereo.cas.acct.provision.AccountRegistrationProvisioner;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class CustomAccountRegistrationProvisioner implements AccountRegistrationProvisioner {

    private JdbcTemplate jdbcTemplate;

    @Override
    public AccountRegistrationResponse provision(AccountRegistrationRequest request) throws Throwable {
        try {
            String sql = """
                        
                    insert into pm_table_accounts (username, password, firstname, lastname, email, phone)
                        values (?, ?, ?, ?, ?, ?);
                       
                        """;
            KeyHolder keyHolder = new GeneratedKeyHolder();

            int updated = this.jdbcTemplate.update(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"id"});
                preparedStatement.setString(1, request.getUsername());
                preparedStatement.setString(2, request.getPassword());
                preparedStatement.setString(3, request.getFirstName());
                preparedStatement.setString(4, request.getLastName());
                preparedStatement.setString(5, request.getEmail());
                preparedStatement.setString(6, request.getPhone());
                return preparedStatement;
            }, keyHolder);
            LOGGER.debug("keyHolder: {}", keyHolder.getKey().longValue());
            String securityQuestionSql = """
                    insert into pm_table_questions (username, question, answer)
                        values (?, ?, ?);
                       
                        """;
            int[] questionUpdated = this.jdbcTemplate.batchUpdate(securityQuestionSql,new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setString(1, request.getUsername());
                    if (i==0){
                        ps.setString(2, request.getProperty("securityquestion1",String.class));
                        ps.setString(3, request.getProperty("securityanswer1",String.class));
                    }else {
                        ps.setString(2, request.getProperty("securityquestion2",String.class));
                        ps.setString(3, request.getProperty("securityanswer2",String.class));
                    }
                }

                @Override
                public int getBatchSize() {
                    return 2;
                }
            });
            if (updated == 0|| questionUpdated.length == 0) {
                return getAccountRegistrationFailureResponse();
            }
        } catch (Exception e) {
            LOGGER.error("Exception: {}", Throwables.getStackTraceAsString(e));
            return getAccountRegistrationFailureResponse();
        }
        AccountRegistrationResponse accountRegistrationResponse = AccountRegistrationResponse.success();
        accountRegistrationResponse.putProperties(request.asMap());
        return accountRegistrationResponse;
    }

    private static AccountRegistrationResponse getAccountRegistrationFailureResponse() {
        AccountRegistrationResponse failure = AccountRegistrationResponse.failure();
        failure.putProperty("message", "Account registration failure.");
        return failure;
    }
}
