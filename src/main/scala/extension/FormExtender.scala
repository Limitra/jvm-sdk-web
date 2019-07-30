package com.limitra.sdk.web.extension

import com.limitra.sdk.core._
import com.limitra.sdk.web._
import com.limitra.sdk.web.definition._
import play.api.data.Form
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Request, Result, Results}

import scala.io.Source

/**
  * Extension methods for Form type.
  */
final class FormExtender[A](value: Form[A]) {
  private val _response = Config("Text").OptionString("Response")
  private val _lang = Config("Culture").String("Lang")
  private val _text: JsValue = if(_response.isDefined) Json.parse(Source.fromFile(_response + "/" + _lang).getLines.mkString) else null

  private def _defSuccess(text: String): JsonResult = {
    return new JsonResult {
      ResponseText = if (text == null || text.isEmpty) (if(_text != null) (_text \ "Ok").as[String] else "-") else text
    }
  }

  private def _defError(text: String): JsonResult = {
    return new JsonResult {
      ResponseText = if (text == null || text.isEmpty) (if(_text != null) (_text \ "BadRequest").as[String] else "-") else text
    }
  }

  def ToResult[B](call: (A) => Unit, successText: String = "", errorText: String = "")(implicit request: Request[B]) = {
    value.bindFromRequest.fold(
      error => {
        Results.BadRequest(this._defError(errorText).ToJson)
      },
      form => {
        call(form)
        Results.Ok(this._defSuccess(successText).ToJson)
      }
    )
  }

  def ToStatus[B](call: (A) => Result, errorText: String = "")(implicit request: Request[B]) = {
    value.bindFromRequest.fold(
      error => {
        Results.BadRequest(this._defError(errorText).ToJson)
      },
      form => {
        call(form)
      }
    )
  }
}
