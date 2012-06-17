package code
package model

import net.liftweb._
import mongodb.record.{MongoMetaRecord, MongoId, MongoRecord}
import record.field.{StringField, EnumNameField, LongField, IntField}
import mongodb.record.field.{MongoListField, BsonRecordField, BsonRecordListField,MongoMapField}
import mongodb.record.BsonRecord
import mongodb.record.BsonMetaRecord
import json.Extraction
import json.JsonAST.JValue

class Craft extends MongoRecord[Craft] with MongoId[Craft] {
  def meta = Craft
  
  object shortId extends IntField(this)
  object version extends IntField(this)
  object code extends MongoListField[Craft, String](this)
  
}
object Craft extends Craft with MongoMetaRecord[Craft] {
  case class PublicCraft(Id: Int, Code: String)
  /**
   * Convert the item to JSON format.  This is
   * implicit and in the companion object, so
   * an Item can be returned easily from a JSON call
   */
  implicit def toJson(craft: Craft): JValue =
    Extraction.decompose(PublicCraft(craft.shortId.value, craft.code.value.last))
}