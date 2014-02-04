package controllers

import play.api._
import play.api.mvc._

import scala.concurrent._
import ExecutionContext.Implicits.global

import mocks._
import models._
import utils.PageHelper

object Details extends Controller {

  def index(repo: String, name: String) = Action.async { request =>
    val reponame = repo + "/" + name
    for {
      commits <- Commit.get(reponame, 100)
      
      historySimpleResult <- getHistoryList(commits)(request)
      timelineSimpleResult <- getTimeline(commits)(request)
      
      historyHtml <- PageHelper.getHtml(historySimpleResult)
      timelineHtml <- PageHelper.getHtml(timelineSimpleResult)
    } yield {
      Ok(views.html.details.index(reponame, historyHtml, timelineHtml))
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
}