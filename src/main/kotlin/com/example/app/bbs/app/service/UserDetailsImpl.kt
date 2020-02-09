package com.example.app.bbs.app.service

import com.example.app.bbs.domain.entity.User
import com.example.app.bbs.domain.entity.UserRole
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserDetailsImpl(argUser: User) : UserDetails {
    val user : User = argUser
    val authorities = mutableListOf(GrantedAuthority({ "ROLE_${argUser.role.name}" }))

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return authorities
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun getUsername(): String {
        return user.name
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun getPassword(): String {
        return user.password
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }
}
