package com.limitra.sdk.web

import play.api.data.Form
import play.api.data.Forms._

case class LoginForm(UserName: String, Password: String, KeepSession: Boolean)

object LoginForm {
  val get = Form(
    mapping(
      "UserName" -> nonEmptyText,
      "Password" -> nonEmptyText,
      "KeepSession" -> boolean
    )(LoginForm.apply)(LoginForm.unapply))
}
