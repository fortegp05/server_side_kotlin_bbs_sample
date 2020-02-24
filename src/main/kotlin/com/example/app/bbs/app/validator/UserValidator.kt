package com.example.app.bbs.app.validator

import com.example.app.bbs.domain.entity.User
import com.example.app.bbs.domain.entity.UserRole
import com.example.app.bbs.domain.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import org.springframework.validation.Errors
import org.springframework.validation.Validator
import java.util.*


@Component
class UserValidator : Validator {

    val PASSWORD_LENGTH_MIN = 4
    val PASSWORD_LENGTH_MAX = 8

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var messageSource: MessageSource

    override fun validate(target: Any, errors: Errors) {
        if (target !is User) {
            errors.reject("TAEGET_IS_NOT_USER_OBJECT")
            return
        }

        // Emailアドレスの重複チェック
        if (userRepository.findByEmail(target.email) != null) {
            errors.rejectValue("email", "REGISTERED_EMAIL")
        }

        // パスワードの長さチェック
        val passwordLengthRange = PASSWORD_LENGTH_MIN..PASSWORD_LENGTH_MAX
        if ( target.password.length !in passwordLengthRange ) {
            errors.rejectValue("password", "PASSWORD_LENGTH_ERROR", arrayOf(PASSWORD_LENGTH_MIN, PASSWORD_LENGTH_MAX),null)
        }

        // パスワード使用文字のチェック
        if (!target.password.matches(Regex("[a-zA-Z0-9\\-!_]*"))) {
            errors.rejectValue("password", "PASSWORD_ILLEGAL_CHAR")
        }

        // ロールのチェック(画面からはエラーにはならない)
        if (target.role != UserRole.USER) {
            errors.rejectValue("role", "NOT_USER_ROLE")
        }
    }

    override fun supports(clazz: Class<*>): Boolean {
        return User::class.equals(clazz)
    }
}