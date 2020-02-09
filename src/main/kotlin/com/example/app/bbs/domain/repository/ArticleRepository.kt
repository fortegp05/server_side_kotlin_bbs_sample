package com.example.app.bbs.domain.repository

import com.example.app.bbs.domain.entity.Article
import org.springframework.data.domain.Page
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.domain.Pageable
import javax.transaction.Transactional

interface ArticleRepository : JpaRepository<Article, Int> {
    @Transactional
    fun deleteByIdIn(ids: List<Int>)

    fun findAllByUserId(userId : Int, pageable : Pageable) : Page<Article>
}