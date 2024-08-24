package com.litvin.exchange.inbound.configuration.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@Configuration
class LocalSecurityConfig {
    @Bean
    fun userDetailsService(): UserDetailsService {
        val user =
            User
                .withDefaultPasswordEncoder()
                .username("admin")
                .password("password")
                .roles("ADMIN")
                .build()
        return InMemoryUserDetailsManager(user)
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { requestMatcherRegistry ->
                requestMatcherRegistry
                    // Admin endpoints
                    .requestMatchers("/fee/**")
                    .hasRole("ADMIN")
                    // Swagger
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**")
                    .permitAll()
                    // Public endpoints
                    .anyRequest()
                    .permitAll()
            }.formLogin { }
            .httpBasic {}
            .csrf { it.disable() }
        return http.build()
    }
}
