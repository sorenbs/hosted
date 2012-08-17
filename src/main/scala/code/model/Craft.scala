package code
package model

import net.liftweb._
import mongodb.record.{MongoMetaRecord, MongoId, MongoRecord}
import record.field.{StringField, EnumNameField, LongField, IntField, DateTimeField, BooleanField}
import mongodb.record.field.{MongoListField, BsonRecordField, BsonRecordListField,MongoMapField}
import mongodb.record.BsonRecord
import mongodb.record.BsonMetaRecord
import json.Extraction
import json.JsonAST.JValue
import net.liftweb.mongodb.record.BsonMetaRecord

class Craft extends MongoRecord[Craft] with MongoId[Craft] {
  def meta = Craft
  
  object shortId extends IntField(this)
  object secretId extends StringField(this,16)
  object code extends BsonRecordListField(this,CraftCode)
  object images extends BsonRecordListField(this,ImageData)
  object publishedFromId extends StringField(this, 16)
  object publishedFromVersion extends IntField(this)
  object readOnly extends BooleanField(this)
  
}
object Craft extends Craft with MongoMetaRecord[Craft]

class ImageData private () extends BsonRecord[ImageData] {
  def meta = ImageData
  
  object url extends StringField(this, 1024)
  object name extends StringField(this, 1024)
}
object ImageData extends ImageData with BsonMetaRecord[ImageData]

class CraftCode private () extends BsonRecord[CraftCode] {
  def meta = CraftCode

  object code extends StringField(this, Int.MaxValue)
  object created extends DateTimeField(this)
  object comment extends StringField(this, 512)
}
object CraftCode extends CraftCode with BsonMetaRecord[CraftCode]