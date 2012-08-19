package code.lib

import code.model._
import net.liftweb._
import common._
import http._
import rest._
import util._
import Helpers._
import json._
import scala.xml._
import com.foursquare.rogue.Rogue._
import org.joda.time.DateTime
import java.util.Date
import java.util.Calendar
import java.io.File
import java.io.FileOutputStream
import sun.security.util.Length
import java.io.FileInputStream

object CraftRest extends RestHelper {
  case class CraftParams(id: String, version: Int)
  case class HistoryItem(version:Int, comment:String, created:Date)
  case class ImageItem(name:String, path:String)
  case class ImageSavedResponse(name: String, path: String)
  case class CraftResponse(id:String, version:Int, code:String, history: List[HistoryItem], images: List[ImageItem], readOnly:Boolean)
  case class ErrorResponse(error:String)
  case class CodeSavedResponse(id:String, version:Int, newVersion:HistoryItem)
  case class ImagesResponse(images: List[ImageData])
  case class TemplateItem(name:String, id:String, version:Int)
  case class TemplatesResponse(templates:List[TemplateItem])
  
  ///
  /// Get: /api/templates
  ///
  serve("api" / "templates" prefix {
    case Nil JsonGet _ => Extraction.decompose(TemplatesResponse(List(
        TemplateItem("Pong", "CK3N5TUGKRAKTJ5Y", 0), 
        TemplateItem("BananaBomber", "SEYRMMRKS1DCOUYX", 0)
        )))
    case _ => Extraction.decompose(ErrorResponse("Not Valid blaHHH"))
  })
  
  serve("api" / "craft" prefix {
    
    //
    // Get: /ase8fsd789gf789gxs9dg/code/7
    //
    case id :: "code" :: version :: Nil JsonGet _ => (Craft.where(_.secretId eqs id)).get match {
      case Some(craft) =>
        println(craft.code.value)
        if(version.toInt < craft.code.value.length)
          Extraction.decompose(CraftResponse(
              craft.secretId.value,
              version.toInt,
              craft.code.get(version.toInt).code.value,
              craft.code.value.zipWithIndex.reverse.map(t => 
                HistoryItem(
                    t._2, 
                    t._1.comment.value, 
                    t._1.created.value.getTime())),
              craft.images.value.map(i => ImageItem(i.name.value, i.url.value)),
              craft.readOnly.value)
          )
        else
          Extraction.decompose(ErrorResponse("That version does not exist"))
      case _ => Extraction.decompose(ErrorResponse("No code found!!!"))
    }
    
    //
    // Post: /ase8fsd789gf789gxs9dg/code/7
    //
    // version is the version this revision is based on. Used for revision graph
    case id :: "code" :: version :: Nil Post json => {
      if(Craft.where(_.secretId eqs id).count == 0) {
        if(id == "0") {
          //Asume the client want's us to create a new Craft
          val secretId = StringHelpers.randomString(16);
          val created = new DateTime().toGregorianCalendar()
          code.model.Craft.createRecord
          	.secretId(secretId)
          	.code(CraftCode.createRecord
          	    .code(json.param("code").open_!)
          	    .comment(json.param("comment").openOr(""))
          	    .created(created) :: Nil)
          	.save
          	Extraction.decompose(CodeSavedResponse(secretId, 0, HistoryItem(0, json.param("comment").openOr(""), created.getTime)))
        }else {
        	Extraction.decompose(ErrorResponse("Wrong ID"))
        }
      } else {
        if(Craft.where(_.secretId eqs id).and(_.readOnly eqs true).count > 0) {
          Extraction.decompose(ErrorResponse("This is ReadOnly!"))
        } else {
        
	      Craft.where(_.secretId eqs id).modify(_.code push(CraftCode.createRecord
	          .code(json.param("code").open_!)
	          .comment(json.param("comment").openOr(""))
	          .created(new DateTime().toGregorianCalendar())
	      )).updateMulti
	      
	      val newVersion = Craft.where(_.secretId eqs id).get().get.code.value.length - 1
	      
	      Extraction.decompose(CodeSavedResponse(id, newVersion, HistoryItem(newVersion, json.param("comment").openOr(""), new DateTime().toGregorianCalendar().getTime)))

        }
      }
    }
    
    //
    // Post: /ase8fsd789gf789gxs9dg/fork/7
    //
    case id :: "fork" :: version :: Nil Post json => (Craft.where(_.secretId eqs id)).get match {
      case Some(craft) => {
        val secretId = StringHelpers.randomString(16);
        val created = new DateTime().toGregorianCalendar()
        val codeString = craft.code.get(version.toInt).code.value
        
        code.model.Craft.createRecord
      	.secretId(secretId)
      	.images(craft.images.value.map(i => ImageData.createRecord.name(i.name.value).url("/images/" + secretId + "/" + i.name.value) ))
      	.code(CraftCode.createRecord
      	    .code(codeString)
      	    .comment("Forked from " + id + ":" + version)
      	    .created(created) :: Nil)
      	.save
      	
      	copyImages(id, secretId)
      	
      	Extraction.decompose(CraftResponse(
      	    secretId, 
      	    0, 
      	    codeString, 
      	    List(HistoryItem(0, json.param("comment").openOr(""), created.getTime)), 
      	    craft.images.value.map(i => ImageItem(i.name.value, i.url.value)),
            false))
      }
      case _ => {
        Extraction.decompose(ErrorResponse("No code found!!! Clone failed"))
      }
    }
    
    //
    // Post: /ase8fsd789gf789gxs9dg/publish/7
    //
    case id :: "publish" :: version :: Nil Post json => (Craft.where(_.secretId eqs id)).get match {
      case Some(craft) => {
        val secretId = StringHelpers.randomString(16);
        val created = new DateTime().toGregorianCalendar()
        val codeString = craft.code.get(version.toInt).code.value
        
        code.model.Craft.createRecord
      	.secretId(secretId)
      	.images(craft.images.value.map(i => ImageData.createRecord.name(i.name.value).url("/images/" + secretId + "/" + i.name.value) ))
      	.code(CraftCode.createRecord
      	    .code(codeString)
      	    .comment("Forked from " + id + ":" + version)
      	    .created(created) :: Nil)
      	.publishedFromId(id)
      	.publishedFromVersion(version.toInt)
      	.readOnly(true)
      	.save
      	
      	copyImages(id, secretId)
      	
      	Extraction.decompose(CraftResponse(
      	    secretId, 
      	    0, 
      	    codeString, 
      	    List(HistoryItem(0, json.param("comment").openOr(""), created.getTime)), 
      	    craft.images.value.map(i => ImageItem(i.name.value, i.url.value)),
              craft.readOnly.value))
      }
      case _ => {
        Extraction.decompose(ErrorResponse("No code found!!! Clone failed"))
      }
    }
    
    //
    // Post: /ase8fsd789gf789gxs9dg/image/7
    //
    case id :: "image" :: version :: Nil Post json => {
      val file = json.uploadedFiles.head
      val escapedId = id.replace('.', '-').replace('/', '-')
      val basePath = "src/main/webapp/images/"
      val path = basePath + escapedId
      val clientPath = "/images/" + id.replace('.', '-').replace('/', '-') + "/" + file.name 
      new File(basePath).mkdir
      new File(path).mkdir
      val oFile = new File(path,  file.name)
      val output = new FileOutputStream(oFile)
      output.write(file.file)
      output.close()
      
      Craft.where(_.secretId eqs id).modify(_.images.push(ImageData.createRecord
          .url(clientPath)
          .name(file.name)
          )).updateMulti
          
         
          
          Extraction.decompose(ImageSavedResponse(file.name, clientPath))
      
    }
    
    //
    // Get: /whatever
    // Post: /crazy request
    case _ =>  
     Extraction.decompose(ErrorResponse("Not Valid blaHHH"))
  })
  
  def copyImages(from:String, to:String) {
    val srcFolder = new File("src/main/webapp/images/" + from)
    val dstFolder = new File("src/main/webapp/images/" + to)
    
    if(srcFolder.exists) {
      if(!dstFolder.exists) {
        dstFolder.mkdir
      }
      srcFolder.list.foreach(f => {
        val input = new FileInputStream(new File(srcFolder, f))
	    val output = new FileOutputStream(new File(dstFolder, f))
	    val buffer = new Array[Byte](1024);
 
        output.write(Stream.continually(input.read).takeWhile(-1 !=).map(_.toByte).toArray)
      
        input.close();
        output.close();
      })
    }
  }

}