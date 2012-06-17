package code
package model

import net.liftweb.mongodb.record.{MongoMetaRecord, MongoId, MongoRecord}
import net.liftweb.record.field.{StringField, EnumNameField, LongField, IntField}
import net.liftweb.mongodb.record.field.{MongoListField, BsonRecordField, BsonRecordListField,MongoMapField}
import net.liftweb.mongodb.record.BsonRecord
import net.liftweb.mongodb.record.BsonMetaRecord


class Counter extends MongoRecord[Counter] with MongoId[Counter] {
  def meta = Counter
  
  object name extends StringField(this, 500)
  object counter extends IntField(this)
}
object Counter extends Counter with MongoMetaRecord[Counter] {
}