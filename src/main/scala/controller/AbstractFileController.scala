package com.limitra.sdk.web.controller

import java.io.File
import java.text.SimpleDateFormat

import com.limitra.sdk.core.Config
import com.limitra.sdk.web.definition.FileUploadResult
import javax.inject._
import org.joda.time.DateTime
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
abstract class AbstractFileController(cc: ControllerComponents, ca: ActionBuilder[Request, AnyContent])(implicit ec: ExecutionContext) extends AbstractController(cc) {
  private val _config = Config("Application").Get("File")
  private val _maxLen = 5242880

  private var _imageTypes = this._config.Get("Image").OptionString("Pattern").getOrElse("image/png,image/jpeg,image/bmp,image/gif").trim.split(',')
  private var _audioTypes = this._config.Get("Audio").OptionString("Pattern").getOrElse("audio/mpeg,audio/ogg,audio/wav,audio/3gpp,audio/mp3,audio/mp4").trim.split(',')
  private var _videoTypes = this._config.Get("Video").OptionString("Pattern").getOrElse("video/mp4,video/webm,video/ogg").trim.split(',')
  private var _documentTypes = this._config.Get("Document").OptionString("Pattern").getOrElse("text/plain,application/pdf").trim.split(',')

  _imageTypes = _imageTypes ++ this._config.Get("Image").OptionString("Include").getOrElse("").trim.split(',')
  _imageTypes = _imageTypes.filter(x => !this._config.Get("Image").OptionString("Exclude").getOrElse("").trim.split(',').contains(x))

  _audioTypes = _audioTypes ++ this._config.Get("Audio").OptionString("Include").getOrElse("").trim.split(',')
  _audioTypes = _audioTypes.filter(x => !this._config.Get("Audio").OptionString("Exclude").getOrElse("").trim.split(',').contains(x))

  _videoTypes = _videoTypes ++ this._config.Get("Video").OptionString("Include").getOrElse("").trim.split(',')
  _videoTypes = _videoTypes.filter(x => !this._config.Get("Video").OptionString("Exclude").getOrElse("").trim.split(',').contains(x))

  _documentTypes = _documentTypes ++ this._config.Get("Document").OptionString("Include").getOrElse("").trim.split(',')
  _documentTypes = _documentTypes.filter(x => !this._config.Get("Document").OptionString("Exclude").getOrElse("").trim.split(',').contains(x))

  def Download(path: String) = Action.async { implicit request =>
    val opType = request.queryString.get("type").filter(x => !x.isEmpty).map(x => x.head).headOption.getOrElse("")
    val source = this._config.OptionString("Path")
    if (source.isDefined) {
      var map = (source.get + "/" + path).replace("//", "/")
      var file = new java.io.File(map)
      if (file.exists()) {
        file.setLastModified(DateTime.now.getMillis)
        opType match {
          case "ping" => Future.successful(Ok(Json.toJson(true)))
          case _ => Future.successful(Ok.sendFile(content = file))
        }
      } else {
        map = (source.get + "/temp/" + path).replace("//", "/")
        file = new java.io.File(map)
        if (file.exists()) {
          file.setLastModified(DateTime.now.getMillis)
          opType match {
            case "ping" => Future.successful(Ok(Json.toJson(true)))
            case _ => Future.successful(Ok.sendFile(content = file))
          }
        } else {
          Future.successful(NotFound("Error: Not Found File"))
        }
      }
    } else {
      Future.successful(InsufficientStorage("Error: Insufficient storage"))
    }
  }

  def Upload = ca(parse.multipartFormData).async { implicit request =>
    val file = request.body.files.headOption
    if (file.isDefined) {
      val path = this._config.OptionString("Path")
      if (path.isDefined) {
        val conType = file.get.contentType
        val tooLarge = Future.successful(EntityTooLarge("Error: Entity too large"))

        if (conType.isDefined && this._imageTypes.contains(conType.get)) {
          if (file.get.fileSize <= _config.Get("Image").OptionInt("MaxLength").getOrElse(_maxLen)) {
            this._image.apply(request)
          } else { tooLarge }
        } else if (conType.isDefined && this._audioTypes.contains(conType.get)) {
          if (file.get.fileSize <= _config.Get("Audio").OptionInt("MaxLength").getOrElse(_maxLen)) {
            this._audio.apply(request)
          } else { tooLarge }
        } else if (conType.isDefined && this._videoTypes.contains(conType.get)) {
          if (file.get.fileSize <= _config.Get("Video").OptionInt("MaxLength").getOrElse(_maxLen)) {
            this._video.apply(request)
          } else { tooLarge }
        } else if (conType.isDefined && this._documentTypes.contains(conType.get)) {
          if (file.get.fileSize <= _config.Get("Document").OptionInt("MaxLength").getOrElse(_maxLen)) {
            this._document.apply(request)
          } else { tooLarge }
        } else { Future.successful(UnsupportedMediaType("Error: Unsupported media type")) }
      } else {
        Future.successful(InsufficientStorage("Error: Insufficient storage"))
      }
    } else {
      Future.successful(BadRequest("Error: Bad Request"))
    }
  }

  private def _uploadFile(fileSelector: String, request: Request[MultipartFormData[play.api.libs.Files.TemporaryFile]]): Future[Result] = {
    val file = request.body.files.head
    val sourcePath = (this._config.String("Path") + "/temp").replace("//", "/")
    val folder = this._config.Get(fileSelector).OptionString("Folder")
    var targetPath = sourcePath
    if (folder.isDefined) {
      targetPath = (targetPath + "/" + folder.get).replace("//", "/")
    }
    val directory = new File(targetPath)
    if (!directory.exists) {
      directory.mkdirs()
    } else if (!directory.isDirectory) {
      directory.delete()
      directory.mkdirs()
    }
    val dateForm = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss.SSS")
    val ext = file.filename.split('.').lastOption.getOrElse("less").toUpperCase()
    val rndVal = "_" + scala.util.Random.nextInt(10000)
    val fileName = dateForm.format(DateTime.now.toDate) + rndVal + "." + ext
    targetPath = (targetPath + "/" + fileName).replace("//", "/")
    file.ref.copyTo(new File(targetPath))

    Future.successful(Ok(Json.toJson(new FileUploadResult {
      Path = ("/" + targetPath.replace(sourcePath, "")).replace("//", "/");
      Name = fileName;
      Type = file.contentType;
      Size = file.fileSize;
    })))
  }

  private def _image = ca(parse.multipartFormData).async { implicit request =>
    this._uploadFile("Image", request)
  }

  private def _audio = ca(parse.multipartFormData).async { implicit request =>
    this._uploadFile("Audio", request)
  }

  private def _video = ca(parse.multipartFormData).async { implicit request =>
    this._uploadFile("Video", request)
  }

  private def _document = ca(parse.multipartFormData).async { implicit request =>
    this._uploadFile("Document", request)
  }
}
