package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.ModelValidationResult
import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.ElementsRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.metaModel.elements.AttributeValue
import models.modelDefinitions.metaModel.elements.EnumSymbol
import models.modelDefinitions.metaModel.elements.ScalarValue.MBool
import models.modelDefinitions.metaModel.elements.ScalarValue.MDouble
import models.modelDefinitions.metaModel.elements.ScalarValue.MInt
import models.modelDefinitions.metaModel.elements.ScalarValue.MString
import models.modelDefinitions.model.elements.Edge
import models.modelDefinitions.model.elements.ModelElement

class EdgeAttributesGlobalUnique(edgeType: String, attributeType: String) extends ElementsRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Every value of attribute $attributeType in edges of type $edgeType must be globally unique."
  override val possibleFix: String = s"Remove duplicated values of attribute $attributeType in edges of type $edgeType."

  override def check(elements: Seq[ModelElement]): Seq[ModelValidationResult] = {

    def handleStrings(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: MString => v }.map(_.value)
    def handleBooleans(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: MBool => v }.map(_.value.toString)
    def handleInts(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: MInt => v }.map(_.value.toString)
    def handleDoubles(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: MDouble => v }.map(_.value.toString)
    def handleEnums(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: EnumSymbol => v }.map(_.toString)


    val edges = Util.getEdges(elements).filter(_.`type`.name == edgeType)
    val attributeValues: Seq[AttributeValue] = edges.flatMap(_.attributes).filter(_.name == attributeType).flatMap(_.value)

    val attributeValuesStrings: Seq[String] = attributeValues.headOption match {
      case None => Seq()
      case Some(_: MString) => handleStrings(attributeValues)
      case Some(_: MBool) => handleBooleans(attributeValues)
      case Some(_: MInt) => handleInts(attributeValues)
      case Some(_: MDouble) => handleDoubles(attributeValues)
      case Some(_: EnumSymbol) => handleEnums(attributeValues)
    }

    // find duplicate values
    val attributesGrouped: Map[String, Seq[String]] = attributeValuesStrings.groupBy(identity)
    val duplicateAttributeValues: Seq[String] = attributesGrouped.filter(_._2.size > 1).keys.toSeq

    def checkEdgeDuplicateValues(acc: Seq[ModelValidationResult], currentEdge: Edge): Seq[ModelValidationResult] = {
      val attributeValues = currentEdge.attributes.flatMap(_.value)

      val attributeValuesStrings: Seq[String] = attributeValues.headOption match {
        case None => Seq()
        case Some(_: MString) => handleStrings(attributeValues)
        case Some(_: MBool) => handleBooleans(attributeValues)
        case Some(_: MInt) => handleInts(attributeValues)
        case Some(_: MDouble) => handleDoubles(attributeValues)
        case Some(_: EnumSymbol) => handleEnums(attributeValues)
      }

      val valid = attributeValuesStrings.foldLeft(true) { (acc, currentString) =>
        if (duplicateAttributeValues.contains(currentString)) false else acc
      }

      acc :+ ModelValidationResult(rule = this, valid = valid, modelElement = Some(currentEdge))
    }

    // check which edges contains one or more of the duplicated values.
    edges.foldLeft(Seq[ModelValidationResult]())(checkEdgeDuplicateValues)

  }

  override val dslStatement: String = s"""Attributes ofType "$attributeType" inEdges "$edgeType" areGlobalUnique ()"""
}

object EdgeAttributesGlobalUnique extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Util.getReferences(metaModel)
    .foldLeft(Seq[DslRule]()) { (acc, currentReference) =>
      acc ++ currentReference.attributes.filter(_.globalUnique).map(attr => new EdgeAttributesGlobalUnique(currentReference.name, attr.name))
    }
}