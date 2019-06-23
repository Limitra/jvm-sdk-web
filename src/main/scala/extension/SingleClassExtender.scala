package com.limitra.sdk.web.extension

import play.api.libs.json._

/**
  * Extension methods for Class type Single object.
  */
final class SingleClassExtender[A](value: A)(implicit wr: Writes[A]) {
  def ToJson: JsValue = {
    return wr.writes(value)
  }
}
