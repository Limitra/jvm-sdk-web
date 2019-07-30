package com.limitra.sdk.web.definition

import play.api.libs.json.Json

case class JsonResult(var ResponseText: String = "",
                      var Notification: JsonResultNotify = new JsonResultNotify()
                     )

object JsonResult {
  implicit val writes = Json.writes[JsonResult]
  implicit val reads = Json.reads[JsonResult]
}
