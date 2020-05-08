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

    val device = new definition.RequestInfo
    device.UserAgent = request.headers.get("UserAgent")
    if (!device.UserAgent.isDefined) {
      device.UserAgent = request.headers.get("User-Agent")
    }
    device.Browser = request.headers.get("Browser")
    device.BrowserVersion = request.headers.get("BrowserVersion")
    device.Device = request.headers.get("Device")
    device.DeviceType = request.headers.get("DeviceType")
    device.OS = request.headers.get("OS")
    device.OSVersion = request.headers.get("OSVersion")

    device.RemoteAddress = request.RemoteAddress
    device.Path = request.path
    device.Header = Some(request.headers.toString)
    device.Body = if (request.hasBody) Some(request.body.toString) else None

    if (jwt.isDefined) {
      device.UserID = Some(jwt.get.ID)
    }

    info(device)

    block(request)
  }
}
