package de.htwg.zeta.server.generator.model.diagram.action

import de.htwg.zeta.server.generator.model.diagram.Diagram

class ActionInclude(var globalActionIncludes: List[ActionGroup] = List[ActionGroup](), val openReferences: List[String] = List()) {
  def solveOpenDependencies(diagram: Diagram): Unit = {
    globalActionIncludes = globalActionIncludes ::: openReferences.map(diagram.globalActionGroups(_))
  }
}

object ActionInclude {
  def apply(actionGroupReferences: List[ActionGroup]) = new ActionInclude(actionGroupReferences)
}

