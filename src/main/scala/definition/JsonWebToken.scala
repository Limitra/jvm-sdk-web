package com.limitra.sdk.web.definition

case class JsonWebToken(var ID: Long = 0,
                        var Password: String = "",
                        var Expire: Long = 0,
                        var Detail: String = "",
                        var IsValid: Boolean = false)
