package be.thomastoye.speelsysteem.data.util

import scala.language.implicitConversions
import scala.concurrent.{Future, Promise}
import scalaz.{-\/, \/-}
import scalaz.concurrent.Task

object ScalazExtensions {
  implicit class PimpedScalazTask[T](task: Task[T]) {
    def toFuture: Future[T] = {
      val p: Promise[T] = Promise()
      task.unsafePerformAsync {
        case \/-(res) => p.success(res)
        case -\/(e) => p.failure(e)
      }
      p.future
    }
  }
}