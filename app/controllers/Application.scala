package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._

import scala.concurrent._
import ExecutionContext.Implicits.global

import utils._

object Application extends Controller {

  def index = Action {
    Ok(views.html.search())
  }
  
  implicit val repositoryAsJson = Json.writes[Repository]
  
  def search(q: Option[String]) = Action.async {
    q match {
      case Some(query) => Repository.search(query).map {
        case r => Ok(Json.toJson(r))
      }
      case None => scala.concurrent.Future { Ok("") }
    }
  }
  
  def searchjson(q: Option[String]) = Action.async {
    q match {
      case Some(query) => Repository.search(query).map {
        case r => Ok(Json.toJson(r))
      }
      case None => scala.concurrent.Future { Ok("") }
    }
  }
}