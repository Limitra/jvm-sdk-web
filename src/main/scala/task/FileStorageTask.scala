package com.limitra.sdk.web.task

import java.io.File

import akka.actor.ActorSystem
import javax.inject.Inject
import org.joda.time.DateTime

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class FileStorageTask @Inject()(actorSystem: ActorSystem)(implicit executionContext: ExecutionContext) {
  private val _config = Config("Application").Get("File")

  actorSystem.scheduler.schedule(initialDelay = 10.seconds, interval = 10.seconds) {
    val source = this._config.OptionString("Path")
    if (source.isDefined) {
      val temp = (source.get + "/temp").replace("//", "/")
      val folder = new File(temp)
      if (folder.exists && folder.isDirectory) {
        folder.listFiles.toList.foreach(file => {
          if (file.isDirectory) {
            file.listFiles.toList.foreach(sub => {
              this._deleteFile(sub)
            })
          } else {
            this._deleteFile(file)
          }
        })
      }
    }
  }

  private def _deleteFile(file: File) {
    if (file.lastModified < DateTime.now.minusMinutes(30).getMillis) {
      file.delete()
    }
  }
}
