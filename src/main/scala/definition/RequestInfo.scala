package com.limitra.sdk.web.definition

case class RequestInfo(
                        var Millis: Long = 0,
                        var Path: String = "",
                        var Host: String = "",
                        var RemoteAddress: String = "",
                        var Header: String = "",
                        var Body: Option[String] = None,
                        var UserID: Option[Long] = None,
                        var UserAgent: Option[String] = None,
                        var OS: Option[String] = None,
                        var OSVersion: Option[String] = None,
                        var Browser: Option[String] = None,
                        var BrowserVersion: Option[String] = None,
                        var Device: Option[String] = None,
                        var DeviceType: Option[String] = None)

