package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent._
import ExecutionContext.Implicits.global

import utils.{Repository, Messages}
import mocks.RepositoryMock

object Search extends Controller {

  def index = Action {
    Ok(views.html.search())
  }

  implicit val repositoryAsJson = Json.writes[Repository]

  def results(q: Option[String]) = Action.async {
    q match {
      case Some(query) if (query.trim.length > 0) =>
        RepositoryMock.empty(query).map {
          case r => if (r.length > 0) Ok(views.html.results(r)) else Ok(views.html.messages.githubmsg(Messages.REPO_NOT_FOUND))
        }
      case _ => scala.concurrent.Future { Ok(views.html.messages.error(Messages.EMPTY_QUERY)) }
    }
  }
}