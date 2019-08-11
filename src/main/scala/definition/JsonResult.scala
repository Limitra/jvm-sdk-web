package com.limitra.sdk.web.definition

import play.api.libs.json.Json

case class JsonResult(var ResponseText: String = "",
                      var ResponseToken: String = "",
                      var Notification: JsonResultNotify = null
                     )

object JsonResult {
  implicit val writes = Json.writes[JsonResult]
  implicit val reads = Json.reads[JsonResult]
}
