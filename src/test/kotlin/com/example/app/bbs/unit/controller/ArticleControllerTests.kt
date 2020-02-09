package com.example.app.bbs.unit.controller

import com.example.app.bbs.app.controller.ArticleController
import com.example.app.bbs.domain.entity.Article
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class ArticleControllerTests {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var target: ArticleController

    @Test
    fun registerArticleTest() {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/")
                        .with(csrf())
                        .param("name", "test")
                        .param("title", "test")
                        .param("contents", "test")
                        .param("articleKey", "test")
        )
                .andExpect(status().is3xxRedirection)
                .andExpect(view().name("redirect:/"))
                .andExpect(flash().attributeExists<String>("message"))
                .andExpect(flash().attribute<String>("message",
                        target.MESSAGE_REGISTER_NORMAL))
    }

    @Test
    fun registerArticleRequestErrorTest() {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/")
                        .with(csrf())
                        .param("name", "")
                        .param("title", "")
                        .param("contents", "")
                        .param("articleKey", "")
        )
                .andExpect(status().is3xxRedirection)
                .andExpect(view().name("redirect:/"))
                .andExpect(flash().attributeExists<String>("errors"))
                .andExpect(flash().attributeExists<String>("request"))
    }


    @Test
    fun getArticleListTest() {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/")
        )
                .andExpect(status().isOk)
                .andExpect(model().attributeExists("page"))
                .andExpect(view().name("index"))
    }


    @Test
    fun getArticleEditNotExistsIdTest() {

        mockMvc.perform(
                MockMvcRequestBuilders.get("/edit/" + 0)
        )
        .andExpect(status().is3xxRedirection)
        .andExpect(view().name("redirect:/"))
    }

    @Test
    @Sql(statements = ["INSERT INTO article (name, title, contents, article_key) VALUES ('test', 'test', 'test', 'test');"])
    fun getArticleEditExistsIdTest() {
        val latestArticle: Article = target.articleRepository.findAll().last()

        mockMvc.perform(
                MockMvcRequestBuilders.get("/edit/" + latestArticle.id)
        )
        .andExpect(status().isOk)
        .andExpect(view().name("edit"))
    }


    @Test
    fun updateArticleNotExistsArticleTest() {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/update")
                        .with(csrf())
                        .param("id", "0")
                        .param("name", "test")
                        .param("title", "test")
                        .param("contents", "test")
                        .param("articleKey", "err.")
        )
                .andExpect(status().is3xxRedirection)
                .andExpect(view().name("redirect:/"))
                .andExpect(flash().attributeExists<String>("message"))
                .andExpect(flash().attribute<String>("message",
                        target.MESSAGE_ARTICLE_DOES_NOT_EXISTS))
                .andExpect(flash().attribute<String>("alert_class",
                        target.ALERT_CLASS_ERROR))
    }

    @Test
    @Sql(statements = ["INSERT INTO article (name, title, contents, article_key,register_at, update_at) VALUES ('test', 'test', 'test', 'test', now(), now());"])
    fun updateArticleNotMatchArticleKeyTest() {
        val latestArticle: Article = target.articleRepository.findAll().last()

        mockMvc.perform(
                MockMvcRequestBuilders.post("/update")
                        .with(csrf())
                        .param("id", latestArticle.id.toString())
                        .param("name", latestArticle.name)
                        .param("title", latestArticle.title)
                        .param("contents", latestArticle.contents)
                        .param("articleKey", "err.")
        )
                .andExpect(status().is3xxRedirection)
                .andExpect(
                        view().name("redirect:/edit/${latestArticle.id.toString()}")
                )
                .andExpect(flash().attributeExists<String>("message"))
                .andExpect(flash().attribute<String>("message",
                        target.MESSAGE_ARTICLE_KEY_UNMATCH))
                .andExpect(flash().attribute<String>("alert_class",
                        target.ALERT_CLASS_ERROR))
    }

    @Test
    @Sql(statements = ["INSERT INTO article (name, title, contents, article_key, register_at, update_at) VALUES ('test', 'test', 'test', 'test', now(), now());"])
    fun updateArticleExistsArticleTest() {
        val latestArticle: Article = target.articleRepository.findAll().last()

        mockMvc.perform(
                MockMvcRequestBuilders.post("/update")
                        .with(csrf())
                        .param("id", latestArticle.id.toString())
                        .param("name", latestArticle.name)
                        .param("title", latestArticle.title)
                        .param("contents", latestArticle.contents)
                        .param("articleKey", latestArticle.articleKey)
        )
                .andExpect(status().is3xxRedirection)
                .andExpect(view().name("redirect:/"))
                .andExpect(flash().attributeExists<String>("message"))
                .andExpect(flash().attribute<String>("message",
                        target.MESSAGE_UPDATE_NORMAL))
    }

    @Test
    fun updateArticleRequestErrorTest() {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/update")
                        .with(csrf())
        )
                .andExpect(status().is3xxRedirection)
                .andExpect(view().name("redirect:/edit/0"))
                .andExpect(flash().attributeExists<String>("errors"))
                .andExpect(flash().attributeExists<String>("request"))
    }


    @Test
    fun getDeleteConfirmNotExistsIdTest() {

        mockMvc.perform(
                MockMvcRequestBuilders.get("/delete/confirm/0")
        )
                .andExpect(status().is3xxRedirection)
                .andExpect(view().name("redirect:/"))
                .andExpect(flash().attributeExists<String>("message"))
                .andExpect(flash().attribute<String>("message", target.MESSAGE_ARTICLE_DOES_NOT_EXISTS))
    }


    @Test
    @Sql(statements = ["INSERT INTO article (name, title, contents, article_key) VALUES ('test', 'test', 'test', 'test');"])
    fun getDeleteConfirmExistsIdTest() {
        val latestArticle: Article = target.articleRepository.findAll().last()

        mockMvc.perform(
                MockMvcRequestBuilders.get(
                        "/delete/confirm/${latestArticle.id.toString()}"
                )
        )
                .andExpect(status().isOk)
                .andExpect(view().name("delete_confirm"))
    }

    @Test
    fun deleteArticleNotExistsArticleTest() {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/delete")
                        .with(csrf())
                        .param("id", "0")
                        .param("name", "test")
                        .param("title", "test")
                        .param("contents", "test")
                        .param("articleKey", "err.")
        )
                .andExpect(status().is3xxRedirection)
                .andExpect(view().name("redirect:/"))
                .andExpect(flash().attributeExists<String>("message"))
                .andExpect(flash().attribute<String>("message",
                        target.MESSAGE_ARTICLE_DOES_NOT_EXISTS))
    }

    @Test
    @Sql(statements = ["INSERT INTO article (name, title, contents, article_key, register_at, update_at) VALUES ('test', 'test', 'test', 'test', now(), now());"])
    fun deleteArticleNotMatchArticleKeyTest() {
        val latestArticle: Article = target.articleRepository.findAll().last()

        mockMvc.perform(
                MockMvcRequestBuilders.post("/delete")
                        .with(csrf())
                        .param("id", latestArticle.id.toString())
                        .param("name", latestArticle.name)
                        .param("title", latestArticle.title)
                        .param("contents", latestArticle.contents)
                        .param("articleKey", "err.")
        )
                .andExpect(status().is3xxRedirection)
                .andExpect(view().name(
                        "redirect:/delete/confirm/${latestArticle.id.toString()}")
                )
                .andExpect(flash().attributeExists<String>("message"))
                .andExpect(flash().attribute<String>("message",
                        target.MESSAGE_ARTICLE_KEY_UNMATCH))
    }

    @Test
    @Sql(statements = ["INSERT INTO article(name, title, contents, article_key, register_at, update_at) VALUES ('test', 'test', 'test', 'test', now(), now());"])
    fun deleteArticleExistsArticleTest() {
        val latestArticle: Article = target.articleRepository.findAll().last()

        mockMvc.perform(
                MockMvcRequestBuilders.post("/delete")
                        .with(csrf())
                        .param("id", latestArticle.id.toString())
                        .param("name", latestArticle.name)
                        .param("title", latestArticle.title)
                        .param("contents", latestArticle.contents)
                        .param("articleKey", latestArticle.articleKey)
        )
                .andExpect(status().is3xxRedirection)
                .andExpect(view().name("redirect:/"))
                .andExpect(flash().attributeExists<String>("message"))
                .andExpect(flash().attribute<String>("message",
                        target.MESSAGE_DELETE_NORMAL))
    }

    @Test
    fun deleteArticleRequestErrorTest() {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/delete")
                        .with(csrf())
        )
                .andExpect(status().is3xxRedirection)
                .andExpect(view().name("redirect:/delete/confirm/0"))
                .andExpect(flash().attributeExists<String>("errors"))
                .andExpect(flash().attributeExists<String>("request"))
    }
}