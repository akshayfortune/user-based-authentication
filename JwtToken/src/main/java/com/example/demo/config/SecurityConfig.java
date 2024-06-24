package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.demo.service.UserService;

import jakarta.servlet.Filter;

@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Autowired
	UserService userService;

	@Bean
	 BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	 DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
		auth.setUserDetailsService(userService);
		auth.setPasswordEncoder(passwordEncoder());
		return auth;
	}

	@Bean
	 AuthenticationManager authenticationManager(AuthenticationConfiguration autheticationConfiguration)
			throws Exception {
		return autheticationConfiguration.getAuthenticationManager();
	}

	@Bean
	 SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

//		http.cors().and().csrf().disable().authorizeHttpRequests().requestMatchers("/registration", "/genToken")
//				.permitAll().anyRequest().authenticated().and().sessionManagement()
//				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//		http.addFilterBefore((Filter) authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
		
		http.csrf(csrf -> csrf.disable())
		.authorizeHttpRequests(auth -> auth
				.requestMatchers("/registration", "/login").permitAll()
				.anyRequest().authenticated())
		.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//		.oauth2ResourceServer(OAuth2ResourceServerConfigurer :: disable)
//		.httpBasic(Customizer.withDefaults());
		http.addFilterBefore(authenticationTokenFilterBean(),UsernamePasswordAuthenticationFilter.class);
		
		return http.build();

	}

	@Bean
	 JwtFilter authenticationTokenFilterBean() throws Exception {
		return new JwtFilter();
	}

}
