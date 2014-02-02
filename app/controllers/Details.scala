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
      collaborators <- Collaborator.get(reponame)
      commits <- Commit.get(reponame, 100)
      
      historySimpleResult <- getHistoryList(collaborators, commits)(request)
      timelineSimpleResult <- getTimeline(commits)(request)
      
      historyHtml <- PageHelper.getHtmlFrom(historySimpleResult)
      timelineHtml <- PageHelper.getHtmlFrom(timelineSimpleResult)
    } yield {
      Ok(views.html.details.index(historyHtml, timelineHtml))
    }
  }

  def getTimeline(commits: Seq[Commit]) = Action {
    Ok(views.html.details.timeline(commits))
  }

  def getHistoryList(collaborators: Seq[Collaborator], commits: Seq[Commit]) = Action {
    val historyList = collaborators map { collaborator =>
      (collaborator, commits.filter(_.committer == collaborator.login))
    }
    Ok(views.html.details.historyList(historyList))
  }
}