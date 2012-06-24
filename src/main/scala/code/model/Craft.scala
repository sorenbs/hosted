package code
package model

import net.liftweb._
import mongodb.record.{MongoMetaRecord, MongoId, MongoRecord}
import record.field.{StringField, EnumNameField, LongField, IntField, DateTimeField}
import mongodb.record.field.{MongoListField, BsonRecordField, BsonRecordListField,MongoMapField}
import mongodb.record.BsonRecord
import mongodb.record.BsonMetaRecord
import json.Extraction
import json.JsonAST.JValue

class Craft extends MongoRecord[Craft] with MongoId[Craft] {
  def meta = Craft
  
  object shortId extends IntField(this)
  object secretId extends StringField(this,16)
  object code extends BsonRecordListField(this,CraftCode)
  
}
object Craft extends Craft with MongoMetaRecord[Craft] {
}



class CraftCode private () extends BsonRecord[CraftCode] {
  def meta = CraftCode

  object code extends StringField(this, Int.MaxValue)
  object created extends DateTimeField(this)
  object comment extends StringField(this, 512)
}
object CraftCode extends CraftCode with BsonMetaRecord[CraftCode]