package com.example.app.bbs.domain.entity

import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Entity
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Int = 0,
    @field:NotBlank
    @field:Size(min = 4, max = 16)
    var name : String = "",
    @field:NotBlank
    @field:Email
    var email : String = "",
    @field:NotBlank
    var password : String = "",
    @Enumerated(EnumType.STRING)
    var role : UserRole = UserRole.USER
)