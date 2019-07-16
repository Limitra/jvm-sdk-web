package com.limitra.sdk.web.extension

import com.limitra.sdk.database.mysql.DbSource
import com.limitra.sdk.web._
import com.limitra.sdk.web.definition.{DataTable, DataTableFilter, DataTableSort, RouteGuard, SelectInput}
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
    dataTable.Search = request.queryString.get("search").filter(x => !x.isEmpty).map(x => x.head).headOption

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

  def ToSelectInput[C](db: DbSource, query: Query[_, _, Seq])
                    (searchCall: (String) => Query[_, _, Seq] = null)(implicit tag: ClassTag[C], wr: Writes[C]) = {
    import db._

    val selectInput = new SelectInput

    selectInput.Search = request.queryString.get("search").filter(x => !x.isEmpty).map(x => x.head).headOption
    selectInput.Page.Number = request.queryString.get("page").flatMap(x => x.flatMap(y => Try(y.toLong).toOption).headOption).getOrElse(1.toLong)
    selectInput.Page.Length = request.queryString.get("length").flatMap(x => x.flatMap(y => Try(y.toLong).toOption).headOption).getOrElse(1.toLong)
    selectInput.Data.Values = request.queryString.get("values").map(x => x.flatMap(y => Try(y.toLong).toOption)).getOrElse(Seq())

    var source = query
    if(searchCall != null && selectInput.Search.isDefined) {
      source = searchCall(selectInput.Search.get)
    }

    val dropLen = selectInput.Page.Length * (selectInput.Page.Number  - 1)
    val dataLen = source.CountVal
    val countLen = dataLen / selectInput.Page.Length
    selectInput.Page.Count = if(countLen == 0) 1 else {
      if(selectInput.Data.Length % selectInput.Page.Length > 0) countLen + 1 else countLen
    }

    val refSource = source.drop(dropLen).take(selectInput.Page.Length).ToRef[C]
    selectInput.Data.Source = refSource.ToJson
    selectInput.Page.Length = refSource.length

    Results.Ok(selectInput.ToJson)
  }

  def ToRouteGuard[C](db: DbSource)
                     (homeCall: (String, Option[String]) => Query[_, _, Seq])
                     (routeCall: (String, String, Option[String]) => Query[_, _, Seq])
                     (errorCall: (String, Option[String]) => Query[_, _, Seq])(implicit tag: ClassTag[C], wr: Writes[C]) = {
    import db._

    val path = request.queryString.get("path").filter(x => !x.isEmpty).map(x => x.head).headOption.getOrElse("")
    val lang = request.queryString.get("lang").filter(x => !x.isEmpty).map(x => x.head).headOption

    val guard = new RouteGuard()

    if (homeCall != null && routeCall != null && errorCall != null) {
      var source: slick.lifted.Query[_, _, Seq] = null
      if (path == "/") {
        source = homeCall(path, lang)
      } else {
        source = routeCall(path, path.substring(1, path.length), lang)
      }

      val route = source.take(1).ToRef[C].headOption
      if (route.isDefined) {
        guard.Data = route.get.ToJson
      } else {
        source = errorCall("not-found", lang)
        val error = source.take(1).ToRef[C].headOption
        if (error.isDefined) {
          guard.Error = error.get.ToJson
        }
      }

      Results.Ok(guard.ToJson)
    } else {
      Results.NotImplemented("Error: Not implemented")
    }
  }
}
