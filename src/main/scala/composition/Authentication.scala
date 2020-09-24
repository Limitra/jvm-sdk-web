package com.limitra.sdk.web.composition

import com.limitra.sdk.web._
import com.limitra.sdk.web.definition._
import play.api.mvc._

import scala.concurrent._

sealed class Authentication(parser: BodyParser[AnyContent], db: DbSource, query: (JsonWebToken) => Rep[Boolean])
                               (implicit ec: ExecutionContext) extends ActionBuilderImpl(parser) {
  var jresult = new JsonResult()

  override def invokeBlock[A](request: Request[A],
                              block: (Request[A]) => Future[Result]) = {
    val jwt = request.ToJwt

    val forbidden = () => {
      jresult.Status = Some(403)
      jresult.SetText("Forbidden")(request)
      Future.successful(Results.Forbidden(jresult.ToJson))
    }

    if (jwt.isDefined && jwt.get.IsValid) {
      val valid = this.query(jwt.get).result.save
      if (valid) {
        block(request)
      } else { forbidden() }
    } else { forbidden() }
  }
}
