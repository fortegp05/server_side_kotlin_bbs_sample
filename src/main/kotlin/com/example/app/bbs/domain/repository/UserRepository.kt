package com.example.app.bbs.domain.repository

import com.example.app.bbs.domain.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Int> {
    fun findByName(name: String) : User?
    fun findByEmail(email: String) : User?
}