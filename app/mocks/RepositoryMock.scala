package mocks

import scala.concurrent._
import ExecutionContext.Implicits.global

import models.Repository

object RepositoryMock {
  
  def search(query: String): Future[Seq[Repository]] = {
    scala.concurrent.Future {
      List(
        Repository("repo 1", 5),
        Repository("repo 2", 20),
        Repository("repo 3", 0),
        Repository("repo 4", 654)
      )
    }
  }
  
  def empty(query: String): Future[Seq[Repository]] = {
    scala.concurrent.Future { Nil }
  }
}