package com.limitra.sdk.web.composition

import com.limitra.sdk.web._
import com.limitra.sdk.database.mysql.DbSource
import com.limitra.sdk.web.definition._
import play.api.mvc._
import slick.lifted.Rep

import scala.concurrent._

sealed class Authentication(parser: BodyParser[AnyContent], db: DbSource, query: (JsonWebToken) => Rep[Boolean])
                               (implicit ec: ExecutionContext) extends ActionBuilderImpl(parser) {
  import db._
  var jresult = new JsonResult()

  override def invokeBlock[A](request: Request[A],
                              block: (Request[A]) => Future[Result]) = {
    val jwt = request.ToJwt

    val unauthorized = () => {
      jresult.Status = Some(401)
      jresult.SetText("Unauthorized")(request)
      Future.successful(Results.Unauthorized(jresult.ToJson))
    }

    if (jwt.isDefined && jwt.get.IsValid) {
      val valid = this.query(jwt.get).result.Save
      if (valid) {
        block(request)
      } else { unauthorized() }
    } else { unauthorized() }
  }
}
