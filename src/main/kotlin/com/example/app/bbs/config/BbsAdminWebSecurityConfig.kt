package com.example.app.bbs.config

import com.example.app.bbs.app.service.UserDetailsServiceImpl
import com.example.app.bbs.domain.entity.UserRole
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.util.matcher.AntPathRequestMatcher


@Configuration
@EnableWebSecurity
@Order(1)
class BbsAdminWebSecurityConfig : WebSecurityConfigurerAdapter() {
    @Autowired
    lateinit var userDetailsService: UserDetailsServiceImpl

    @Override
    override fun configure(http: HttpSecurity) {
        // 許可の設定
        http
                .antMatcher("/admin/**")
                .authorizeRequests()
                .antMatchers("/admin/login").permitAll()
                .antMatchers("/admin/**").hasRole(UserRole.ADMIN.name)
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().accessDeniedPage("/admin/login")


        // ログイン設定
        http.formLogin()
                .loginProcessingUrl("/admin/login/auth")
                .loginPage("/admin/login")


        // ログアウト
        http.logout()
                .logoutRequestMatcher(AntPathRequestMatcher("/admin/logout"))
                .logoutSuccessUrl("/")
    }


    @Override
    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService)
    }
}