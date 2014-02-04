package utils

import play.api.templates.Html
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.{Codec, SimpleResult}
import play.api.libs.iteratee.Iteratee

object PageHelper {
  def getHtml(result: SimpleResult)(implicit codec: Codec): Future[Html] = {
    result.body
      .run(Iteratee.consume())
      .map(bytes => Html(new String(bytes, codec.charset)))
  }

  def render(html: Html, id: String): Html = {
    views.html.utils.stream(html, id)
  }

  def renderStream(html: Html, id: String): HtmlStream = {
    HtmlStream(render(html, id))
  }

  def renderStream(htmlFuture: Future[Html], id: String): HtmlStream = {
    HtmlStream.flatten(htmlFuture.map(html => renderStream(html, id)))
  }
}