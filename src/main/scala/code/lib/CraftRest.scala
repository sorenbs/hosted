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
  case class CodeSavedResponse(id:String, version:Int)
  
  serve("api" / "craft" prefix {
    
    case id :: version :: Nil JsonGet _ => (Craft.where(_.secretId eqs id)).get match {
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
    
    case "save" :: id :: Nil Post json =>
      Craft.where(_.secretId eqs id).modify(_.code push(CraftCode.createRecord
          .code(json.param("code").open_!)
          .comment(json.param("comment").openOr(""))
          .created(new DateTime().toGregorianCalendar())
      )).updateMulti
      
      val newVersion = Craft.where(_.secretId eqs id).get().get.code.value.length - 1
      
      Extraction.decompose(CodeSavedResponse(id, newVersion))
    
    case _ =>  
     Extraction.decompose(ErrorResponse("Not Valid blaHHH"))
  })

}