package com.limitra.sdk.web.definition

import play.api.libs.json.Json

case class DataTableFilter(var Key: String = "", var Value: String = "")

object DataTableFilter {
  implicit val writes = Json.writes[DataTableFilter]
  implicit val reads = Json.reads[DataTableFilter]
}
