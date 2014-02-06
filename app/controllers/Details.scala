package controllers

import play.api._
import play.api.mvc._
import play.api.templates._

import scala.concurrent._
import ExecutionContext.Implicits.global

import mocks._
import models._
import utils.PageHelper

object Details extends Controller {

  def index(owner: String, repo: String) = Action.async { request =>
    val repofullname = owner + "/" + repo
    for {
      commits <- Commit.get(repofullname, 100)
      
      readmeSimpleResult <- getReadme(repofullname)(request)
      historySimpleResult <- getHistoryList(commits)(request)
      timelineSimpleResult <- getTimeline(commits)(request)
      
      readmeHtml <- PageHelper.getHtmlFrom(readmeSimpleResult)
      historyHtml <- PageHelper.getHtmlFrom(historySimpleResult)
      timelineHtml <- PageHelper.getHtmlFrom(timelineSimpleResult)
    } yield {
      Ok(views.html.details.index(repofullname, readmeHtml, historyHtml, timelineHtml))
    }
  }

  def getTimeline(commits: Seq[Commit]) = Action {
    Ok(views.html.details.timeline(commits))
  }

  def getHistoryList(commits: Seq[Commit]) = Action {
    val label = "%s (%2.2f%%, %d commits)"
    val grouped = commits groupBy { commit => commit.committer }
    val history = grouped map { commitList =>
      (label.format(commitList._1, (commitList._2.length.toDouble / commits.length.toDouble) * 100, commitList._2.length), commitList._2)
    }
    Ok(views.html.details.historyList(history.toList.sortBy(- _._2.length)))
  }

  def getReadme(repofullname: String) = Action.async {
    for {
      readme <- Readme.get(repofullname)
    } yield {
        Ok(Html(readme.content))
    }
  }
}