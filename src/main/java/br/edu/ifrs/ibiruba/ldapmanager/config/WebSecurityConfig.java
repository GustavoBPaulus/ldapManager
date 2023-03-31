package br.edu.ifrs.ibiruba.ldapmanager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import br.edu.ifrs.ibiruba.ldapmanager.services.UserDetailsServiceImpl;
import br.edu.ifrs.ibiruba.ldapmanager.useful.CriptografiaUtil;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
        	//.antMatchers("/**").permitAll()
            .antMatchers("/adminlte/**").permitAll()
            .antMatchers("/img/**").permitAll()
            .antMatchers("/js/**").permitAll()
            .antMatchers("/css/**").permitAll()
            .antMatchers("/plugins/**").permitAll()
            .antMatchers("/altermypassword/**").permitAll()
            .antMatchers("**/userpasswordmanager/changepassword").permitAll()
            .antMatchers("**/userpasswordmanager/forgot-password").permitAll()
            .antMatchers("**/userpasswordmanager/**").permitAll()
            .antMatchers("/userpasswordmanager/**").permitAll()
            .antMatchers("/servidores").hasAnyAuthority("TI")
            .antMatchers("/**/cadastrar").hasAuthority("TI")
            .antMatchers("/**/editar").hasAuthority("TI")
            .antMatchers("/**/excluir").hasAuthority("TI")
            .anyRequest().authenticated();

        http.formLogin()
            .loginPage("/login")
            .defaultSuccessUrl("/servidores")
            .permitAll();

        http.logout()
            .logoutRequestMatcher(
                new AntPathRequestMatcher("/logout", "GET")
            )
            .logoutSuccessUrl("/login");

        http.rememberMe()
            .key("chaverememberMe");
        
        http.cors().and().csrf().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsServiceImpl)
            .passwordEncoder(new CriptografiaUtil());
    }
    

    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("*");
    }

}
