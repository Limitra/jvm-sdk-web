package com.limitra.sdk.web.definition

import play.api.libs.json.Json

case class FileUploadResult(var url: String = "",
                            var Path: String = "",
                            var Name: String = "",
                            var Type: Option[String] = None,
                            var Size: Long = 0)

object FileUploadResult {
  implicit val writes = Json.writes[FileUploadResult]
  implicit val reads = Json.reads[FileUploadResult]
}
