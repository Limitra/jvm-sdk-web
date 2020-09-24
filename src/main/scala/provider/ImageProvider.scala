package com.limitra.sdk.web.provider

import com.limitra.sdk.core._
import java.awt.Font
import java.io.{File, FileOutputStream}

import com.limitra.sdk.web
import com.sksamuel.scrimage.nio.{JpegWriter, PngWriter}
import com.sksamuel.scrimage.{Color, Image}

/**
 * It is used to make customized and image operations.
 */
sealed class ImageProvider {
  private val _config = Config("Application").Get("File")

  def Resize(width: Int, height: Int, path: String, ride: Boolean = false, compressForce: Boolean = false): Option[String] = {
    web.File._init()
    val source = this._config.OptionString("Path")
    if (source.isDefined) {
      var map = (source.get + "/" + path).replace("//", "/")
      val file = new java.io.File(map)
      var newPath = path
      if (file.exists()) {
        if (!ride && path.contains(".")) {
          val partials = path.split('.')
          newPath = path.replace("." + partials.last, "") + "_" + width.toString + "x" + height.toString + "." + partials.last
        }
        map = (source.get + "/" + newPath).replace("//", "/")
        val rgb = if (newPath.toLowerCase().endsWith("png") && !compressForce) Color.Transparent else Color.Black
        if (newPath.toLowerCase().endsWith("png") && !compressForce) {
          implicit val writer = PngWriter.MaxCompression
          Image.fromFile(file).fit(width, height, rgb).output(new File(map))
        } else {
          implicit val writer = JpegWriter().withCompression(50)
          Image.fromFile(file).fit(width, height, rgb).output(new File(map))
        }
        if (new File(map).exists()) {
          Some(newPath)
        } else { None }
      } else { None }
    } else { None }
  }

  def WriteText(path: String, text: String, size: Int, compressForce: Boolean = false): Boolean = {
    web.File._init()
    val source = this._config.OptionString("Path")
    if (source.isDefined) {
      val map = (source.get + "/" + path).replace("//", "/")
      val file = new java.io.File(map)
      if (file.exists()) {
        val font = new Font("Arial Black", Font.BOLD, size)
        val bufferedImage = Image.fromFile(file).toNewBufferedImage()
        val graphics = bufferedImage.getGraphics
        graphics.setColor(new java.awt.Color(255, 255, 255, 90))
        graphics.setFont(font)

        val metrics = graphics.getFontMetrics(font)
        val x = (bufferedImage.getWidth - metrics.stringWidth(text)) / 2
        val y = ((bufferedImage.getHeight() - metrics.getHeight) / 2) + metrics.getAscent

        graphics.drawString(text, x, y)
        if (path.toLowerCase().endsWith("png") && !compressForce) {
          implicit val writer = PngWriter.MaxCompression
          writer.write(bufferedImage, new FileOutputStream(file))
        } else {
          implicit val writer = JpegWriter().withCompression(50)
          writer.write(bufferedImage, new FileOutputStream(file))
        }
        true
      } else { false }
    } else { false }
  }
}
