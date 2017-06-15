package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.metaModel.elements.MReference
import models.modelDefinitions.model.elements.Edge
import models.modelDefinitions.model.elements.ToNodes
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgesNoSourcesTest extends FlatSpec with Matchers {

  val mReference = MReference(
    "edgeType",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    Seq.empty,
    Seq.empty,
    Seq[MAttribute]()
  )
  val rule = new EdgesNoSources("edgeType")

  "isValid" should "return true on edges of type edgeType with no sources" in {
    val edge = Edge("", mReference, Seq(), Seq(),Map.empty)
    rule.isValid(edge).get should be(true)
  }

  it should "return false on edges of type edgeType with sources" in {
    val source = MClass(
      name = "",
      abstractness = false,
      superTypeNames = Seq(),
      inputs = Seq(),
      outputs = Seq(),
      attributes = Seq()
    )
    val toNode = ToNodes(clazz = source, nodeNames = Seq(""))
    val edge = Edge("", mReference, Seq(toNode), Seq(), Map.empty)

    rule.isValid(edge).get should be(false)
  }

  it should "return true on edges of type edgeType with empty source list" in {
    val source = MClass(
      name = "",
      abstractness = false,
      superTypeNames = Seq(),
      inputs = Seq(),
      outputs = Seq(),
      attributes = Seq()
    )
    val toNode = ToNodes(clazz = source, nodeNames = Seq())
    val edge = Edge("", mReference, Seq(toNode), Seq(), Map.empty)

    rule.isValid(edge).get should be(true)
  }

  it should "return None on non-matching edges" in {
    val differentReference = MReference(
      "differenteEdgeType",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      Seq.empty,
      Seq.empty,
      Seq[MAttribute]()
    )
    val edge = Edge("", differentReference, Seq(), Seq(), Map.empty)
    rule.isValid(edge) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Edges ofType "edgeType" haveNoSources ()""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val reference = MReference("reference", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
    val metaModel = TestUtil.toMetaModel(Seq(reference))
    val result = EdgesNoSources.generateFor(metaModel)

    result.size should be (1)
    result.head match {
      case rule: EdgesNoSources =>
        rule.edgeType should be ("reference")
      case _ => fail
    }
  }

}
