package com.limitra.sdk.web.definition

import play.api.libs.json.{JsValue, Json}


case class RouteGuard(var Error: JsValue = Json.toJson(""),
                      var Data: JsValue = Json.toJson(""))
object RouteGuard {
  implicit val reads = Json.reads[RouteGuard]
  implicit val writes = Json.writes[RouteGuard]
}
