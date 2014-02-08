package mocks

import play.api._
import play.api.mvc._
import play.api.libs.concurrent.Promise

import scala.concurrent._
import ExecutionContext.Implicits.global

import controllers._
import models._
import utils._
import utils.HtmlStreamImplicits._

object DetailsMock extends Controller {

  def index(repo: String, name: String) = Action.async { request =>
    val reponame = repo + "/" + name
    for {
      commits <- CommitMock.get(reponame, 100)

      historySimpleResult <- Details.getHistoryList(commits)(request)
      timelineSimpleResult <- Details.getTimeline(commits)(request)

      historyHtml <- PageHelper.getHtml(historySimpleResult)
      timelineHtml <- PageHelper.getHtml(timelineSimpleResult)
    } yield {
      Ok(views.html.details.index(reponame, None, historyHtml, timelineHtml))
    }
  }

	def stream(repo: String, name: String) = Action.async { request =>
		val reponame = repo + "/" + name
		CommitMock.get(reponame, 100).map { commits =>
			Ok.chunked(views.stream.details.chunked(reponame, buildHtmlStream(commits, request)))
		}
	}

	def buildHtmlStream(commits: Seq[Commit], request: Request[AnyContent]): HtmlStream = {
		val historyHtmlFuture = Details.getHistoryList(commits)(request).flatMap(simpleResult => PageHelper.getHtml(simpleResult))
		val timelineHtmlFuture = Details.getTimeline(commits)(request).flatMap(simpleResult => PageHelper.getHtml(simpleResult))
		
		val historyStream = PageHelper.renderStream(historyHtmlFuture, "contributionsTab")
		val timelineStream = PageHelper.renderStream(timelineHtmlFuture, "timelineScript")
		
		HtmlStream.interleave(historyStream, timelineStream)
	}
}