package com.limitra.sdk

import com.limitra.sdk.core.extension.{BigDecimalExtender, DateTimeExtender, LongExtender}
import com.limitra.sdk.core.{definition => df}
import com.limitra.sdk.web.extension._
import com.limitra.sdk.web.provider._
import org.joda.time.DateTime
import play.api.data.Form
import play.api.libs.json._
import play.api.mvc.Request

package object web {
  implicit def RequestExt[A](value: Request[A]) = new RequestExtender[A](value)

  implicit def FormExt[A](value: Form[A])(implicit request: Request[_]) = new FormExtender[A](value)(request)

  implicit def SingleClassExt[A](value: A)(implicit wr: Writes[A]) = new SingleClassExtender[A](value)

  def DateTime(implicit request: Request[_]) = new df.DateTime(request.TimeZone)

  def Response(implicit request: Request[_]): ResponseProvider = new ResponseProvider(request.Language)
  def Dictionary(implicit request: Request[_]): DictionaryProvider = new DictionaryProvider(request.Language)
  def Route: RouteProvider = new RouteProvider()
  def Jwt: JwtProvider = new JwtProvider()
  def File: FileProvider = new FileProvider()

  implicit def LongExt(value: Long)(implicit request: Request[_]) = new LongExtender(request.TimeZone, value)

  implicit def BigDecimalExt(value: BigDecimal)(implicit request: Request[_]) = new BigDecimalExtender(request.Language, value)

  implicit def DateTimeExt(value: DateTime)(implicit request: Request[_]) = new DateTimeExtender(request.Language, value)
}
