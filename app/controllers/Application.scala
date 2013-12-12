package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._

import scala.concurrent._
import ExecutionContext.Implicits.global

import utils._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index())
  }
  
  implicit val repositoryAsJson = Json.writes[Repository]
  
  def search(query: String) = Action.async {
    Repository.search(query).map {
      case r => Ok(Json.toJson(r))
    }
  }
}