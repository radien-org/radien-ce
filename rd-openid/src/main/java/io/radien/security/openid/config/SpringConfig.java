package io.radien.security.openid.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:${spring.profiles.active:application}.properties")
@ComponentScans({ @ComponentScan("org.openappframe.security.oidc.rp.service") })
public class SpringConfig {
}
