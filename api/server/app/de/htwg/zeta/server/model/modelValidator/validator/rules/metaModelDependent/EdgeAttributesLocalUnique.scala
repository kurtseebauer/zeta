package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.EnumValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.BoolValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.DoubleValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.IntValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleEdgeRule

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class EdgeAttributesLocalUnique(val edgeType: String, val attributeType: String) extends SingleEdgeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Attribute values of attribute type $attributeType in Edge of type $edgeType must be unique locally."
  override val possibleFix: String = s"Remove all but one of the duplicated attribute values of type $attributeType in Edge of type $edgeType."

  override def isValid(edge: Edge): Option[Boolean] = if (edge.referenceName == edgeType) Some(rule(edge)) else None

  def rule(edge: Edge): Boolean = {

    def handleStrings(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: StringValue => v }.map(_.value)
    def handleBooleans(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: BoolValue => v }.map(_.value.toString)
    def handleInts(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: IntValue => v }.map(_.value.toString)
    def handleDoubles(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: DoubleValue => v }.map(_.value.toString)
    def handleEnums(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: EnumValue => v }.map(_.toString)

    edge.attributeValues.get(attributeType) match {
      case None => true
      case Some(attribute) =>
        val attributeValues: Seq[String] = attribute.headOption match {
          case None => Seq.empty
          case Some(_: StringValue) => handleStrings(attribute)
          case Some(_: BoolValue) => handleBooleans(attribute)
          case Some(_: IntValue) => handleInts(attribute)
          case Some(_: DoubleValue) => handleDoubles(attribute)
          case Some(_: EnumValue) => handleEnums(attribute)
        }

        if (attributeValues.isEmpty) {
          true
        } else {
          attributeValues.distinct.size == attribute.size
        }
    }
  }

  override val dslStatement: String = s"""Attributes ofType "$attributeType" inEdges "$edgeType" areLocalUnique ()"""
}

object EdgeAttributesLocalUnique extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = metaModel.referenceMap.values
    .foldLeft(Seq[DslRule]()) { (acc, currentReference) =>
      acc ++ currentReference.attributes.filter(_.localUnique).map(attr => new EdgeAttributesLocalUnique(currentReference.name, attr.name))
    }
}
