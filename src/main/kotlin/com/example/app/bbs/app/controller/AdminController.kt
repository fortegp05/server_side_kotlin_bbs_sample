package com.example.app.bbs.app.controller

import com.example.app.bbs.domain.entity.Article
import com.example.app.bbs.domain.repository.ArticleRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class AdminController {

    val MESSAGE_ARTICLE_DOES_NOT_EXISTS = "対象の記事が見つかりませんでした。"
    val MESSAGE_ARTICLE_NOT_SELECTED = "削除する記事を選択してください。"
    val MESSAGE_DELETE_NORMAL = "正常に削除しました。"

    val ALERT_CLASS_ERROR = "alert-error"

    val PAGE_SIZE: Int = 10

    @Autowired
    lateinit var articleRepository : ArticleRepository

    @GetMapping("/admin/login")
    fun getUserLogin() : String {
        return "admin_login"
    }

    @PostMapping("/admin/login/auth")
    fun userLogin() : String {

        return "redirect:/admin/index"
    }

    @PostMapping("/admin/logout")
    fun userLogout() : String {

        return "redirect:/"
    }

    @GetMapping("/admin/index")
    fun getAdminIndex(@RequestParam(value = "page",
                              defaultValue = "0",
                              required = false) page: Int,
                      model : Model) : String {

        val pageable: Pageable = PageRequest.of(
                page,
                this.PAGE_SIZE,
                Sort(Sort.Direction.DESC, "updateAt")
                        .and(Sort(Sort.Direction.ASC, "id"))
        )

        val articles: Page<Article> = articleRepository.findAll(pageable)
        model.addAttribute("page", articles)
        model.addAttribute("isAdmin", true)

        return "admin_index"
    }

    @PostMapping("/admin/article/delete/{id}")
    fun deleteArticle(@PathVariable id: Int,
                      redirectAttributes: RedirectAttributes
    ) : String {

        if (!articleRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("message",
                    MESSAGE_ARTICLE_DOES_NOT_EXISTS)
            redirectAttributes.addFlashAttribute("alert_class",
                    ALERT_CLASS_ERROR)

            return "redirect:/admin/index"
        }

        articleRepository.deleteById(id)

        redirectAttributes.addFlashAttribute("message",
                MESSAGE_DELETE_NORMAL)

        return "redirect:/admin/index"
    }

    @PostMapping("/admin/article/deletes")
    fun deleteArticles(@RequestParam(value = "article_checks", required = false) checkboxValues: List<Int>?,
                      redirectAttributes: RedirectAttributes
    ) : String {

        if (checkboxValues == null || checkboxValues.isEmpty()) {
            redirectAttributes.addFlashAttribute("message",
                    MESSAGE_ARTICLE_NOT_SELECTED)
            redirectAttributes.addFlashAttribute("alert_class",
                    ALERT_CLASS_ERROR)

            return "redirect:/admin/index"
        }

        articleRepository.deleteByIdIn(checkboxValues)

        redirectAttributes.addFlashAttribute("message",
                MESSAGE_DELETE_NORMAL)

        return "redirect:/admin/index"
    }
}