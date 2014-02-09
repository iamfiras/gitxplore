package controllers

import play.api._
import play.api.mvc._
import play.api.http.Status._
import play.api.templates._

import scala.concurrent._
import ExecutionContext.Implicits.global

import models._
import utils._
import utils.HtmlStreamImplicits._

object AggregateStream extends Controller {

	def index(owner: String, repo: String) = Action.async { request =>
		val repofullname = owner + "/" + repo
		Future { Ok.chunked(views.stream.details.chunked(repofullname, buildHtmlStream(repofullname, request))) }
	}

	def buildHtmlStream(repofullname: String, request: Request[AnyContent]): HtmlStream = {
    val commitsFuture = Commit.get(repofullname)
		val readmeHtmlFuture = Details.getReadme(repofullname)(request).flatMap(simpleResult => PageHelper.getHtml(simpleResult))
		val historyHtmlFuture = Details.getContributions(commitsFuture)(request).flatMap(simpleResult => PageHelper.getHtml(simpleResult))
		val timelineHtmlFuture = Details.getTimeline(commitsFuture)(request).flatMap(simpleResult => PageHelper.getHtml(simpleResult))

		val readmeStream = PageHelper.renderStream(readmeHtmlFuture, "readmeTab")		
		val historyStream = PageHelper.renderStream(historyHtmlFuture, "contributionsTab")
		val timelineStream = PageHelper.renderStream(timelineHtmlFuture, "timelineScript")

		HtmlStream.interleave(readmeStream, historyStream, timelineStream)
	}
}