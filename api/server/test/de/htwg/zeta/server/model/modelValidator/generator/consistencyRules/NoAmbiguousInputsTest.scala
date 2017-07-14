package de.htwg.zeta.server.model.modelValidator.generator.consistencyRules

import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReferenceLinkDef
import org.scalatest.FlatSpec
import org.scalatest.Matchers

import scala.collection.immutable.Seq

class NoAmbiguousInputsTest extends FlatSpec with Matchers {

  val input = MReferenceLinkDef(
    referenceName = "input",
    upperBound = -1,
    lowerBound = 0,
    deleteIfLower = false
  )

  val superClass = MClass(
    name = "superClass",
    abstractness = false,
    superTypeNames = Seq(),
    inputs = Seq(input),
    outputs = Seq(),
    attributes = Seq()
  )

  val nonAmbiguousInput = MReferenceLinkDef(
    referenceName = "input",
    upperBound = -1,
    lowerBound = 0,
    deleteIfLower = false
  )

  val ambiguousInput = MReferenceLinkDef(
    referenceName = "input",
    upperBound = 5, // different upperBound
    lowerBound = 0,
    deleteIfLower = false
  )

  val nonAmbiguousSuperClass = MClass(
    name = "nonAmbiguousSuperClass",
    abstractness = false,
    superTypeNames = Seq(),
    inputs = Seq(nonAmbiguousInput),
    outputs = Seq(),
    attributes = Seq()
  )

  val ambiguousSuperClass = MClass(
    name = "ambiguousSubClass",
    abstractness = false,
    superTypeNames = Seq(),
    inputs = Seq(ambiguousInput),
    outputs = Seq(),
    attributes = Seq()
  )

  val nonAmbiguousSubClass = MClass(
    name = "nonAmbiguousSubClass",
    abstractness = false,
    superTypeNames = Seq(superClass.name, nonAmbiguousSuperClass.name),
    inputs = Seq(),
    outputs = Seq(),
    attributes = Seq()
  )

  val ambiguousSubClass = MClass(
    name = "ambiguousSubClass",
    abstractness = false,
    superTypeNames = Seq(superClass.name, ambiguousSuperClass.name),
    inputs = Seq(),
    outputs = Seq(),
    attributes = Seq()
  )

  val nonAmbiguousMetaModel = MetaModel(
    name = "nonAmbiguousMetaModel",
    classes = Seq(superClass, nonAmbiguousSuperClass, nonAmbiguousSubClass),
    references = Seq(),
    enums = Seq.empty,
    uiState = ""
  )

  val ambiguousMetaModel = MetaModel(
    name = "ambiguousMetaMOdel",
    classes = Seq(superClass, ambiguousSuperClass, ambiguousSubClass),
    references = Seq(),
    enums = Seq.empty,
    uiState = ""
  )

  val rule = new NoAmbiguousInputs

  "check" should "return true on a meta model with correct input inheritance" in {
    rule.check(nonAmbiguousMetaModel) should be (true)
  }

  it should "return false on a meta model with incorrect input inheritance" in {
    rule.check(ambiguousMetaModel) should be (false)
  }

}