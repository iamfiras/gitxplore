package models

import scala.concurrent._
import ExecutionContext.Implicits.global

import play.api.libs.ws._
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

import utils.GithubAPI

case class Readme(content: String)

object Readme {

  def get(repofullname: String): Future[Readme] = {
    WS.url(GithubAPI.readme.format(repofullname))
    .withHeaders("Accept" -> "application/vnd.github.v3.html+json")
    .get().map(
      r => r.status match {
        case 200 => Readme(r.body)
        case e => sys.error(s"Bad response. Status $e")
      }
    )
  }
}