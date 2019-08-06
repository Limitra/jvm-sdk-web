package com.limitra.sdk.web.provider

import com.limitra.sdk.core.{Config => conf}
import java.io.{File, PrintWriter}
import com.limitra.sdk.web.RouteItem

/**
 * It is used to make automatize route config.
 */
sealed class RouteProvider {
  private val _app = conf("Application")
  private val _path = _app.OptionString("Root")

  def Config(routes: Seq[RouteItem]) {
    if (_path.isDefined) {
      val pw = new PrintWriter(new File(_path.get + "/conf/routes"))
      routes.foreach(route => {
        val row = route.Type_Name + " " + route.Route + " " + route.EndPoint
        pw.println(row)
      })
      pw.close
    }
  }
}
