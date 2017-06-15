package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleNodeRule
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class NodesAttributesNamesNotEmpty extends SingleNodeRule {
  override val name: String = getClass.getSimpleName
  override val description: String = "Attribute names of nodes attributes must not be empty."
  override val possibleFix: String = "Add name to every attribute."

  override def isValid(node: Node): Option[Boolean] = Some(!node.attributes.keys.toSeq.contains(""))
}
