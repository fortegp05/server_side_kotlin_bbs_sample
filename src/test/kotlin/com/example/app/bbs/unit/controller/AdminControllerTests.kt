package com.example.app.bbs.unit.controller

import com.example.app.bbs.app.controller.AdminController
import com.example.app.bbs.domain.entity.Article
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTests {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var target: AdminController

    @Test
    fun noAuthenticationTest() {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/admin/index")
        )
        .andExpect(status().is3xxRedirection)
        .andExpect(redirectedUrlPattern("**/login"))
    }

    @Test
    @WithUserDetails(value = "admin")
    fun authenticationTest() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/admin/index")
        )
        .andExpect(status().isOk)
        .andExpect(model().attributeExists("page"))
        .andExpect(model().attributeExists("isAdmin"))
        .andExpect(view().name("admin_index"))
    }


    @Test
    @WithUserDetails(value = "admin")
    fun singleDeleteNotExistsArticleTest() {
        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/admin/article/delete/0")
                        .with(csrf())
        )
                .andExpect(status().is3xxRedirection)
                .andExpect(view().name("redirect:/admin/index"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(flash().attribute("message",
                        target.MESSAGE_ARTICLE_DOES_NOT_EXISTS))
    }

    @Test
    @Sql(statements = ["INSERT INTO article (name, title, contents, article_key) VALUES ('test', 'test', 'test', 'test');"])
    @WithUserDetails(value = "admin")
    fun singleDeleteExistsArticleTest() {
        val latestArticle: Article = target.articleRepository.findAll().last()

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/admin/article/delete/" + latestArticle.id)
                        .with(csrf())
        )
        .andExpect(status().is3xxRedirection)
        .andExpect(view().name("redirect:/admin/index"))
        .andExpect(flash().attributeExists("message"))
        .andExpect(flash().attribute("message",
                target.MESSAGE_DELETE_NORMAL))
    }

    @Test
    @WithUserDetails(value = "admin")
    fun multiDeleteNotSelectedArticleTest() {

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/admin/article/deletes")
                        .with(csrf())
        )
                .andExpect(status().is3xxRedirection)
                .andExpect(view().name("redirect:/admin/index"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(flash().attribute("message",
                        target.MESSAGE_ARTICLE_NOT_SELECTED))
    }

    @Test
    @Sql(statements = [
        "INSERT INTO article (name, title, contents, article_key) VALUES ('test', 'test', 'test', 'test');",
        "INSERT INTO article (name, title, contents, article_key) VALUES ('test', 'test', 'test', 'test');",
        "INSERT INTO article (name, title, contents, article_key) VALUES ('test', 'test', 'test', 'test');"
    ])
    @WithUserDetails(value = "admin")
    fun multiDeleteSelectedArticleTest() {
        val latestArticles: List<Article> = target.articleRepository.findAll()
        val ids = latestArticles.map{it.id}.joinToString(",")

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/admin/article/deletes")
                        .with(csrf())
                        .param("article_checks", ids)
        )
                .andExpect(status().is3xxRedirection)
                .andExpect(view().name("redirect:/admin/index"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(flash().attribute("message",
                        target.MESSAGE_DELETE_NORMAL))
    }

    @Test
    fun getAdminLoginTest() {

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/admin/login")
        )
                .andExpect(status().isOk)
    }

    @Test
    @Sql(statements = ["INSERT INTO users (name, email, password, role) VALUES ('admin1', 'admin1@example.com', '\$2a\$10\$CPNJ.PlWH8k1aMhC6ytjIuwxYuLWKMXTP3H6h.LRnpumtccpvXEGy', 'USER');"])
    fun adminLoginAuthTest() {

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/admin/login/auth")
                        .with(csrf())
                        .param("username","admin1")
                        .param("password","root")
        )
                .andExpect(status().is3xxRedirection)
                .andExpect(redirectedUrl("/"))
    }
}