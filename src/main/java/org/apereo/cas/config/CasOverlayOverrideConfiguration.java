package org.apereo.cas.config;


import org.apereo.cas.acct.provision.AccountRegistrationProvisionerConfigurer;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;



@AutoConfiguration
@EnableConfigurationProperties(CasConfigurationProperties.class)
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@EnableWebSecurity
public class CasOverlayOverrideConfiguration {

    /*
    @Bean
    public MyCustomBean myCustomBean() {
        ...
    }
     */


    @Bean
    public AccountRegistrationProvisionerConfigurer customProvisioningConfigurer(JdbcTemplate jdbcTemplate) {
        return () -> new CustomAccountRegistrationProvisioner(jdbcTemplate);
    }


}
