package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgeAttributesUpperBoundTest extends FlatSpec with Matchers {

  val mReference = MReference(
    "edgeType",
    "",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    Seq.empty,
    Seq.empty,
    Seq[MAttribute](),
    Seq.empty
  )

  val emptyEdge: Edge = Edge.empty("", mReference.name, Seq.empty, Seq.empty)

  val rule = new EdgeAttributesUpperBound("edgeType", "attributeType", 2)

  "check" should "return true on edges with 2 or less attributes of type attributeType" in {

    val noAttributes: Map[String, Seq[AttributeValue]] = Map("attributeType" -> Seq.empty)
    val noAttributesEdge = emptyEdge.copy(attributeValues = noAttributes)

    rule.isValid(noAttributesEdge).get should be(true)

    val oneAttribute: Map[String, Seq[AttributeValue]] = Map("attributeType" -> Seq(StringValue("att")))
    val oneAttributeEdge = emptyEdge.copy(attributeValues = oneAttribute)

    rule.isValid(oneAttributeEdge).get should be(true)

    val twoAttributes: Map[String, Seq[AttributeValue]] = Map("attributeType" -> Seq(StringValue("att1"), StringValue("att2")))
    val twoAttributesEdge = emptyEdge.copy(attributeValues = twoAttributes)

    rule.isValid(twoAttributesEdge).get should be(true)

  }

  it should "return false on edges with more than 2 attributes of type attributeType" in {
    val threeAttributes: Map[String, Seq[AttributeValue]] = Map("attributeType" -> Seq(StringValue("att1"), StringValue("att2"), StringValue("att3")))
    val threeAttributesEdge = emptyEdge.copy(attributeValues = threeAttributes)

    rule.isValid(threeAttributesEdge).get should be(false)

    val fourAttributes: Map[String, Seq[AttributeValue]] = Map("attributeType" -> Seq(StringValue("att1"), StringValue("att2"), StringValue("att3"), StringValue("att4")))
    val fourAttributesEdge = emptyEdge.copy(attributeValues = fourAttributes)

    rule.isValid(fourAttributesEdge).get should be(false)
  }

  it should "return None on non-matching edges" in {
    val differentReference = MReference(
      "differentEdgeType",
      "",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      Seq.empty,
      Seq.empty,
      Seq[MAttribute](),
      Seq.empty
    )
    val edge = emptyEdge.copy(referenceName = differentReference.name)

    rule.isValid(edge) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Attributes ofType "attributeType" inEdges "edgeType" haveUpperBound 2""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val attribute = MAttribute("attributeName", globalUnique = false, localUnique = false, StringType, StringValue(""), constant = false, singleAssignment = false,
      "", ordered = false, transient = false, 7, 0)
    val reference = MReference("reference", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq.empty, Seq.empty, Seq[MAttribute]
      (attribute), Seq.empty)
    val metaModel = TestUtil.referencesToMetaModel(Seq(reference))
    val result = EdgeAttributesUpperBound.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: EdgeAttributesUpperBound =>
        rule.edgeType should be("reference")
        rule.attributeType should be("attributeName")
        rule.upperBound should be(7)
      case _ => fail
    }
  }

}
