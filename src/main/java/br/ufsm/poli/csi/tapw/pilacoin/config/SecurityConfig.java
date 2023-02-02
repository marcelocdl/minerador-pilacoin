package br.ufsm.poli.csi.tapw.pilacoin.config;

import br.ufsm.poli.csi.tapw.pilacoin.security.JwtConfigurer;
import br.ufsm.poli.csi.tapw.pilacoin.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	@Autowired
	private JwtTokenProvider tokenProvider;

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		return bCryptPasswordEncoder;
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and()
				.csrf().disable()
				.httpBasic().and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
				.and()
				.authorizeRequests()
				.antMatchers("/auth/login").permitAll()
				.antMatchers("/api/**").authenticated()
				.and()
				.apply(new JwtConfigurer(tokenProvider));

	}

}
