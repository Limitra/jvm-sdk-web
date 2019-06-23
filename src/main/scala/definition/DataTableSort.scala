package com.limitra.sdk.web.definition

import play.api.libs.json.Json

case class DataTableSort(var Field: String = "",
                               var Direction: String = "")

object DataTableSort {
  implicit val reads = Json.reads[DataTableSort]
  implicit val writes = Json.writes[DataTableSort]
}
