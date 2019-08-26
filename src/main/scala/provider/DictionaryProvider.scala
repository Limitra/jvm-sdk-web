package com.limitra.sdk.web.provider

import java.io.File

import com.limitra.sdk.core.Config
import play.api.libs.json.{JsValue, Json}

import scala.io.Source

/**
 * It is used to make customized and localized key mapped words.
 */
sealed class DictionaryProvider(lang: Option[String]) {
  private val _app = Config("Application")
  private val _path = _app.OptionString("Root")
  private val _response = _app.Get("Text").OptionString("Dictionary")
  private var _lang = lang.getOrElse("")
  if (!new File(_path.get + "/" + _response + "/" + _lang).exists()) {
    _lang = _app.OptionString("DefaultLang").getOrElse("en-US")
  }
  private val _text: JsValue = if(_response.isDefined && _path.isDefined) Json.parse(Source.fromFile(_path.get + "/" + _response.get + "/" + _lang).getLines.mkString) else null

  def Language: String = {
    return _lang
  }

  def Read(key: String): String = {
    var text: Option[String] = None
    if (lang.isDefined) {
      text = if(_text != null) (_text \ key).asOpt[String] else None
    }
    text.getOrElse("-")
  }

  def ReadOption(key: String): Option[String] = {
    var text: Option[String] = None
    if (lang.isDefined) {
      text = if(_text != null) (_text \ key).asOpt[String] else None
    }
    return text
  }
}
