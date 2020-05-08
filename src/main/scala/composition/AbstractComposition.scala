package com.limitra.sdk.web.composition

import com.limitra.sdk.web.{definition=>definition}
import com.limitra.sdk.database.mysql.DbSource
import com.limitra.sdk.web.definition.{RequestInfo, JsonWebToken}
import play.api.mvc.{ActionBuilder, ActionBuilderImpl, AnyContent, BodyParser, Request}
import slick.lifted.{Query, Rep}

import scala.concurrent.ExecutionContext

abstract class AbstractComposition(parser: BodyParser[AnyContent])(implicit ec: ExecutionContext) extends ActionBuilderImpl(parser) {
  def Db: DbSource
  def GetInfo(info: definition.RequestInfo): Unit = () => {}
  def IsSecured(token: Option[String]): Boolean = { return true }
  def IsAuthenticated(jwt: JsonWebToken): Rep[Boolean]
  def AuthorizedUrls(jwt: JsonWebToken): Query[_, String, Seq]

  def Secured: ActionBuilder[Request, AnyContent] = {
    return new RequestInfo(parser, this.GetInfo)(ec) andThen new Security(parser, this.IsSecured)(ec)
  }

  def Authenticated: ActionBuilder[Request, AnyContent] = {
    return new RequestInfo(parser, this.GetInfo)(ec) andThen new Authentication(parser, this.Db, this.IsAuthenticated)(ec) andThen new Security(parser, this.IsSecured)(ec)
  }

  def Authorized: ActionBuilder[Request, AnyContent] = {
    return new RequestInfo(parser, this.GetInfo)(ec) andThen new Authentication(parser, this.Db, this.IsAuthenticated)(ec) andThen new Authorization(parser, this.Db, this.AuthorizedUrls)(ec) andThen new Security(parser, this.IsSecured)(ec)
  }
}
