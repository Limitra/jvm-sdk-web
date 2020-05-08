package com.limitra.sdk.web.composition

import com.limitra.sdk.web._
import com.limitra.sdk.web.definition._
import play.api.mvc._

import scala.concurrent._

sealed class RequestInfo(parser: BodyParser[AnyContent], info: (definition.RequestInfo) => Unit)
                   (implicit ec: ExecutionContext) extends ActionBuilderImpl(parser) {
  override def invokeBlock[A](request: Request[A],
                              block: (Request[A]) => Future[Result]) = {
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
    reqInfo.Header = Some(request.headers.toString)
    reqInfo.Body = if (request.hasBody) Some(request.body.toString) else None

    if (jwt.isDefined) {
      reqInfo.UserID = Some(jwt.get.ID)
    }

    info(reqInfo)

    block(request)
  }
}
