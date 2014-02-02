package mocks

import scala.concurrent._
import ExecutionContext.Implicits.global

import models.Collaborator

object CollaboratorMock {

  def get(fullname: String): Future[Seq[Collaborator]] = {
    scala.concurrent.Future {
      List(
        Collaborator("login 1"),
        Collaborator("login 2"),
        Collaborator("login 3"),
        Collaborator("login 4")
      )
    }
  }

  def empty(fullname: String): Future[Seq[Collaborator]] = {
    scala.concurrent.Future { Nil }
  }
}