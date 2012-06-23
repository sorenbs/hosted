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

object CraftRest extends RestHelper {
  case class CraftParams(id: Int, version: Int)
  case class CraftResponse(id:Int, version:Int, code:String)
  case class ErrorResponse(error:String)
  case class CodeSavedResponse(id:Int, version:Int)
  
  serve("api" / "craft" prefix {
    
    case id :: version :: Nil JsonGet _ => (Craft.where(_.shortId eqs id.toInt)).get match {
      case Some(craft) =>
        if(version.toInt < craft.code.value.length)
          Extraction.decompose(CraftResponse(craft.shortId.value, version.toInt, craft.code.get(version.toInt)))
        else
          Extraction.decompose(ErrorResponse("That version does not exist"))
      case _ => Extraction.decompose(ErrorResponse("No code found!!!"))
    }
    
    case "save" :: id :: Nil Post json =>
      Craft.where(_.shortId eqs id.toInt).modify(_.code push(json.param("code").open_!)).updateMulti
      val newVersion = Craft.where(_.shortId eqs id.toInt).get().get.code.value.length - 1
      Extraction.decompose(CodeSavedResponse(id.toInt, newVersion))
      
     // Extraction.decompose(ErrorResponse("Not Valid blaHHH " + json.param("code").openOr("No Code")))
  })

}