package ua.foxminded.universitycms;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import ua.foxminded.universitycms.handler.RoleBasedAuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.formLogin(
        form -> form.loginPage("/login").successHandler(new RoleBasedAuthenticationSuccessHandler()).permitAll())
        .authorizeHttpRequests(
            (authorizeHttpRequests) -> authorizeHttpRequests
                .requestMatchers("/admin/**").hasRole("ADMINISTRATOR")
                .requestMatchers("/teacher/**").hasRole("TEACHER")
                .requestMatchers("/student/**").hasRole("STUDENT")
                .requestMatchers("/*", "/js/**", "/css/**").permitAll());

    return http.build();
  }
  
  @Bean
  public PasswordEncoder passwordEncoder() {
    Map<String, PasswordEncoder> encoders = new HashMap<>();
    encoders.put("bcrypt", new BCryptPasswordEncoder());
    
    return new DelegatingPasswordEncoder("bcrypt", encoders);
  }
}
