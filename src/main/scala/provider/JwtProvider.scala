package com.limitra.sdk.web.provider

import com.limitra.sdk.core._
import com.limitra.sdk.web.definition.{JsonWebToken => JWT}
import pdi.jwt.{JwtAlgorithm, JwtJson}
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
  private val _algorithm = JwtAlgorithm.HS256
  private val _secret = _config.String("Secret")
  private val _prefix = _config.String("Prefix")

  def CreateToken(id: Long, password: String, expire: Long, detail: String = ""): String = {
    val claim = Json.obj(("id", id), ("expire", expire), ("password", password), ("detail", detail))
    this._prefix + " " + JwtJson.encode(claim, _secret, _algorithm)
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
    val jwtT = JWT()
    val claims = JwtJson.decodeJson(jwt, _secret, Seq(_algorithm)).toOption
    if (claims.isDefined) {
      val claimsVal = claims.get
      jwtT.ID = (claimsVal \ "id").as[Long]
      jwtT.Expire = (claimsVal \ "expire").as[Long]
      jwtT.Password = (claimsVal \ "password").as[String]
      jwtT.Detail = (claimsVal \ "detail").as[String]
      jwtT.IsValid = jwtT.Expire >= DateTime.now.getMillis
    }
    jwtT
  }
}
