package com.limitra.sdk.web.extension

import com.limitra.sdk.core._
import com.limitra.sdk.web._
import com.limitra.sdk.web.definition._
import play.api.data.Form
import play.api.mvc.{Request, Result, Results}

/**
  * Extension methods for Form type.
  */
final class FormExtender[A](value: Form[A]) {
  private val _textConfig = Config("Response").Get("Text")

  private def _defSuccess(text: String): JsonResult = {
    return new JsonResult {
      ResponseText = if (text == null || text.isEmpty) _textConfig.OptionString("Ok").getOrElse("Empty") else text
    }
  }

  private def _defError(text: String): JsonResult = {
    return new JsonResult {
      ResponseText = if (text == null || text.isEmpty) _textConfig.OptionString("BadRequest").getOrElse("Empty") else text
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
