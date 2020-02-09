function articleDelete(articleId) {
    let deleteArticleForm = document.form_delete_article
    deleteArticleForm.action += articleId
    deleteArticleForm.submit()
}