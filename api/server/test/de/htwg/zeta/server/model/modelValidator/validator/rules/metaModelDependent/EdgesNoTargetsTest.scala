package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.metaModel.elements.MLinkDef
import models.modelDefinitions.metaModel.elements.MReference
import models.modelDefinitions.model.elements.Edge
import models.modelDefinitions.model.elements.Node
import models.modelDefinitions.model.elements.ToNodes
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgesNoTargetsTest extends FlatSpec with Matchers {

  val mReference = MReference("edgeType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
  val rule = new EdgesNoTargets("edgeType")

  "check" should "return true on edges of type edgeType with no targets" in {
    val edge = Edge.apply2("", mReference, Seq(), Seq(), Seq())
    rule.isValid(edge).get should be (true)
  }

  it should "return false on edges of type edgeType with targets" in {
    val target = MClass(
      name = "",
      abstractness = false,
      superTypes = Seq(),
      inputs = Seq(),
      outputs = Seq(),
      attributes = Seq()
    )
    val toNode = ToNodes(`type` = target, nodes = Seq(Node(
      id = "",
      `type` = target,
      _outputs = Seq(),
      _inputs = Seq(),
      attributes = Seq()
    )))
    val edge = Edge.apply2("", mReference, Seq(), Seq(toNode), Seq())

    rule.isValid(edge).get should be (false)
  }

  it should "return true on edges of type edgeType with empty target list" in {
    val target = MClass(
      name = "",
      abstractness = false,
      superTypes = Seq(),
      inputs = Seq(),
      outputs = Seq(),
      attributes = Seq()
    )
    val toNode = ToNodes(`type` = target, nodes = Seq())
    val edge = Edge.apply2("", mReference, Seq(), Seq(toNode), Seq())

    rule.isValid(edge).get should be (true)
  }

  it should "return None on non-matching edges" in {
    val differentReference = MReference("differenteEdgeType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
    val edge = Edge.apply2("", differentReference, Seq(), Seq(), Seq())
    rule.isValid(edge) should be (None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be ("""Edges ofType "edgeType" haveNoTargets ()""")
  }

}