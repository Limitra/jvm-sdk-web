package com.limitra.sdk.web.definition

import play.api.libs.json.Json

case class JsonResult(var ResponseText: String = "",
                      var Errors: Seq[JsonResultError] = Seq()
                     )

object JsonResult {
  implicit val writes = Json.writes[JsonResult]
  implicit val reads = Json.reads[JsonResult]
}
