package controllers

import play.api._
import play.api.mvc._
import play.api.http.Status._
import play.api.templates._

import scala.concurrent._
import ExecutionContext.Implicits.global

import models._
import utils.PageHelper

object Aggregate extends Controller {

  def index(owner: String, repo: String) = Action.async { request =>
    val repofullname = owner + "/" + repo
    val commitsFuture = Commit.get(repofullname)
    for {
      readmeSimpleResult <- Details.getReadme(repofullname)(request)
      contributionsSimpleResult <- Details.getContributions(commitsFuture)(request)
      timelineSimpleResult <- Details.getTimeline(commitsFuture)(request)

      readmeHtml <- PageHelper.getHtml(readmeSimpleResult)
      contributionsHtml <- PageHelper.getHtml(contributionsSimpleResult)
      timelineHtml <- PageHelper.getHtml(timelineSimpleResult)
    } yield {
      val readmeOption = readmeSimpleResult.header.status match {
        case OK => Some(readmeHtml)
        case _ => None
      }
      Ok(views.html.details.index(repofullname, readmeOption, contributionsHtml, timelineHtml))
    }
  }
}