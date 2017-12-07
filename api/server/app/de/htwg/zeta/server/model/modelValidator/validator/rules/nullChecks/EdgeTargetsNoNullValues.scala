package de.htwg.zeta.server.model.modelValidator.validator.rules.nullChecks

import de.htwg.zeta.common.models.modelDefinitions.model.GraphicalDslInstance
import de.htwg.zeta.server.model.modelValidator.validator.rules.ModelRule

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class EdgeTargetsNoNullValues extends ModelRule {
  override val name: String = getClass.getSimpleName
  override val description: String = "The target list inside an edge contains Null values."
  override val possibleFix: String = "Remove the Null values."

  override def check(model: GraphicalDslInstance): Boolean = !model.edges.flatMap(_.targetNodeName).contains(null) // scalastyle:ignore null
}
