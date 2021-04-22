package com.limitra.sdk.web.provider

import com.limitra.sdk.core._

import java.io.File

/**
 * It is used to make customized and file upload operations.
 */
sealed class FileProvider {
  private val _config = Config("Application").Get("File")

  def _init(): Unit = {
    val source = this._config.OptionString("Path")
    val folders = Seq("Image", "Audio", "Video", "Document")

    if (source.isDefined) {
      folders.foreach(folder => {
        val fold = this._config.Get(folder).OptionString("Folder")
        if (fold.isDefined) {
          val file = new File(source.get + "/" + fold.get)
          if (!file.exists()) {
            file.mkdirs()
          }
        }
      })
    }
  }

  def Size(path: String): Long = {
    val source = this._config.OptionString("Path")
    if (source.isDefined) {
      val map = (source.get + "/" + path).replace("//", "/")
      val file = new java.io.File(map)
      if (file.exists()) {
        file.length
      } else { 0 }
    } else { 0 }
  }

  def Delete(path: String): Boolean = {
    val source = this._config.OptionString("Path")
    if (source.isDefined) {
      val map = (source.get + "/" + path).replace("//", "/")
      val file = new java.io.File(map)
      if (file.exists()) {
        file.delete()
      } else { false }
    } else { false }
  }

  def Delete(path: Option[String]): Boolean = {
    if (path.isDefined) {
      this.Delete(path.get)
    } else { false }
  }

  def Move(path: String): Boolean = {
    this._init()
    val source = this._config.OptionString("Path")
    if (source.isDefined) {
      var map = (source.get + "/temp/" + path).replace("//", "/")
      val file = new java.io.File(map)
      if (file.exists()) {
        map = map.replace("/temp", "")
        if (file.renameTo(new File(map))) {
          true
        } else { false }
      } else { false }
    } else { false }
  }

  def Move(path: Option[String]): Boolean = {
    if (path.isDefined) {
      this.Move(path.get)
    } else { false }
  }
}
