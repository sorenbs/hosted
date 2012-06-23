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
  object code extends MongoListField[Craft, String](this)
  
}
object Craft extends Craft with MongoMetaRecord[Craft] {
}