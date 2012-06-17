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
  
  serve("api" / "craft" prefix {
    
    case id :: Nil JsonGet _ => Craft where(_.shortId eqs id.toInt) get match {
      case Some(craft) => craft:JValue
      case _ =>json.JsonDSL.string2jvalue("No Code Found!")
    }

  })

}