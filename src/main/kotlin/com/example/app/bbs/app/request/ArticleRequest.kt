package com.example.app.bbs.app.request

import javax.validation.constraints.*

data class ArticleRequest (
        var id : Int = 0,
        @field:NotBlank
        @field:Size(min = 1, max = 50)
        var name : String = "",
        @field:NotBlank
        @field:Size(min = 1, max = 50)
        var title : String = "",
        @field:NotBlank
        @field:Size(min = 1, max = 500)
        var contents : String = "",
        @field:NotBlank
        @field:Size(min = 1, max = 4)
        var articleKey : String = ""
)