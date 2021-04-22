package com.limitra.sdk.web.definition

import play.api.libs.json.{JsValue, Json}

case class SelectInputData(var Length: Long = 0,
                           var Values: Seq[Long] = Seq(),
                         var Source: JsValue = Json.toJson(""))

object SelectInputData {
  implicit val reads = Json.reads[SelectInputData]
  implicit val writes = Json.writes[SelectInputData]
}
