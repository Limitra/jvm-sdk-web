package com.limitra.sdk.web.definition

import play.api.libs.json.Json

case class SelectInputPage(var Number: Long = 0,
                         var Count: Long = 0,
                         var Length: Long = 0)

object SelectInputPage {
  implicit val reads = Json.reads[SelectInputPage]
  implicit val writes = Json.writes[SelectInputPage]
}
