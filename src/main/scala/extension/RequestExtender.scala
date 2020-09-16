package com.limitra.sdk.web.extension

import com.limitra.sdk.database.mysql.DbSource
import com.limitra.sdk.web._
import com.limitra.sdk.web.definition._
import play.api.libs.json.{JsValue, Writes}
import play.api.mvc.{Request, Result, Results}
import slick.lifted.{MappedProjection, Query}

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag
import scala.util.Try

/**
  * Extension methods for Request type.
  */
final class RequestExtender[A](request: Request[A]) {
  def ToDataTable[C, T, E](db: DbSource, query: Query[T, E, Seq])
                                    (projection: (T) => MappedProjection[C, E], sourceMap: (Seq[C]) => Seq[C] = null)
                                    (searchCall: (String) => Query[T, E, Seq] = null)
                                    (sortCall: (DataTableSort) => Query[T, E, Seq] = null)
                                    (filterCall: (DataTableFilter) => Query[T, E, Seq] = null)
                   (implicit tag: ClassTag[C], wr: Writes[C]): JsValue = {
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
    if (dataTable.Page.Number < 1) {
      dataTable.Page.Number = 1
    }

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
    if (dataTable.Page.Length < 1) {
      dataTable.Page.Length = 1
    }

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
      dataTable.Sort.reverse.foreach(sort => {
        source = sortCall(sort)
      })
    }

    dataTable.Data.Length = source.count.result.save

    val dropLen = dataTable.Page.Length * (dataTable.Page.Number - 1)
    val countLen = dataTable.Data.Length / dataTable.Page.Length
    dataTable.Page.Count = if(countLen == 0) 1 else {
      if(dataTable.Data.Length % dataTable.Page.Length > 0) countLen + 1 else countLen
    }

    var refSource = source.drop(dropLen).take(dataTable.Page.Length).map(data => projection(data)).result.save
    if (sourceMap != null) {
      refSource = sourceMap(refSource)
    }

    dataTable.Data.Source = refSource.ToJson
    dataTable.Page.Length = refSource.length

    dataTable.ToJson
  }

  def ToDataTableRef[C](db: DbSource, query: Query[_, _, Seq], sourceMap: (Seq[C]) => Seq[C] = null)
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
    if (dataTable.Page.Number < 1) {
      dataTable.Page.Number = 1
    }

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
    if (dataTable.Page.Length < 1) {
      dataTable.Page.Length = 1
    }

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
      dataTable.Sort.reverse.foreach(sort => {
        source = sortCall(sort)
      })
    }

    dataTable.Data.Length = source.count.result.save

    val dropLen = dataTable.Page.Length * (dataTable.Page.Number - 1)
    val countLen = dataTable.Data.Length / dataTable.Page.Length
    dataTable.Page.Count = if(countLen == 0) 1 else {
      if(dataTable.Data.Length % dataTable.Page.Length > 0) countLen + 1 else countLen
    }

    var refSource = source.drop(dropLen).take(dataTable.Page.Length).toRef[C]
    if (sourceMap != null) {
      refSource = sourceMap(refSource)
    }

    dataTable.Data.Source = refSource.ToJson
    dataTable.Page.Length = refSource.length

    dataTable.ToJson
  }

  def ToSelect[C, T, E](db: DbSource, query: Query[T, E, Seq])
                       (projection: (T) => MappedProjection[C, E], sourceMap: (Seq[C]) => Seq[C] = null)
                       (searchCall: (String) => Query[T, E, Seq] = null, textCall: (Seq[Long]) => Query[T, E, Seq] = null)
                       (implicit tag: ClassTag[C], wr: Writes[C]) = {
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

    val ids = request.queryString.get("ids").map(x => x.flatMap(y => Try(y.toLong).toOption)).getOrElse(Seq())
    if(textCall != null && ids.length > 0) {
      source = textCall(ids)
    }

    val dropLen = selectInput.Page.Length * (selectInput.Page.Number  - 1)
    val dataLen = source.count.result.save
    val countLen = dataLen / selectInput.Page.Length
    selectInput.Page.Count = if(countLen == 0) 1 else {
      if(selectInput.Data.Length % selectInput.Page.Length > 0) countLen + 1 else countLen
    }

    var refSource = source.drop(dropLen).take(selectInput.Page.Length).map(data => projection(data)).result.save
    if (sourceMap != null) {
      refSource = sourceMap(refSource)
    }

    selectInput.Data.Source = refSource.ToJson
    selectInput.Page.Length = refSource.length

    selectInput.ToJson
  }

  def ToSelectRef[C](db: DbSource, query: Query[_, _, Seq])
                    (sourceMap: (Seq[C]) => Seq[C] = null)
                    (searchCall: (String) => Query[_, _, Seq] = null, textCall: (Seq[Long]) => Query[_, _, Seq] = null)
                    (implicit tag: ClassTag[C], wr: Writes[C]) = {
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

    val ids = request.queryString.get("ids").map(x => x.flatMap(y => Try(y.toLong).toOption)).getOrElse(Seq())
    if(textCall != null && ids.length > 0) {
      source = textCall(ids)
    }

    val dropLen = selectInput.Page.Length * (selectInput.Page.Number  - 1)
    val dataLen = source.count.result.save
    val countLen = dataLen / selectInput.Page.Length
    selectInput.Page.Count = if(countLen == 0) 1 else {
      if(selectInput.Data.Length % selectInput.Page.Length > 0) countLen + 1 else countLen
    }

    var refSource = source.drop(dropLen).take(selectInput.Page.Length).toRef[C]
    if (sourceMap != null) {
      refSource = sourceMap(refSource)
    }

    selectInput.Data.Source = refSource.ToJson
    selectInput.Page.Length = refSource.length

    selectInput.ToJson
  }

  def ToBody: String = {
    request.domain
  }

  def RemoteAddress: String = {
    request.headers.get("X-Forwarded-For").getOrElse(request.remoteAddress)
  }

  def Host: String = {
    return request.headers.get("X-Forwarded-Host").getOrElse("localhost")
  }

  def Identities: Seq[Long] = {
    return request.queryString.get("ids").map(x => x.flatMap(y => Try(y.toLong).toOption)).getOrElse(Seq())
  }

  def Language: Option[String] = {
    return request.headers.get("Language")
  }

  def TimeZone: Option[Int] = {
    return request.headers.get("TimeZone").map(x => x.toInt)

  }

  def ToJsonResult(call: (JsonResult) => Result) = {
    val result = new JsonResult {
      Notification = Some(new JsonResultNotify {
        Status = NotifyStatus.Success;
        Title = Response(request).Read("Title");
        Message = Response(request).Read("Ok")
      })
    }
    call(result)
  }

  def ToJsonResultAsync(call: (JsonResult) => Result)(implicit ec: ExecutionContext) = {
    val result = new JsonResult {
      Notification = Some(new JsonResultNotify {
        Status = NotifyStatus.Success;
        Title = Response(request).Read("Title");
        Message = Response(request).Read("Ok")
      })
    }
    Future { call(result) }
  }

  def ToJwt: Option[JsonWebToken] =  {
    val header = request.headers.get("Authorization")
    if (header.isDefined && !header.isEmpty) {
      return Jwt.ReadToken(header.get)
    }
    return None
  }

  def ToRouteGuard[C](db: DbSource)
                     (homeCall: (String, Option[String]) => Query[_, _, Seq])
                     (routeCall: (String, String, Option[String]) => Query[_, _, Seq])
                     (errorCall: (String, Option[String]) => Query[_, _, Seq])
                     (authCall: (String) => Query[_, _, Seq] = null)(implicit tag: ClassTag[C], wr: Writes[C]) = {
    import db._

    val path = request.queryString.get("path").filter(x => !x.isEmpty).map(x => x.head).headOption.getOrElse("")
    val lang = request.queryString.get("lang").filter(x => !x.isEmpty).map(x => x.head).headOption

    val e401 = request.queryString.get("e401").filter(x => !x.isEmpty).map(x => x.head).headOption.getOrElse("401")
    val e404 = request.queryString.get("e404").filter(x => !x.isEmpty).map(x => x.head).headOption.getOrElse("404")

    val guard = new RouteGuard()

    if (homeCall != null && routeCall != null && errorCall != null) {
      var source: slick.lifted.Query[_, _, Seq] = null
      if (path == "/") {
        source = homeCall(path, lang)
      } else {
        source = routeCall(path, path.substring(1, path.length), lang)
      }

      val route = source.take(1).toRef[C].headOption
      if (route.isDefined) {
        var hasAuth = false
        if (authCall != null) {
          hasAuth = (authCall(path).length > 0).result.save
        } else { hasAuth = true }

        if (hasAuth) {
          guard.Data = route.get.ToJson
        } else {
          source = errorCall(e401, lang)
          val error = source.take(1).toRef[C].headOption
          if (error.isDefined) {
            guard.Error = error.get.ToJson
          }
        }
      } else {
        source = errorCall(e404, lang)
        val error = source.take(1).toRef[C].headOption
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
