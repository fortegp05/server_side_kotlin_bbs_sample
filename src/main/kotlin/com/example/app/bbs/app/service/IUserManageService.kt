package com.example.app.bbs.app.service

import com.example.app.bbs.domain.entity.User

interface IUserManageService {
    fun registerUser(user : User, rawPassword: String)
}