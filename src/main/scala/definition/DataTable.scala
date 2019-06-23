package com.limitra.sdk.web.definition

import play.api.libs.json.Json


case class DataTable(var Search: Option[String] = None,
                     var Page: DataTablePage = new DataTablePage,
                     var Data: DataTableData = new DataTableData,
                     var Sort: Seq[DataTableSort] = Seq(),
                     var Filter: Seq[DataTableFilter] = Seq())
object DataTable {
  implicit val reads = Json.reads[DataTable]
  implicit val writes = Json.writes[DataTable]
}
