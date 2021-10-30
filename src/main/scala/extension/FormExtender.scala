package com.limitra.sdk.web.extension

import com.limitra.sdk.web._
import com.limitra.sdk.web.definition._
import play.api.data.FormBinding.Implicits._
import play.api.data._
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Extension methods for Form type.
  */
final class FormExtender[A](value: Form[A])(implicit request: Request[_]) {
  private def _defSuccess: JsonResult = {
    return new JsonResult {
      Notification = Some(new JsonResultNotify {
        Status = NotifyStatus.Success;
        Title = Response(request).Read("Title");
        Message = Response(request).Read("Ok")
      })
    }
  }

  private def _defError: JsonResult = {
    return new JsonResult {
      Notification = Some(new JsonResultNotify {
        Status = NotifyStatus.Danger;
        Title = Response(request).Read("Title");
        Message = Response(request).Read("BadRequest")
      })
    }
  }

  def ToJsonResult[B](call: (A, JsonResult) => Result, errCall: (Form[A], JsonResult) => Unit = null, js: Option[JsValue] = None)(implicit request: Request[B]) = {
    (if (js.isDefined) value.bind(js.get, Long.MaxValue) else value.bindFromRequest).fold(
      error => {
        val result = this._defError
        if (errCall != null) {
          errCall(error, result)
        }
        Results.BadRequest(result.ToJson)
      },
      form => { call(form, this._defSuccess) }
    )
  }

  def ToJsonResultAsync[B](call: (A, JsonResult) => Future[Result], errCall: (Form[A], JsonResult) => Unit = null, js: Option[JsValue] = None)(implicit request: Request[B], ec: ExecutionContext) = {
    (if (js.isDefined) value.bind(js.get, Long.MaxValue) else value.bindFromRequest).fold(
      error => {
        val result = this._defError
        if (errCall != null) {
          errCall(error, result)
        }
        Future { Results.BadRequest(result.ToJson) }
      },
      form => { call(form, this._defSuccess) }
    )
  }
}
