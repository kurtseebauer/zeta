package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.{DslRule, GeneratorRule, SingleEdgeRule}
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.model.elements.Edge

class D05_EdgeSourceNodes(edgeType: String, sourceTypes: Seq[String]) extends SingleEdgeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Edges of type $edgeType are only allowed to have source nodes of types ${sourceTypes.mkString("{", ", ", "}")}."
  override val possibleFix: String = s"Remove all source nodes that are not of types ${sourceTypes.mkString("{", ", ", "}")} from edges of type $edgeType."

  override def isValid(edge: Edge): Option[Boolean] = if (edge.`type`.name == edgeType) Some(rule(edge)) else None

  def rule(edge: Edge): Boolean = edge.source.map(_.`type`.name).foldLeft(true) { (acc, sourceName) =>
    if (sourceTypes.contains(sourceName)) acc else false
  }

  override val dslStatement: String = s"""Sources ofEdges "$edgeType" areOfTypes ${Util.stringSeqToSeqString(sourceTypes)}"""
}

object D05_EdgeSourceNodes extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Util.getReferences(metaModel)
    .foldLeft(Seq[DslRule]()) { (acc, currentReference) =>
      if (currentReference.source.isEmpty) acc
      else acc :+ new D05_EdgeSourceNodes(currentReference.name, currentReference.source.map(_.mType.name))
    }
}