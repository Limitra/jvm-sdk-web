package com.limitra.sdk.web.definition

import play.api.libs.json.Json

case class SelectInput(var Search: Option[String] = None,
                     var Page: SelectInputPage = new SelectInputPage,
                     var Data: SelectInputData = new SelectInputData)

object SelectInput {
  implicit val reads = Json.reads[SelectInput]
  implicit val writes = Json.writes[SelectInput]
}
