package com.limitra.sdk.web.definition

import play.api.libs.json.{JsValue, Json}

case class DataTableData(var Length: Long = 0,
                         var Source: JsValue = Json.toJson(""))

object DataTableData {
  implicit val reads = Json.reads[DataTableData]
  implicit val writes = Json.writes[DataTableData]
}
