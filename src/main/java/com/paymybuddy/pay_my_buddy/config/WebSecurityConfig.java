package com.paymybuddy.pay_my_buddy.config;
import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public WebSecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> {
                    csrf.disable();
                    csrf.ignoringRequestMatchers(antMatcher("/api/user/register"));
                })
                .authorizeHttpRequests((requests) -> requests
                               // .requestMatchers(HttpMethod.POST, "/api/user/register").permitAll()
                                .requestMatchers("/", "/home", "/register", "/transaction", "/profile**", "/relation", "/css/**").permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                                .loginPage("/login")
                                .permitAll()
                                .defaultSuccessUrl("/transaction", true)
                )
                .logout((logout) -> logout.permitAll())
                .httpBasic(withDefaults());

			http
            .userDetailsService(userDetailsService);

		return http.build();
	}

	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    // @Bean
    // public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    //     return authConfig.getAuthenticationManager();
    // }

	// @Autowired
    // public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    //     auth.userDetailsService(this.userDetailsService).passwordEncoder(passwordEncoder());
    // }

	@Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // @Bean
    // public UserDetailsService userDetailsService() {
    //     return userDetailsService;
    // }

}