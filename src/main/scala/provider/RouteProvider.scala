package com.limitra.sdk.web.provider

import java.io.{File, PrintWriter}

import com.limitra.sdk.core.{Config => conf}
import com.limitra.sdk.web.definition.RouteItem

/**
 * It is used to make automatize route config.
 */
sealed class RouteProvider {
  private val _app = conf("Application")
  private val _path = _app.OptionString("Root")

  def Config(routes: Seq[RouteItem], file: String = null) {
    if (_path.isDefined) {
      val pw = new PrintWriter(new File(_path.get + "/conf/" + (if (file != null) file else "routes")))
      routes.foreach(route => {
        val row = route.Type_Name + " " + route.Route + " " + route.EndPoint
        pw.println(row)
      })
      pw.close
    }
  }
}
