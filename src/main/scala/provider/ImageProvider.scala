package com.limitra.sdk.web.provider

import java.io.File

import com.limitra.sdk.core.Config
import com.limitra.sdk.web
import com.sksamuel.scrimage.{Color, Image}

/**
 * It is used to make customized and image operations.
 */
sealed class ImageProvider {
  private val _config = Config("Application").Get("File")

  def Resize(width: Int, height: Int, path: String, ride: Boolean = false): Option[String] = {
    web.File._init()
    val source = this._config.OptionString("Path")
    if (source.isDefined) {
      var map = (source.get + "/" + path).replace("//", "/")
      val file = new java.io.File(map)
      if (file.exists()) {
        if (ride && map.contains(".")) {
          val partials = map.split('.')
          if (partials.length == 2) {
            map = partials(0) + "_" + width.toString + "x" + height.toString + "." + partials(1)
          }
        }
        Image.fromFile(file).fit(width, height, Color.Black).output(new File(map))
        if (new File(map).exists()) {
          Some(map)
        } else { None }
      } else { None }
    } else { None }
  }
}
