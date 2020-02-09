package com.example.app.bbs.unit.controller

import com.example.app.bbs.app.controller.UserController
import com.example.app.bbs.domain.entity.Article
import com.example.app.bbs.domain.entity.UserRole
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTests {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var target: UserController

    @Test
    fun getUserSignupTest() {

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/user/signup")
        )
        .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun postUserSignupNormalTest() {

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/user/signup")
                        .with(csrf())
                        .param("name","test1")
                        .param("email","test1@example.com")
                        .param("password","test1")
                        .param("role", UserRole.USER.toString())
        )
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection)
        .andExpect(MockMvcResultMatchers.view().name("redirect:/user/login"))
    }

    @Test
    @Sql(statements = ["INSERT INTO user (name, email, password, role) VALUES ('test2', 'test2@example.com', 'test2', 'USER');"])
    fun postUserSignupRegisteredErrorTest() {

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/user/signup")
                        .with(csrf())
                        .param("name","test2")
                        .param("email","test2@example.com")
                        .param("password","test2")
                        .param("role", UserRole.USER.toString())
        )
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection)
        .andExpect(MockMvcResultMatchers.view().name("redirect:/user/signup"))
        .andExpect(MockMvcResultMatchers.flash().attributeExists<String>("errors"))
        .andExpect(MockMvcResultMatchers.flash().attributeExists<String>("request"))
    }

    @Test
    fun postUserSignupPasswordShortTest() {

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/user/signup")
                        .with(csrf())
                        .param("name","test2")
                        .param("email","test2@example.com")
                        .param("password","a")
                        .param("role", UserRole.USER.toString())
        )
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection)
        .andExpect(MockMvcResultMatchers.view().name("redirect:/user/signup"))
        .andExpect(MockMvcResultMatchers.flash().attributeExists<String>("errors"))
        .andExpect(MockMvcResultMatchers.flash().attributeExists<String>("request"))
    }

    @Test
    fun postUserSignupPasswordLongTest() {

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/user/signup")
                        .with(csrf())
                        .param("name","test2")
                        .param("email","test2@example.com")
                        .param("password","123456789012345")
                        .param("role", UserRole.USER.toString())
        )
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection)
        .andExpect(MockMvcResultMatchers.view().name("redirect:/user/signup"))
        .andExpect(MockMvcResultMatchers.flash().attributeExists<String>("errors"))
        .andExpect(MockMvcResultMatchers.flash().attributeExists<String>("request"))
    }

    @Test
    fun postUserSignupPasswordCharErrorTest() {

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/user/signup")
                        .with(csrf())
                        .param("name","test2")
                        .param("email","test2@example.com")
                        .param("password","あいうえお")
                        .param("role", UserRole.USER.toString())
        )
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection)
        .andExpect(MockMvcResultMatchers.view().name("redirect:/user/signup"))
        .andExpect(MockMvcResultMatchers.flash().attributeExists<String>("errors"))
        .andExpect(MockMvcResultMatchers.flash().attributeExists<String>("request"))
    }

    @Test
    fun postUserSignupAdminRegistrationTest() {

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/user/signup")
                        .with(csrf())
                        .param("name","test2")
                        .param("email","test2@example.com")
                        .param("password","1234")
                        .param("role", UserRole.ADMIN.toString())
        )
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection)
        .andExpect(MockMvcResultMatchers.view().name("redirect:/user/signup"))
        .andExpect(MockMvcResultMatchers.flash().attributeExists<String>("errors"))
        .andExpect(MockMvcResultMatchers.flash().attributeExists<String>("request"))
    }

    @Test
    fun getUserLoginTest() {

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/user/login")
        )
        .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    @Sql(statements = ["INSERT INTO user (name, email, password, role) VALUES ('test3', 'test3@example.com', '\$2a\$10\$MMnbIXYB4BQI88yiKpiR2eiIIHiUEymGMyWqWlp01Iz.aqbD3ud4i', 'USER');"])
    fun userLoginAuthTest() {

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/user/login/auth")
                        .with(csrf())
                        .param("email","test3@example.com")
                        .param("password","test3")
        )
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection)
        .andExpect(MockMvcResultMatchers.redirectedUrl("/"))
    }

    @Test
    @Sql(statements = ["INSERT INTO user (name, email, password, role) VALUES ('test4', 'test4@example.com', '\$2a\$10\$MMnbIXYB4BQI88yiKpiR2eiIIHiUEymGMyWqWlp01Iz.aqbD3ud4i', 'USER');"])
    @WithUserDetails(value = "test4")
    fun authentication() {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/user/index")
        )
        .andExpect(MockMvcResultMatchers.status().isOk)
        .andExpect(MockMvcResultMatchers.model().attributeExists("page"))
        .andExpect(MockMvcResultMatchers.model().attributeExists("user"))
    }

    @Test
    @Sql(statements = ["INSERT INTO user (name, email, password, role) VALUES ('test5', 'test5@example.com', '\$2a\$10\$MMnbIXYB4BQI88yiKpiR2eiIIHiUEymGMyWqWlp01Iz.aqbD3ud4i', 'USER');"])
    @WithUserDetails(value = "test5")
    fun registerArticleTest() {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/user/article/register")
                        .with(csrf())
                        .param("name", "test")
                        .param("title", "test")
                        .param("contents", "test")
                        .param("articleKey", "test")
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection)
                .andExpect(MockMvcResultMatchers.view().name("redirect:/user/index"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists<String>("message"))
                .andExpect(MockMvcResultMatchers.flash().attribute<String>("message",
                        target.MESSAGE_REGISTER_NORMAL))
    }

    @Test
    @Sql(statements = ["INSERT INTO user (name, email, password, role) VALUES ('test6', 'test6@example.com', '\$2a\$10\$MMnbIXYB4BQI88yiKpiR2eiIIHiUEymGMyWqWlp01Iz.aqbD3ud4i', 'USER');"])
    @WithUserDetails(value = "test6")
    fun registerArticleRequestErrorTest() {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/user/article/register")
                        .with(csrf())
                        .param("name", "")
                        .param("title", "")
                        .param("contents", "")
                        .param("articleKey", "")
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection)
                .andExpect(MockMvcResultMatchers.view().name("redirect:/user/index"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists<String>("errors"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists<String>("request"))
    }
}