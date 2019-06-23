package com.limitra.sdk

import com.limitra.sdk.web.extension._
import play.api.data.Form
import play.api.libs.json._
import play.api.mvc.Request

package object web {
  implicit def RequestExt[A](value: Request[A]) = new RequestExtender[A](value)

  implicit def FormExt[A](value: Form[A]) = new FormExtender[A](value)

  implicit def SingleClassExt[A](value: A)(implicit wr: Writes[A]) = new SingleClassExtender[A](value)
}
