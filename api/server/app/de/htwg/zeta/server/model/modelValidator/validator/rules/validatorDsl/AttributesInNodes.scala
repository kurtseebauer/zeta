package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodeAttributes

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class AttributesInNodes(nodeType: String) {

  def areOfTypes(attributeType: Seq[String]): NodeAttributes = new NodeAttributes(nodeType, attributeType)

}
