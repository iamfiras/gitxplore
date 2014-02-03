package controllers

import play.api._
import play.api.mvc._
import scala.concurrent._
import ExecutionContext.Implicits.global

import models.Repository
import mocks.RepositoryMock
import utils.{Messages, PageHelper}

object Search extends Controller {

  def index = Action {
    Ok(views.html.search.index())
  }

  def search(q: Option[String]) = Action.async { request =>
    for {
      resultsSimpleResult <- results(q)(request)
      resultsHtml <- PageHelper.getHtmlFrom(resultsSimpleResult)
    } yield {
      Ok(views.html.search.index(q.getOrElse(""), resultsHtml))
    }
  }

  def results(q: Option[String]) = Action.async {
    val query = q.getOrElse("").trim
    if (query.length > 0) {
      Repository.search(query).map {
        case r if (r.length > 0) => Ok(views.html.search.results(r))
        case _ => Ok(views.html.messages.github(Messages.REPO_NOT_FOUND))
      }
    } else {
      Future { Ok(views.html.messages.error(Messages.EMPTY_QUERY)) }
    }
  }
}