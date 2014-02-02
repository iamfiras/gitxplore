package mocks

import scala.concurrent._
import ExecutionContext.Implicits.global

import models.Commit

object CommitMock {

  def get(fullname: String, limit: Int): Future[Seq[Commit]] = {
    scala.concurrent.Future {
      List(
        Commit("commit 1", "sha 1", "url 1", "2013/01/23", "login 1"),
        Commit("commit 2", "sha 2", "url 2", "2013/01/23", "login 2"),
        Commit("commit 3", "sha 3", "url 3", "2013/01/23", "login 3"),
        Commit("commit 4", "sha 4", "url 4", "2013/01/23", "login 4")
      )
    }
  }

  def empty(fullname: String, limit: Int): Future[Seq[Commit]] = {
    scala.concurrent.Future { Nil }
  }
}