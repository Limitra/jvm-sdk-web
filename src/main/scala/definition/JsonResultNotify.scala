package com.limitra.sdk.web.definition

import play.api.libs.json.Json

case class JsonResultNotify(var Title: String = "",
                            var Status: String = "",
                            var Message: String = "",
                            var AutoHide: Boolean = false,
                            var Delay: Int = 5000,
                            var Top: Option[Int] = None,
                            var Right: Option[Int] = None,
                            var Bottom: Option[Int] = None,
                            var Left: Option[Int] = None)

object JsonResultNotify {
  implicit val writes = Json.writes[JsonResultNotify]
  implicit val reads = Json.reads[JsonResultNotify]
}
