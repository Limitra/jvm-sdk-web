package com.limitra.sdk.web.composition

import com.limitra.sdk.database.mysql.DbSource
import com.limitra.sdk.web.{definition, _}
import com.limitra.sdk.web.definition._
import play.api.mvc._
import slick.lifted.Rep

import scala.concurrent._

sealed class Security(parser: BodyParser[AnyContent], check: (Option[String], definition.RequestInfo) => Boolean)
                     (implicit ec: ExecutionContext) extends ActionBuilderImpl(parser) {
  var jresult = new JsonResult()

  override def invokeBlock[A](request: Request[A],
                              block: (Request[A]) => Future[Result]) = {
    val token = request.headers.get("SecurityToken")
    val jwt = request.ToJwt

    val reqInfo = new definition.RequestInfo
    reqInfo.UserAgent = request.headers.get("UserAgent")
    if (!reqInfo.UserAgent.isDefined) {
      reqInfo.UserAgent = request.headers.get("User-Agent")
    }
    reqInfo.Browser = request.headers.get("Browser")
    reqInfo.BrowserVersion = request.headers.get("BrowserVersion")
    reqInfo.Device = request.headers.get("Device")
    reqInfo.DeviceType = request.headers.get("DeviceType")
    reqInfo.OS = request.headers.get("OS")
    reqInfo.OSVersion = request.headers.get("OSVersion")


    reqInfo.Millis = DateTime(request).now.getMillis
    reqInfo.RemoteAddress = request.RemoteAddress
    reqInfo.Path = request.path
    reqInfo.Header = request.headers.headers.mkString(", ")
    reqInfo.Body = if (request.hasBody) Some(request.body.toString) else None

    if (jwt.isDefined) {
      reqInfo.UserID = Some(jwt.get.ID)
    }

    val secured = check(token, reqInfo)

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
