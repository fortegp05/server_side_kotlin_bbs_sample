package com.example.app.bbs.app.service

import com.example.app.bbs.domain.entity.User
import com.example.app.bbs.domain.entity.UserRole
import com.example.app.bbs.domain.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserManagerServiceImpl {
    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    //一般ユーザを登録するメソッド
    fun registerUser(user: User, rawPassword: String) {

        // 明示的にUserRoleを指定する
        user.role = UserRole.USER

        // エンコードしたパスワードでユーザーを作成する
        user.password = passwordEncoder.encode(rawPassword)

        userRepository.save(user)
    }
}