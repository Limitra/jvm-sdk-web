package com.limitra.sdk.web.definition

import com.limitra.sdk.web._
import play.api.libs.json.Json
import play.api.mvc.Request

case class JsonResultNotify(var Title: String = "",
                            var Status: String = "",
                            var Message: String = "",
                            var AutoHide: Boolean = false,
                            var Delay: Int = 5000,
                            var Top: Option[Int] = None,
                            var Right: Option[Int] = None,
                            var Bottom: Option[Int] = None,
                            var Left: Option[Int] = None) {
  def SetTitle(title: String)(implicit request: Request[_]): Unit = {
    this.Title = Response.Read(title);
  }

  def SetStatus(status: String)(implicit request: Request[_]): Unit = {
    this.Status = Response.Read(status);
  }

  def SetMessage(message: String)(implicit request: Request[_]): Unit = {
    this.Message = Response.Read(message);
  }
}

object JsonResultNotify {
  implicit val writes = Json.writes[JsonResultNotify]
  implicit val reads = Json.reads[JsonResultNotify]
}
