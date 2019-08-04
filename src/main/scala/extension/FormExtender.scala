package com.limitra.sdk.web.extension

import com.limitra.sdk.web._
import com.limitra.sdk.web.definition._
import play.api.data.Form
import play.api.mvc.{Request, Result, Results}

/**
  * Extension methods for Form type.
  */
final class FormExtender[A](value: Form[A])(implicit request: Request[_]) {
  private val _response = Response(request)

  private def _defSuccess: JsonResult = {
    return new JsonResult {
      Notification = new JsonResultNotify {
        Status = NotifyStatus.Success;
        Title = _response.Read("Title");
        Message = _response.Read("Ok")
      }
    }
  }

  private def _defError: JsonResult = {
    return new JsonResult {
      Notification = new JsonResultNotify {
        Status = NotifyStatus.Danger;
        Title = _response.Read("Title");
        Message = _response.Read("BadRequest")
      }
    }
  }

  def Init[B](call: (A, JsonResult) => Result, errCall: (Form[A], JsonResult) => Unit = null)(implicit request: Request[B]) = {
    value.bindFromRequest.fold(
      error => {
        val result = this._defError
        if (error != null) {
          errCall(error, result)
        }
        Results.BadRequest(result.ToJson)
      },
      form => {
        call(form, this._defSuccess)
      }
    )
  }
}
