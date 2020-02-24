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
                // /user/以下に対して以下の設定を行う
                .antMatcher("/user/**")
                .authorizeRequests()
                // ログイン画面とユーザー登録画面はアクセス可能
                .antMatchers("/user/login").permitAll()
                .antMatchers("/user/signup").permitAll()
                // ユーザーは一般ユーザーとする
                .antMatchers("/user/**").hasRole(UserRole.USER.name)
                // 条件に一致するURLは認可が必要
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().accessDeniedPage("/user/login")

        // ログイン設定(独自の認証画面を指定する)
        http.formLogin()
                // 認証処理をするURLを指定
                .loginProcessingUrl("/user/login/auth")
                // ログインページのURLを指定
                .loginPage("/user/login")
                // ログインに失敗したときの遷移先
                .failureForwardUrl("/user/login")
                // ログインに使用するユーザー名とパスワードのパラメータ名
                .usernameParameter("email")
                .passwordParameter("password")

        // ログアウト
        http.logout()
                // ログアウト処理のURL
                .logoutRequestMatcher(AntPathRequestMatcher("/logout**"))
                // ログアウトしたあとに表示するURL
                .logoutSuccessUrl("/")
    }

    @Override
    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService)
    }
}