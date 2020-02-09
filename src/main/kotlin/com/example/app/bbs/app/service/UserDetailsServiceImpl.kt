package com.example.app.bbs.app.service

import com.example.app.bbs.domain.entity.User
import com.example.app.bbs.domain.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl : UserDetailsService {
    @Autowired
    lateinit var userRepository: UserRepository

    override fun loadUserByUsername(username: String?): UserDetails {
        var user : User? = null
        if (username != null) {
            user = userRepository.findByName(username)

            if (user == null) {
                user = userRepository.findByEmail(username)
            }
        }

        if (user == null) {
            throw UsernameNotFoundException(username)
        }
        return UserDetailsImpl(user)
    }
}