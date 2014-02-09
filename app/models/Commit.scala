package models

import scala.concurrent._
import ExecutionContext.Implicits.global

import play.api.libs.ws._
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

import utils.GithubAPI

case class Commit(var message: String, sha: String, url: String, date: String, committer: String) {
  message = message.filter(_ >= ' ') // Control characters (\n, \t etc.) are all lower than the space character. => we filter them
}

object Commit {

  implicit def parseCommit: Reads[Seq[Commit]] = {
    (__).read(
      seq(
        (__ \ "commit" \ "message").readNullable[String] and
        (__ \ "html_url").read[String] and
        (__ \ "sha").read[String] and
        (__ \ "commit" \ "committer" \ "date").read[String] and
        (__ \ "committer" \ "login").readNullable[String]
        tupled
      )
    ).map(
      _.collect {
        case (Some(message), url, sha, date, Some(login)) => Commit(message, sha, url, date, login)
      }
    )
  }

  def get(repofullname: String, limit: Int = 100): Future[Seq[Commit]] = {
    WS.url(GithubAPI.commits.format(repofullname, limit))
    .get().map(
      r => r.status match {
        case 200 => r.json.asOpt[Seq[Commit]].getOrElse(Nil)
        case e => sys.error(s"Bad response. Status $e")
      }
    )
  }
}