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
final class FormExtender[A](value: Form[A])(implicit request: Request[A]) {
  private def _defSuccess(formType: String): JsonResult = {
    return new JsonResult {
      Notification = new JsonResultNotify {
        Title = Response(request).Read(formType + "Title");
        Message = Response(request).Read("Ok")
      }
    }
  }

  private def _defError(formType: String): JsonResult = {
    return new JsonResult {
      Notification = new JsonResultNotify {
        Title = Response(request).Read(formType + "Title");
        Message = Response(request).Read("BadRequest")
      }
    }
  }

  def Post[B](call: (A) => Result, errCall: (Form[A]) => Result = null)(implicit request: Request[B]) = {
    value.bindFromRequest.fold(
      error => {
        if (error != null) {
          errCall(error)
        } else {
          Results.BadRequest(this._defError("Post").ToJson)
        }
      },
      form => {
        call(form)
      }
    )
  }

  def Put[B](call: (A) => Result, errCall: (Form[A]) => Result = null)(implicit request: Request[B]) = {
    value.bindFromRequest.fold(
      error => {
        if (error != null) {
          errCall(error)
        } else {
          Results.BadRequest(this._defError("Put").ToJson)
        }
      },
      form => {
        call(form)
      }
    )
  }
}
