package com.example.app.bbs.domain.repository

import com.example.app.bbs.domain.entity.Article
import org.springframework.data.jpa.repository.JpaRepository

interface ArticleRepository : JpaRepository<Article, Int>