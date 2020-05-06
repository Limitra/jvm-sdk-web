package com.limitra.sdk.web.composition

import com.limitra.sdk.database.mysql.DbSource
import com.limitra.sdk.web._
import com.limitra.sdk.web.definition._
import play.api.mvc._
import slick.lifted.Rep

import scala.concurrent._

sealed class Security(parser: BodyParser[AnyContent], header: (Option[String]) => Boolean)
                     (implicit ec: ExecutionContext) extends ActionBuilderImpl(parser) {
  var jresult = new JsonResult()

  override def invokeBlock[A](request: Request[A],
                              block: (Request[A]) => Future[Result]) = {
    val token = request.headers.get("SecurityToken")
    val secured = header(token)

    val forbidden = () => {
      jresult.Status = Some(403)
      jresult.SetText("Forbidden")(request)
      Future.successful(Results.Forbidden(jresult.ToJson))
    }

    if (secured) {
      block(request)
    } else {
      forbidden()
    }
  }
}
