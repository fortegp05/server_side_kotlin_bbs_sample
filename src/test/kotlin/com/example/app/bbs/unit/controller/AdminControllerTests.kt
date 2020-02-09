package com.example.app.bbs.unit.controller

import com.example.app.bbs.app.controller.AdminController
import com.example.app.bbs.domain.entity.Article
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTests {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var target: AdminController

    @Test
    fun noAuthentication() {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/admin/index")
        )
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection)
        .andExpect(MockMvcResultMatchers.redirectedUrlPattern("**/login"))
    }

    @Test
    @WithUserDetails(value = "admin")
    fun authentication() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/admin/index")
        )
        .andExpect(MockMvcResultMatchers.status().isOk)
        .andExpect(MockMvcResultMatchers.model().attributeExists("page"))
        .andExpect(MockMvcResultMatchers.view().name("admin_index"))
    }


    @Test
    @WithUserDetails(value = "admin")
    fun singleDeleteNotExistsArticle() {
        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/admin/article/delete/0")
                        .with(csrf())
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection)
                .andExpect(MockMvcResultMatchers.view().name("redirect:/admin/index"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists<String>("message"))
                .andExpect(MockMvcResultMatchers.flash().attribute<String>("message",
                        target.MESSAGE_ARTICLE_DOES_NOT_EXISTS))
    }

    @Test
    @Sql(statements = ["INSERT INTO article (name, title, contents, article_key) VALUES ('test', 'test', 'test', 'test');"])
    @WithUserDetails(value = "admin")
    fun singleDeleteExistsArticle() {
        val latestArticle: Article = target.articleRepository.findAll().last()

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/admin/article/delete/" + latestArticle.id)
                        .with(csrf())
        )
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection)
        .andExpect(MockMvcResultMatchers.view().name("redirect:/admin/index"))
        .andExpect(MockMvcResultMatchers.flash().attributeExists<String>("message"))
        .andExpect(MockMvcResultMatchers.flash().attribute<String>("message",
                target.MESSAGE_DELETE_NORMAL))
    }

    @Test
    @WithUserDetails(value = "admin")
    fun multiDeleteNotSelectedArticle() {

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/admin/article/deletes")
                        .with(csrf())
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection)
                .andExpect(MockMvcResultMatchers.view().name("redirect:/admin/index"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists<String>("message"))
                .andExpect(MockMvcResultMatchers.flash().attribute<String>("message",
                        target.MESSAGE_ARTICLE_NOT_SELECTED))
    }

    @Test
    @Sql(statements = [
        "INSERT INTO article (name, title, contents, article_key) VALUES ('test', 'test', 'test', 'test');",
        "INSERT INTO article (name, title, contents, article_key) VALUES ('test', 'test', 'test', 'test');",
        "INSERT INTO article (name, title, contents, article_key) VALUES ('test', 'test', 'test', 'test');"
    ])
    @WithUserDetails(value = "admin")
    fun multiDeleteSelectedArticle() {
        val latestArticles: List<Article> = target.articleRepository.findAll()
        val ids = latestArticles.map{it.id}.joinToString(",")

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/admin/article/deletes")
                        .with(csrf())
                        .param("article_checks", ids)
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection)
                .andExpect(MockMvcResultMatchers.view().name("redirect:/admin/index"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists<String>("message"))
                .andExpect(MockMvcResultMatchers.flash().attribute<String>("message",
                        target.MESSAGE_DELETE_NORMAL))
    }
}