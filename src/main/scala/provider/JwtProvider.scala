package com.limitra.sdk.web.provider

import authentikat.jwt._
import com.limitra.sdk.core._
import com.limitra.sdk.web.definition.{JsonWebToken => JWT}
import play.api.libs.json.Json

case class ChatHubBodyDTO(var SeenDate: Option[Long] = None,
                          var SenderName: Option[String] = None,
                          var SenderAbout: Option[String] = None,
                          var SenderEmail: Option[String] = None,
                          var DeviceIdentity: Option[String] = None,
                          var TransmitterID: Option[Long] = None,
                          var SessionID: Option[Long] = None,
                          var Message: Option[String] = None,
                          var Token: Option[String] = None,
                          var RegisterDate: Option[Long] = None,
                          var IsSelf: Option[Boolean] = None,
                          var Type: String = "")

/**
 * JWT token provider for web authentications.
 */
sealed class JwtProvider {
  private val _config = Config("Security")
  private val _header = JwtHeader("HS256")
  private val _secret = _config.String("Secret")
  private val _prefix = _config.String("Prefix")

  def CreateToken(id: Long, password: String, expire: Long, detail: String = ""): String = {
    var payLoad = JwtClaimsSet(Map("id" -> id, "expire" -> expire, "roles" -> Seq(password, detail)))
    this._prefix + " " + JsonWebToken(_header, payLoad, _secret)
  }

  def ReadToken(jwt: String): Option[JWT] = {
    val token = this._extractToken(jwt)
    if (token.isDefined) {
      return Some(this._getJwtClaims(token.get))
    } else {
      return None
    }
  }

  private def _extractToken(authHeader: String): Option[String] = {
    authHeader.split(this._prefix + " ") match {
      case Array(_, token) => Some(token)
      case _ => None
    }
  }

  private def _getJwtClaims(jwt: String): JWT = {
    var jwtT = JWT()
    val claims = this._claims(jwt)
    if (claims.isDefined) {
      val claimsVal = Json.parse(claims.get)
      jwtT.ID = (claimsVal \ "id").as[Long]
      jwtT.Expire = (claimsVal \ "expire").as[Long]
      val roles = (claimsVal \ "roles").as[Seq[String]]
      jwtT.Password = roles.head
      jwtT.Detail = roles.last
      jwtT.IsValid = this._validateToken(jwt) && jwtT.Expire >= DateTime.now.getMillis
    }
    jwtT
  }

  private def _validateToken(jwt: String): Boolean = {
    JsonWebToken.validate(jwt, this._secret)
  }

  private def _claims(jwt: String): Option[String] =
    jwt match {
      case JsonWebToken(header, claimsSet, signature) => Option(claimsSet.asJsonString)
      case _ => None
    }

  private def _getJwtBody(jwt: String): JWT = {
    var jwtT = JWT()
    val token = this._extractToken(jwt)
    if (token.isDefined) {
      jwtT = this._getJwtClaims(token.get)
    }
    jwtT
  }
}
