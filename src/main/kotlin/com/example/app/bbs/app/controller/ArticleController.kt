package com.example.app.bbs.app.controller

import com.example.app.bbs.app.request.ArticleRequest
import com.example.app.bbs.domain.entity.Article
import com.example.app.bbs.domain.repository.ArticleRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Direction
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class ArticleController {

    val PAGE_SIZE: Int = 10

    @Autowired
    lateinit var articleRepository : ArticleRepository

//    @GetMapping("/seed")
//    @ResponseBody
//    fun seed(): String {
//        for (i in 1..50) {
//            var article = Article()
//            article.name = "name_$i"
//            article.title = "title_$i"
//            article.contents = "contents_$i"
//            article.articleKey = "1234"
//            articleRepository.save(article)
//        }
//
//        return "Finish"
//    }


    @PostMapping("/")
    fun registerArticle(@Validated @ModelAttribute articleRequest: ArticleRequest, result: BindingResult
                        , redirectAttributes: RedirectAttributes, model: Model
    ): String {
        return if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", result)
            redirectAttributes.addFlashAttribute("request", articleRequest)

            "redirect:/"
        } else {
            articleRepository.save(getArticle(articleRequest))

            redirectAttributes.addFlashAttribute("message", "正常に投稿できました。")
            redirectAttributes.addFlashAttribute("alert_class", "alert-success")

            "redirect:/"
        }
    }

    @GetMapping("/")
    fun getArticleList(@ModelAttribute articleRequest: ArticleRequest,
                       @RequestParam(value = "page", defaultValue = "0", required = false) page: Int,
                       model : Model
    ) : String {

        val pageable: Pageable = PageRequest.of(
            page,
            this.PAGE_SIZE,
            Sort(Direction.DESC, "updateAt").and(Sort(Direction.ASC, "id"))
        )

        val key: String = BindingResult.MODEL_KEY_PREFIX + "articleRequest"
        if (model.containsAttribute("errors")) {
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
    fun getArticleEdit(@PathVariable id: Int, model : Model, redirectAttributes: RedirectAttributes) : String {

        return if (articleRepository.existsById(id)) {

            if (model.containsAttribute("request")) {
                model.addAttribute("article", model.asMap()["request"])
            } else {
                model.addAttribute("article", articleRepository.findById(id).get())
            }

            val key: String = BindingResult.MODEL_KEY_PREFIX + "article"
            if (model.containsAttribute("errors")) {
                model.addAttribute(key, model.asMap()["errors"])
            }

            "edit"
        } else {
            redirectAttributes.addFlashAttribute("message", "対象の記事が見つかりませんでした。")

            "redirect:/"
        }
    }

    @PostMapping("/update")
    fun updateArticle(@Validated articleRequest: ArticleRequest, result: BindingResult
                      , redirectAttributes: RedirectAttributes) : String {

        val newArticle = getArticle(articleRequest)

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", result)
            redirectAttributes.addFlashAttribute("request", articleRequest)

            return "redirect:/edit/${newArticle.id}"
        }

        val article: Article = articleRepository.findById(newArticle.id).orElse(Article())

        if (existsArticle(newArticle, article)) {
            newArticle.articleKey = article.articleKey
            newArticle.registerAt = article.registerAt
            articleRepository.save(newArticle)

            redirectAttributes.addFlashAttribute("message", "正常に更新しました。")
            redirectAttributes.addFlashAttribute("alert_class", "alert-success")
        } else {
            redirectAttributes.addFlashAttribute("message", "更新に失敗したので、もう一度お試しください。")
            redirectAttributes.addFlashAttribute("alert_class", "alert-error")
        }

        return "redirect:/"
    }

    @GetMapping("/delete/confirm/{id}")
    fun getDeleteConfirm(@PathVariable id: Int, model : Model, redirectAttributes: RedirectAttributes) : String {

        return if (articleRepository.existsById(id)) {

            if (model.containsAttribute("request")) {
                model.addAttribute("article", model.asMap()["request"])
            } else {
                model.addAttribute("article", articleRepository.findById(id).get())
            }

            val key: String = BindingResult.MODEL_KEY_PREFIX + "article"
            if (model.containsAttribute("errors")) {
                model.addAttribute(key, model.asMap()["errors"])
            }

            "delete_confirm"
        } else {
            redirectAttributes.addFlashAttribute("message", "対象の記事が見つかりませんでした。")

            "redirect:/"
        }
    }

    @PostMapping("/delete")
    fun deleteArticle(@Validated @ModelAttribute articleRequest: ArticleRequest,  result: BindingResult
                      , redirectAttributes: RedirectAttributes) : String {

        val deleteArticle = getArticle(articleRequest)

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", result)
            redirectAttributes.addFlashAttribute("request", articleRequest)

            return "redirect:/delete/confirm/${deleteArticle.id}"
        }

        val article = articleRepository.findById(deleteArticle.id).orElse(Article())

        if (existsArticle(deleteArticle, article)) {
            articleRepository.deleteById(deleteArticle.id)

            redirectAttributes.addFlashAttribute("message", "正常に削除しました。")
            redirectAttributes.addFlashAttribute("alert_class", "alert-success")
        } else {
            redirectAttributes.addFlashAttribute("message", "投稿Keyが一致しませんでした。")
            redirectAttributes.addFlashAttribute("alert_class", "alert-error")
        }

        return "redirect:/"
    }

    private fun getArticle(request: ArticleRequest): Article {
        return Article(request.id,
                request.name,
                request.title,
                request.contents,
                request.articleKey)
    }

    private fun existsArticle(request: Article, article: Article): Boolean {
        return request.id == article.id
                && request.articleKey == article.articleKey
    }
}