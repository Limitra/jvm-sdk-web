package com.limitra.sdk.web.composition

import com.limitra.sdk.web._
import com.limitra.sdk.web.definition._
import play.api.mvc._

import scala.concurrent._

sealed class DeviceInfo(parser: BodyParser[AnyContent], info: (definition.DeviceInfo) => Unit)
                   (implicit ec: ExecutionContext) extends ActionBuilderImpl(parser) {
  override def invokeBlock[A](request: Request[A],
                              block: (Request[A]) => Future[Result]) = {
    val jwt = request.ToJwt

    val device = new definition.DeviceInfo
    device.UserAgent = request.headers.get("UserAgent")
    device.Browser = request.headers.get("Browser")
    device.BrowserVersion = request.headers.get("BrowserVersion")
    device.Device = request.headers.get("Device")
    device.DeviceType = request.headers.get("DeviceType")
    device.OS = request.headers.get("OS")
    device.OSVersion = request.headers.get("OSVersion")

    if (jwt.isDefined) {
      device.UserID = Some(jwt.get.ID)
    }

    info(device)

    block(request)
  }
}
