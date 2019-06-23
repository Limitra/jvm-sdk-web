package com.limitra.sdk.web.definition

import play.api.libs.json.Json

case class JsonResultError(var Field: String = "",
                           var Message: String = ""
                          )

object JsonResultError {
  implicit val writes = Json.writes[JsonResultError]
  implicit val reads = Json.reads[JsonResultError]
}
