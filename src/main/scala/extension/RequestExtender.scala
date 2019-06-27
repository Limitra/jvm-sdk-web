package com.limitra.sdk.web.extension

import com.limitra.sdk.database.mysql.DbSource
import com.limitra.sdk.web._
import com.limitra.sdk.web.definition.{DataTable, DataTableFilter, DataTableSort}
import play.api.libs.json.Writes
import play.api.mvc.{Request, Results}
import slick.lifted.Query

import scala.reflect.ClassTag
import scala.util.Try

/**
  * Extension methods for Request type.
  */
final class RequestExtender[A](request: Request[A]) {
  def ToBody: String = {
    request.domain
  }

  def ToDataTable[C](db: DbSource, query: Query[_, _, Seq])
                    (searchCall: (String) => Query[_, _, Seq] = null)
                    (sortCall: (DataTableSort) => Query[_, _, Seq] = null)
                    (filterCall: (DataTableFilter) => Query[_, _, Seq] = null)(implicit tag: ClassTag[C], wr: Writes[C]) = {
    import db._
    val dataTable = new DataTable
    dataTable.Search = request.queryString.get("search").flatMap(x => x.headOption)

    val keys = request.queryString.get("keys")
    val values = request.queryString.get("values")

    if(keys.isDefined && values.isDefined) {
      keys.get.foreach(key => {
        val index = keys.get.indexOf(key)

        val value = if(values.get.length > index) Some(values.get(index)) else None
        if(value.isDefined) {
          dataTable.Filter = dataTable.Filter :+ (new DataTableFilter() { Key = key; Value = value.get })
        }
      })
    }

    dataTable.Page.Number = request.queryString.get("page").flatMap(x => x.flatMap(y => Try(y.toLong).toOption).headOption).getOrElse(1.toLong)
    dataTable.Sort = request.queryString.get("sort").map(x => x.map(y => {
      val dtSort = new DataTableSort
      if (y.contains(",")) {
        val partials = y.split(',')
        dtSort.Field = partials(0)
        dtSort.Direction = partials(1)
      }
      dtSort
    })).getOrElse(Seq())

    dataTable.Page.Length = request.queryString.get("length").flatMap(x => x.flatMap(y => Try(y.toLong).toOption).headOption).getOrElse(1.toLong)

    var source = query

    if(searchCall != null && dataTable.Search.isDefined) {
      source = searchCall(dataTable.Search.get)
    }

    if(filterCall != null) {
      dataTable.Filter.foreach(filter => {
        source = filterCall(filter)
      })
    }

    if(sortCall != null) {
      dataTable.Sort.foreach(sort => {
        source = sortCall(sort)
      })
    }

    dataTable.Data.Length = source.CountVal

    val dropLen = dataTable.Page.Length * (dataTable.Page.Number - 1)
    val countLen = dataTable.Data.Length / dataTable.Page.Length
    dataTable.Page.Count = if(countLen == 0) 1 else {
      if(dataTable.Data.Length % dataTable.Page.Length > 0) countLen + 1 else countLen
    }

    val refSource = source.drop(dropLen).take(dataTable.Page.Length).ToRef[C]
    dataTable.Data.Source = refSource.ToJson
    dataTable.Page.Length = refSource.length

    Results.Ok(dataTable.ToJson)
  }
}
