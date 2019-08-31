package com.example.app.bbs.app.controller

import com.example.app.bbs.app.request.ArticleRequest
import com.example.app.bbs.domain.entity.Article
import com.example.app.bbs.domain.repository.ArticleRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.util.*

@Controller
class ArticleController {

    val PAGE_SIZE: Int = 10

    val MESSAGE_REGISTER_NORMAL = "正常に投稿できました。"
    val MESSAGE_ARTICLE_DOES_NOT_EXISTS = "対象の記事が見つかりませんでした。"
    val MESSAGE_UPDATE_NORMAL = "正常に更新しました。"
    val MESSAGE_ARTICLE_KEY_UNMATCH = "投稿KEYが一致しません。"
    val MESSAGE_DELETE_NORMAL = "正常に削除しました。"

    @Autowired
    lateinit var articleRepository : ArticleRepository

    @GetMapping("/seed")
    @ResponseBody
    fun seed(): String {
        for (i in 1..50) {
            var article = Article()
            article.name = "name_$i"
            article.title = "title_$i"
            article.contents = "contents_$i"
            article.articleKey = "1234"
            articleRepository.save(article)
        }

        return "Finish"
    }

    @PostMapping("/")
    fun registerArticle(@Validated @ModelAttribute articleRequest: ArticleRequest,
                        result: BindingResult,
                        redirectAttributes: RedirectAttributes): String {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", result)
            redirectAttributes.addFlashAttribute("request", articleRequest)

            return "redirect:/"
        }

        articleRepository.save(
            Article(
                articleRequest.id,
                articleRequest.name,
                articleRequest.title,
                articleRequest.contents,
                articleRequest.articleKey
            )
        )

        redirectAttributes.addFlashAttribute(
            "message", MESSAGE_REGISTER_NORMAL
        )

        return "redirect:/"
    }

    @GetMapping("/")
    fun getArticleList(@ModelAttribute articleRequest: ArticleRequest,
                       @RequestParam(value = "page", defaultValue = "0", required = false) page: Int,
                       model : Model
    ) : String {

        val pageable: Pageable = PageRequest.of(
            page,
            this.PAGE_SIZE,
            Sort(Sort.Direction.DESC, "updateAt").and(Sort(Sort.Direction.ASC, "id"))
        )

        if (model.containsAttribute("errors")) {
            val key: String = BindingResult.MODEL_KEY_PREFIX + "articleRequest"
            model.addAttribute(key, model.asMap()["errors"])
        }

        if (model.containsAttribute("request")) {
            model.addAttribute("articleRequest", model.asMap()["request"])
        }

        val articles: Page<Article> = articleRepository.findAll(pageable)
        model.addAttribute("page", articles)

        return "index"
    }


    @GetMapping("/edit/{id}")
    fun getArticleEdit(@PathVariable id: Int, model: Model, redirectAttributes: RedirectAttributes) : String {

        return if (articleRepository.existsById(id)) {
            if (model.containsAttribute("request")) {
                model.addAttribute("article", model.asMap()["request"])
            } else {
                model.addAttribute("article", articleRepository.findById(id).get())
            }

            if (model.containsAttribute("errors")) {
                val key: String = BindingResult.MODEL_KEY_PREFIX + "article"
                model.addAttribute(key, model.asMap()["errors"])
            }

            "edit"
        } else {
            redirectAttributes.addFlashAttribute("message", MESSAGE_ARTICLE_DOES_NOT_EXISTS)

            "redirect:/"
        }
    }

    @PostMapping("/update")
    fun updateArticle(@Validated articleRequest: ArticleRequest, result: BindingResult,
                      redirectAttributes: RedirectAttributes
    ) : String {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", result)
            redirectAttributes.addFlashAttribute("request", articleRequest)

            return "redirect:/edit/${articleRequest.id}"
        }

        if (!articleRepository.existsById(articleRequest.id)) {
            redirectAttributes.addFlashAttribute("message", MESSAGE_ARTICLE_DOES_NOT_EXISTS)

            return "redirect:/"
        }

        val article: Article = articleRepository.findById(articleRequest.id).get()

        return if (articleRequest.articleKey == article.articleKey) {
            article.name = articleRequest.name
            article.title = articleRequest.title
            article.contents = articleRequest.contents
            article.updateAt = Date()

            articleRepository.save(article)

            redirectAttributes.addFlashAttribute("message", MESSAGE_UPDATE_NORMAL)

            "redirect:/"
        } else {
            redirectAttributes.addFlashAttribute("message", MESSAGE_ARTICLE_KEY_UNMATCH)

            "redirect:/edit/${articleRequest.id}"
        }
    }


    @GetMapping("/delete/confirm/{id}")
    fun getDeleteConfirm(@PathVariable id: Int, model : Model,
                         redirectAttributes: RedirectAttributes
    ) : String {

        return if (articleRepository.existsById(id)) {
            model.addAttribute("article", articleRepository.findById(id).get())

            val key: String = BindingResult.MODEL_KEY_PREFIX + "article"
            if (model.containsAttribute("errors")) {
                model.addAttribute(key, model.asMap()["errors"])
            }

            "delete_confirm"
        } else {
            redirectAttributes.addFlashAttribute("message", MESSAGE_ARTICLE_DOES_NOT_EXISTS)

            "redirect:/"
        }
    }

    @PostMapping("/delete")
    fun deleteArticle(@Validated @ModelAttribute articleRequest: ArticleRequest,  result: BindingResult,
                      redirectAttributes: RedirectAttributes

    ) : String {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", result)

            return "redirect:/delete/confirm/${articleRequest.id}"
        }

        if (!articleRepository.existsById(articleRequest.id)) {
            redirectAttributes.addFlashAttribute("message", MESSAGE_ARTICLE_DOES_NOT_EXISTS)

            return "redirect:/"
        }

        val article: Article = articleRepository.findById(articleRequest.id).get()

        return if (articleRequest.articleKey == article.articleKey) {
            articleRepository.deleteById(articleRequest.id)

            redirectAttributes.addFlashAttribute("message", MESSAGE_DELETE_NORMAL)

            "redirect:/"
        } else {
            redirectAttributes.addFlashAttribute("message", MESSAGE_ARTICLE_KEY_UNMATCH)

            "redirect:/delete/confirm/${article.id}"
        }
    }
}