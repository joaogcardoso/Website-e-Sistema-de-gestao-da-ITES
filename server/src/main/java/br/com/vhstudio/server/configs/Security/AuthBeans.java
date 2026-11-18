package br.com.vhstudio.server.configs.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class AuthBeans {
    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
