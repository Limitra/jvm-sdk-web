package com.limitra.sdk.web.composition

import com.limitra.sdk.database.mysql.DbSource
import com.limitra.sdk.web._
import com.limitra.sdk.web.definition._
import play.api.mvc._
import slick.lifted.Query

import scala.concurrent._

sealed class Authorization(parser: BodyParser[AnyContent], db: DbSource, query: (JsonWebToken) => Query[_, String, Seq])
                          (implicit ec: ExecutionContext) extends ActionBuilderImpl(parser) {
  import db._
  var jresult = new JsonResult

  override def invokeBlock[A](request: Request[A],
                              block: (Request[A]) => Future[Result]) = {
    val jwt = request.ToJwt

    val unauthorized = () => {
      jresult.Status = Some(401)
      jresult.SetText("Unauthorized")(request)
      jresult.SetStatus(NotifyStatus.Danger)(request)
      jresult.SetTitle("Title")(request)
      jresult.SetMessage("Unauthorized")(request)
      Future.successful(Results.Unauthorized(jresult.ToJson))
    }

    if (jwt.isDefined && jwt.get.IsValid) {
      var valid: Boolean = false
      val routes: Seq[String] = this.query(jwt.get).result.save
      routes.foreach(route => {
        var uri = ""
        route.split('/').foreach(partial => {
          uri = uri + (if (partial.contains(":")) "/*.*" else "/" + partial)
        })
        uri = uri.replace("//", "/").replace("/", """\/""")
        if (request.path.matches(uri)) {
          valid = true
        }
      })
      if (valid) {
        block(request)
      } else { unauthorized() }
    } else { unauthorized() }
  }
}
