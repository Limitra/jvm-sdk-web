package com.limitra.sdk.web.definition

import com.limitra.sdk.web.Response
import play.api.libs.json.Json
import play.api.mvc.Request

case class JsonResult(var Status: Option[Int] = None,
                      var Text: Option[String] = None,
                      var Notification: JsonResultNotify = null
                     ) {
  def SetText(message: String)(implicit request: Request[_]): Unit = {
    this.Text = Response.ReadOption(message);
  }
}

object JsonResult {
  implicit val writes = Json.writes[JsonResult]
  implicit val reads = Json.reads[JsonResult]
}
