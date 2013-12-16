package utils

import scala.concurrent._
import ExecutionContext.Implicits.global

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

import play.api.libs.ws._

case class Commit(sha: String, url: String, date: String, commiter: String)

object Commit {
  
  implicit def parseCommit: Reads[Seq[Commit]] = {
    (__).read(
      seq(
        (__ \ "url").read[String] and
        (__ \ "sha").read[String] and
        (__ \ "commit" \ "committer" \ "date").read[String] and
        (__ \ "committer" \ "login").read[String]
        tupled
      )
    ).map(
      _.collect {
        case (url, sha, date, login) => Commit(sha, url, date, login)
      }
    )
  }
  
  def get(fullname: String): Future[Seq[Commit]] =
    WS.url(GithubAPI.commits.format(fullname))
    .get().map(
      r => r.status match {
        case 200 => r.json.asOpt[Seq[Commit]].getOrElse(Nil)
        case e => sys.error(s"Bad response. Status $e")
      }
    )
}