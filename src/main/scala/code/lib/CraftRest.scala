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

object CraftRest extends RestHelper {
  case class CraftParams(id: String, version: Int)
  case class HistoryItem(version:Int, comment:String, created:Date)
  case class CraftResponse(id:String, version:Int, code:String, history: List[HistoryItem])
  case class ErrorResponse(error:String)
  case class CodeSavedResponse(id:String, version:Int, newVersion:HistoryItem)
  case class ImagesResponse(urls: List[ImageData])
  case class TemplateItem(name: String, code: String)
  case class TemplatesResponse(templates: List[TemplateItem])
  
  serve("api" / "templates" prefix {
    case Nil JsonGet _ => Extraction.decompose(TemplatesResponse(List(
        TemplateItem("Pong",GameTemplates.Pong), 
        TemplateItem("BananaBomber", GameTemplates.bnanaBomber)
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
                    t._1.created.value.getTime())))
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
          //Asume the client want's us to create new Craft
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
	      Craft.where(_.secretId eqs id).modify(_.code push(CraftCode.createRecord
	          .code(json.param("code").open_!)
	          .comment(json.param("comment").openOr(""))
	          .created(new DateTime().toGregorianCalendar())
	      )).updateMulti
	      
	      val newVersion = Craft.where(_.secretId eqs id).get().get.code.value.length - 1
	      
	      Extraction.decompose(CodeSavedResponse(id, newVersion, HistoryItem(newVersion, json.param("comment").openOr(""), new DateTime().toGregorianCalendar().getTime)))
      }
    }
    
    //
    // Get: /ase8fsd789gf789gxs9dg/images
    //
    case id :: "images" :: Nil JsonGet _ => (Craft.where(_.secretId eqs id)).get match {
      case Some(craft) =>
        Extraction.decompose(ImagesResponse(craft.images.value))
      case _ => Extraction.decompose(ErrorResponse("No code found!!!"))
    }
    
    //
    // Get: /whatever
    // Post: /crazy request
    case _ =>  
     Extraction.decompose(ErrorResponse("Not Valid blaHHH"))
  })

}