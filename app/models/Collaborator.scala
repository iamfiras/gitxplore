package models

import scala.concurrent._
import ExecutionContext.Implicits.global

import play.api.libs.ws._
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

import utils.GithubAPI

case class Collaborator(login: String)

object Collaborator {
  
  implicit def parseCollaborators: Reads[Seq[Collaborator]] = {
    (__).read(
      seq(
        (__ \ "login").read[String]
      )
    ).map(
      _.collect {
        case login => Collaborator(login)
      }
    )
  }
  
  def get(fullname: String): Future[Seq[Collaborator]] = {
    WS.url(GithubAPI.collaborators.format(fullname))
    .get().map(
      r => r.status match {
        case 200 => r.json.asOpt[Seq[Collaborator]].getOrElse(Nil)
        case e => sys.error(s"Bad response. Status $e")
      }
    )
  }
}