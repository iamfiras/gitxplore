package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._

import scala.concurrent._
import ExecutionContext.Implicits.global

import utils._
import models._

object Application extends Controller {

  def index = Action {
    Ok(views.html.search())
  }
  
  def search(q: String) = index
  
  def repo(author: String, reponame: String) = Action {
    Ok(views.html.repository())
  }
  
  implicit val repositoryAsJson = Json.writes[Repository]
  implicit val commitAsJson = Json.writes[Commit]
  implicit val contributorAsJson = Json.writes[Contributor]
  
  def searchjson(q: Option[String]) = Action.async {
    q match {
      case Some(query) => Repository.search(query).map {
        case r => Ok(Json.toJson(r))
      }
      case None => scala.concurrent.Future { Ok("") }
    }
  }
  
  def commitsjson(repofullname: Option[String], limit: Option[Int]) = Action.async {
    val max = limit.getOrElse(100)
    
    repofullname match {
      case Some(name) => Commit.get(name, max).map {
        case r => Ok(Json.toJson(r))
      }
      case None => scala.concurrent.Future { BadRequest("") }
    }
  }
  
  def contributorsjson(repofullname: Option[String]) = Action.async {
    repofullname match {
      case Some(name) => Contributor.get(name).map {
        case r => Ok(Json.toJson(r))
      }
      case None => scala.concurrent.Future { BadRequest("") }
    }
  }
}