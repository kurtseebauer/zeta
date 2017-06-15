package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 *
 * Starting keyword of the DSL.
 */
object Outputs {

  def ofNodes(nodeType: String): OutputsOfNodes = new OutputsOfNodes(nodeType)

}
