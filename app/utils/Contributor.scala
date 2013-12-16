package utils

import scala.concurrent._
import ExecutionContext.Implicits.global

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

import play.api.libs.ws._

case class Contributor(login: String)

object Contributor {
  
  implicit def parseContributor: Reads[Seq[Contributor]] = {
    (__).read(
      seq(
        (__ \ "login").read[String]
      )
    ).map(
      _.collect {
        case login => Contributor(login)
      }
    )
  }
  
  def get(fullname: String): Future[Seq[Contributor]] =
    WS.url(GithubAPI.contributors.format(fullname))
    .get().map(
      r => r.status match {
        case 200 => r.json.asOpt[Seq[Contributor]].getOrElse(Nil)
        case e => sys.error(s"Bad response. Status $e")
      }
    )
}