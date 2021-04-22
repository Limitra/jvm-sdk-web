package com.limitra.sdk.web.definition

import play.api.libs.json.Json

case class DataTablePage(var Number: Long = 0,
                         var Count: Long = 0,
                         var Length: Long = 0)

object DataTablePage {
  implicit val reads = Json.reads[DataTablePage]
  implicit val writes = Json.writes[DataTablePage]
}
