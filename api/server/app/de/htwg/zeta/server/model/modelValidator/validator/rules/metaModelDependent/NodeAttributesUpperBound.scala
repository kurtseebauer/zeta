package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleNodeRule
import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class NodeAttributesUpperBound(val nodeType: String, val attributeType: String, val upperBound: Int) extends SingleNodeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String =
    s"Nodes of type $nodeType must have a maximum of $upperBound attributes of type $attributeType."
  override val possibleFix: String =
    s"Remove attributes of type $attributeType from nodes of type $nodeType until there are a maximum of $upperBound attributes."

  override def isValid(node: Node): Option[Boolean] = if (node.clazz.name == nodeType) Some(rule(node)) else None

  def rule(node: Node): Boolean = if (upperBound == -1) true else node.attributes.get(attributeType) match {
    case Some(attribute) => attribute.size <= upperBound
    case None => true
  }

  override val dslStatement: String = s"""Attributes ofType "$attributeType" inNodes "$nodeType" haveUpperBound $upperBound"""
}

object NodeAttributesUpperBound extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Util.inheritAttributes(Util.simplifyMetaModelGraph(metaModel))
    .filterNot(_.abstractness)
    .foldLeft(Seq[DslRule]()) { (acc, currentClass) =>
      acc ++ currentClass.attributes.map(attr => new NodeAttributesUpperBound(currentClass.name, attr.name, attr.upperBound))
    }
}
