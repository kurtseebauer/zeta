package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.AttributeValue.MString
import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.model.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodeAttributesLowerBoundTest extends FlatSpec with Matchers {

  val rule = new NodeAttributesLowerBound("nodeType", "attributeType", 2)
  val mClass = MClass("nodeType", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute]())

  "isValid" should "return true on nodes with 2 or more attributes of type attributeType" in {
    val twoAttributes = Map("attributeType" -> Seq(MString("att1"), MString("att2")))
    val twoAttributeNode = Node("", mClass, Seq(), Seq(), twoAttributes)

    rule.isValid(twoAttributeNode).get should be(true)

    val threeAttributes = Map("attributeType" -> Seq(MString("att1"), MString("att2"), MString("att3")))
    val threeAttributesNode = Node("", mClass, Seq(), Seq(), threeAttributes)

    rule.isValid(threeAttributesNode).get should be(true)
  }

  it should "return false on nodes with less than 2 attributes of type attributeType" in {
    val noAttributes = Map("attributeType" -> Seq())
    val noAttributesNode = Node("", mClass, Seq(), Seq(), noAttributes)

    rule.isValid(noAttributesNode).get should be(false)

    val oneAttribute = Map("attributeType" -> Seq(MString("att")))
    val oneAttributeNode = Node("", mClass, Seq(), Seq(), oneAttribute)

    rule.isValid(oneAttributeNode).get should be(false)
  }

  it should "return None on non-matching nodes" in {
    val differentMClass = MClass("differentNodeType", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute]())
    val node = Node("", differentMClass, Seq(), Seq(), Map.empty)

    rule.isValid(node) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Attributes ofType "attributeType" inNodes "nodeType" haveLowerBound 2""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val attribute = MAttribute("attributeName", globalUnique = false, localUnique = false, ScalarType.String, MString(""), constant = false, singleAssignment = false, "", ordered = false, transient = false, -1, 5)
    val mClass = MClass("class", abstractness = false, superTypes = Seq[MClass](), Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute](attribute))
    val metaModel = TestUtil.toMetaModel(Seq(mClass))
    val result = NodeAttributesLowerBound.generateFor(metaModel)

    result.size should be (1)
    result.head match {
      case rule: NodeAttributesLowerBound =>
        rule.nodeType should be ("class")
        rule.attributeType should be ("attributeName")
        rule.lowerBound should be (5)
      case _ => fail
    }

  }

}
