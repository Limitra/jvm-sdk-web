package com.limitra.sdk.web.provider

import com.limitra.sdk.core._
import play.api.libs.json.{JsValue, Json}

import java.io.File
import scala.io.Source

/**
 * It is used to make customized and localized http response messages.
 */
sealed class ResponseProvider(lang: Option[String]) {
  private val _app = Config("Application")
  private val _path = _app.OptionString("Root")
  private val _culture = _app.Get("Culture")
  private val _response = _app.Get("Text").OptionString("Response")
  private var _lang = lang.getOrElse("")
  if (!new File(_path.get + "/" + _response + "/" + _lang).exists()) {
    _lang = _culture.OptionString("DefaultLang").getOrElse("en-US")
  }
  private val _text: JsValue = if(_response.isDefined && _path.isDefined) Json.parse(Source.fromFile(_path.get + "/" + _response.get + "/" + _lang + ".json").getLines.mkString) else null

  def Read(key: String): String = {
    val text = if(_text != null) (_text \ key).asOpt[String] else None
    return text.getOrElse("-")
  }

  def ReadOption(key: String): Option[String] = {
    val text = if(_text != null) (_text \ key).asOpt[String] else None
    return text
  }
}
