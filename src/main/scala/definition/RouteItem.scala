package com.limitra.sdk.web.definition

import play.api.libs.json.Json

case class RouteItem(
  var TypeName: String = "",
  var Route: String = "",
  var EndPoint: String = ""
)

object RouteItem {
  implicit val writes = Json.writes[RouteItem]
  implicit val reads = Json.reads[RouteItem]
}
