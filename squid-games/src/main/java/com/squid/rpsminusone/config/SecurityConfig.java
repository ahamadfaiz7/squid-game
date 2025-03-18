package com.squid.rpsminusone.config;

import com.squid.rpsminusone.component.JwtRequestFilter;
import com.squid.rpsminusone.util.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    @Lazy
    private UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   @Qualifier("mvcHandlerMappingIntrospector") HandlerMappingIntrospector introspector) throws Exception {
        //  Define matchers for WebSockets and REST API
        MvcRequestMatcher authMatcher = new MvcRequestMatcher(introspector, "/auth/**");
        MvcRequestMatcher gameMatcher = new MvcRequestMatcher(introspector, "/game/**");
        AntPathRequestMatcher h2ConsoleMatcher = new AntPathRequestMatcher("/h2-console/**");
        AntPathRequestMatcher wsMatcher = new AntPathRequestMatcher("/gameplay/**"); //  Allow WebSockets

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) //  Enable CORS
                .csrf(csrf -> csrf.ignoringRequestMatchers(h2ConsoleMatcher, wsMatcher).disable()) //  Disable CSRF for WebSockets
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(authMatcher).permitAll() //  Allow auth requests
                        .requestMatchers(h2ConsoleMatcher).permitAll() //  Allow H2 Console
                        .requestMatchers(wsMatcher).permitAll() //  Allow WebSocket connections
                        .requestMatchers(gameMatcher).hasAuthority("USER") //  Protect game routes
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable())) //  Allow H2 Console in iframe
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtRequestFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    //  Correctly return CorsConfigurationSource
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); //  Allows frontend
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source; //  Fix: Return CorsConfigurationSource instead of CorsFilter
    }

    @Bean
    public JwtRequestFilter jwtRequestFilter() {
        return new JwtRequestFilter(jwtUtil, userDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
