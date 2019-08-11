package com.limitra.sdk.web.definition

import play.api.libs.json.Json

case class JsonResult(var Status: Option[Int] = None,
                      var Text: Option[String] = None,
                      var Notification: JsonResultNotify = null
                     )

object JsonResult {
  implicit val writes = Json.writes[JsonResult]
  implicit val reads = Json.reads[JsonResult]
}
