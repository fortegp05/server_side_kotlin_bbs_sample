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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher


@Configuration
@EnableWebSecurity
@Order(2)
class BbsUserWebSecurityConfig : WebSecurityConfigurerAdapter() {
    @Autowired
    lateinit var userDetailsService: UserDetailsServiceImpl

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Override
    override fun configure(web: WebSecurity) {
        // ここに設定したものはセキュリティ設定を無視
        web.ignoring().antMatchers(
                "/favicon.ico",
                "/css/**",
                "/js/**"
        )
    }

    @Override
    override fun configure(http: HttpSecurity) {
        // 許可の設定
        http
                .antMatcher("/user/**")
                .authorizeRequests()
                .antMatchers("/user/login").permitAll()
                .antMatchers("/user/signup").permitAll()
                .antMatchers("/user/**").hasRole(UserRole.USER.name)
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().accessDeniedPage("/user/login")

        // ログイン設定(独自の認証画面を指定する)
        http.formLogin()
                .loginProcessingUrl("/user/login/auth")
                .loginPage("/user/login")
                .failureForwardUrl("/user/login")
                .usernameParameter("email")
                .passwordParameter("password")

        // ログアウト
        http.logout()
                .logoutRequestMatcher(AntPathRequestMatcher("/user/logout"))
                .logoutSuccessUrl("/")
    }

    @Override
    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService)
    }
}