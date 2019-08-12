package com.limitra.sdk.web.definition

import com.limitra.sdk.web.Response
import play.api.libs.json.Json
import play.api.mvc.Request

case class JsonResult(var Status: Option[Int] = None,
                      var Text: Option[String] = None,
                      var Notification: Option[JsonResultNotify] = None
                     ) {
  def SetText(message: String)(implicit request: Request[_]): Unit = {
    this.Text = Response.ReadOption(message);
  }

  def SetTitle(title: String)(implicit request: Request[_]): Unit = {
    if (this.Notification.isDefined) {
      this.Notification.get.Title = Response.Read(title);
    }
  }

  def SetStatus(status: String)(implicit request: Request[_]): Unit = {
    if (this.Notification.isDefined) {
      this.Notification.get.Status = status;
    }
  }

  def SetMessage(message: String)(implicit request: Request[_]): Unit = {
    if (this.Notification.isDefined) {
      this.Notification.get.Message = Response.Read(message);
    }
  }
}

object JsonResult {
  implicit val writes = Json.writes[JsonResult]
  implicit val reads = Json.reads[JsonResult]
}
