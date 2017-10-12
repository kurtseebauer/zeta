package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import java.util.UUID

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.EnumValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgeAttributeEnumTypesTest extends FlatSpec with Matchers {

  val mReference = MReference(
    "reference",
    "",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    Seq.empty,
    Seq.empty,
    Seq[MAttribute](),
    Seq.empty
  )
  val rule = new EdgeAttributeEnumTypes("reference", "attributeType", "enumName")

  "the rule" should "be true for valid edges" in {
    val mEnum = MEnum(name = "enumName", valueNames = Seq())
    val attribute: Map[String, Seq[AttributeValue]] = Map("attributeType" -> Seq(EnumValue("enumName", mEnum.name)))
    val edge = Edge.empty("", mReference.name, Seq.empty, Seq.empty)

    rule.isValid(edge).get should be(true)
  }

  it should "return None for non-matching edge" in {
    val differentMReference = MReference(
      "differentMReference",
      "",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      Seq.empty,
      Seq.empty,
      Seq[MAttribute](),
      Seq.empty
    )
    val edge = Edge.empty("", differentMReference.name, Seq(), Seq())

    rule.isValid(edge) should be(None)
  }

  it should "be false for invalid edges" in {
    val differentEnum = MEnum(name = "differentEnumName", valueNames = Seq())
    val attribute: Map[String, Seq[AttributeValue]] = Map("attributeType" -> Seq(EnumValue("differentEnumName", differentEnum.name)))
    val edge = Edge.empty("", mReference.name, Seq(), Seq())

    rule.isValid(edge).get should be(false)
  }

  it should "be None for non-matching edges" in {
    val differentReference = MReference(
      "differentRef",
      "",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      Seq.empty,
      Seq.empty,
      Seq[MAttribute](),
      Seq.empty
    )
    val edge = Edge.empty("", differentReference.name, Seq(), Seq())
    rule.isValid(edge) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Attributes ofType "attributeType" inEdges "reference" areOfEnumType "enumName"""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val enumType = MEnum("enumName", Seq("enumValue1", "enumValue2"))
    val enumAttribute = MAttribute(
      "attributeName",
      globalUnique = false,
      localUnique = false,
      enumType,
      enumType.values.head,
      constant = false,
      singleAssignment = false,
      "",
      ordered = false,
      transient = false,
      -1,
      0
    )
    val scalarAttribute = MAttribute(
      "attributeName2",
      globalUnique = false,
      localUnique = false,
      StringType,
      StringValue(""),
      constant = false,
      singleAssignment = false,
      "",
      ordered = false,
      transient = false,
      -1,
      0
    )
    val reference = MReference(
      "reference",
      "",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      Seq.empty,
      Seq.empty,
      Seq[MAttribute](enumAttribute, scalarAttribute),
      Seq.empty
    )
    val metaModel = TestUtil.referencesToMetaModel(Seq(reference))
    val result = EdgeAttributeEnumTypes.generateFor(metaModel)

    result.size should be (1)
    result.head match {
      case rule: EdgeAttributeEnumTypes =>
        rule.edgeType should be ("reference")
        rule.attributeType should be ("attributeName")
        rule.enumName should be ("enumName")
      case _ => fail
    }

  }

}
