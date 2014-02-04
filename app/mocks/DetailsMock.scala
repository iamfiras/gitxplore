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

      historySimpleResult <- getHistoryList(commits)(request)
      timelineSimpleResult <- getTimeline(commits)(request)

      historyHtml <- PageHelper.getHtml(historySimpleResult)
      timelineHtml <- PageHelper.getHtml(timelineSimpleResult)
    } yield {
      Ok(views.html.details.index(reponame, historyHtml, timelineHtml))
    }
  }

	def stream(repo: String, name: String) = Action.async { request =>
		val reponame = repo + "/" + name
		CommitMock.get(reponame, 100).map { commits =>
			Ok.chunked(views.stream.details.chunked(reponame, buildHtmlStream(commits, request)))
		}
	}

	def buildHtmlStream(commits: Seq[Commit], request: Request[AnyContent]): HtmlStream = {
		val historyHtmlFuture = getHistoryList(commits)(request).flatMap(simpleResult => PageHelper.getHtml(simpleResult))
		val timelineHtmlFuture = getTimeline(commits)(request).flatMap(simpleResult => PageHelper.getHtml(simpleResult))
		
		val historyStream = PageHelper.renderStream(historyHtmlFuture, "contributionsTab")
		val timelineStream = PageHelper.renderStream(timelineHtmlFuture, "timelineScript")
		
		HtmlStream.interleave(historyStream, timelineStream)
	}

  def getTimeline(commits: Seq[Commit]) = Action {
    Ok(views.html.details.timeline(commits))
  }

  def getHistoryList(commits: Seq[Commit]) = Action.async {
    val label = "%s (%2.2f%%, %d commits)"
    val grouped = commits groupBy { commit => commit.committer }
    val history = grouped map { commitList =>
      (label.format(commitList._1, (commitList._2.length.toDouble / commits.length.toDouble) * 100, commitList._2.length), commitList._2)
    }
	  Promise.timeout(Ok(views.html.details.historyList(history.toList.sortBy(- _._2.length))), 1000)
  }
}