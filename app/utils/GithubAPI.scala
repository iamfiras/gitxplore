package utils

object GithubAPI {
  val search = "https://api.github.com/search/repositories?q=%s"
  val commits = "https://api.github.com/repos/%s/commits?per_page=%d"
  val collaborators = "https://api.github.com/repos/%s/collaborators"
}