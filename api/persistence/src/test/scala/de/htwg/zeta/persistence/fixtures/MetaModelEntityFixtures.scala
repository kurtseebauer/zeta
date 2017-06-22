package de.htwg.zeta.persistence.fixtures

import java.util.UUID

import de.htwg.zeta.common.models.entity.MetaModelEntity
import de.htwg.zeta.common.models.modelDefinitions.helper.HLink
import de.htwg.zeta.common.models.modelDefinitions.metaModel.Diagram
import de.htwg.zeta.common.models.modelDefinitions.metaModel.Dsl
import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.Shape
import de.htwg.zeta.common.models.modelDefinitions.metaModel.Style
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.BoolType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.DoubleType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.IntType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MBool
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MDouble
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MInt
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MString
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClassLinkDef
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReferenceLinkDef

object MetaModelEntityFixtures {

  val links1 = Seq(
    HLink(rel = "rel1", href = "href1", method = "method1"),
    HLink(rel = "rel2", href = "href2", method = "method2")
  )

  val links2 = Seq(
    HLink(rel = "rel3", href = "href3", method = "method3"),
    HLink(rel = "rel4", href = "href4", method = "method4"),
    HLink(rel = "rel5", href = "href5", method = "method5")
  )

  val dsl1 = Dsl(
    diagram = Some(Diagram("diagramCode1")),
    shape = Some(Shape("shapeCode1")),
    style = None
  )

  val dsl2 = Dsl(
    diagram = None,
    shape = None,
    style = Some(Style("styleCode1"))
  )

  val stringAttribute = MAttribute(
    name = "stringAttribute",
    globalUnique = true,
    localUnique = false,
    typ = StringType,
    default = MString("stringValue"),
    constant = false,
    singleAssignment = true,
    expression = "stringExpression",
    ordered = false,
    transient = true,
    upperBound = 1,
    lowerBound = 2
  )

  val boolAttribute = MAttribute(
    name = "boolAttribute",
    globalUnique = false,
    localUnique = false,
    typ = BoolType,
    default = MBool(false),
    constant = false,
    singleAssignment = true,
    expression = "boolExpression",
    ordered = true,
    transient = false,
    upperBound = 2,
    lowerBound = 3
  )

  val doubleAttribute = MAttribute(
    name = "doubleAttribute",
    globalUnique = false,
    localUnique = true,
    typ = DoubleType,
    default = MDouble(-1.5),
    constant = true,
    singleAssignment = true,
    expression = "doubleExpression",
    ordered = true,
    transient = false,
    upperBound = 2,
    lowerBound = 1
  )

  val intAttribute = MAttribute(
    name = "intAttribute",
    globalUnique = true,
    localUnique = false,
    typ = IntType,
    default = MInt(2),
    constant = true,
    singleAssignment = false,
    expression = "intExpression",
    ordered = true,
    transient = false,
    upperBound = 3,
    lowerBound = 0
  )

  val enum1 = MEnum("enum1", Set("enumValue1", "enumValue2"))
  val enum2 = MEnum("enum2", Set.empty)

  val enumAttribute = MAttribute(
    name = "enumAttribute",
    globalUnique = false,
    localUnique = true,
    typ = enum1,
    default = enum1.symbols.head,
    constant = false,
    singleAssignment = true,
    expression = "enumExpression",
    ordered = true,
    transient = true,
    upperBound = 1,
    lowerBound = 3
  )

  val referenceName1 = "referenceName1"
  val referenceName2 = "referenceName2"

  val className1 = "className1"
  val className2 = "className2"

  val referenceLinkDef1 = MReferenceLinkDef(
    referenceName = referenceName1,
    upperBound = 0,
    lowerBound = 1,
    deleteIfLower = true
  )

  val referenceLinkDef2 = MReferenceLinkDef(
    referenceName = referenceName2,
    upperBound = 2,
    lowerBound = 3,
    deleteIfLower = false
  )

  val classLinkDef1 = MClassLinkDef(
    className = className1,
    upperBound = 0,
    lowerBound = 1,
    deleteIfLower = true
  )

  val classLinkDef2 = MClassLinkDef(
    className = className2,
    upperBound = -1,
    lowerBound = 2,
    deleteIfLower = false
  )

  val class1 = MClass(
    name = "class1",
    abstractness = true,
    superTypeNames = Set(className1, className2),
    inputs = Set(referenceLinkDef1, referenceLinkDef2),
    outputs = Set.empty,
    attributes = Set(intAttribute, doubleAttribute, enumAttribute)
  )

  val class2 = MClass(
    name = "class2",
    abstractness = false,
    superTypeNames = Set(className2),
    inputs = Set.empty,
    outputs = Set(referenceLinkDef1, referenceLinkDef2),
    attributes = Set(stringAttribute, boolAttribute)
  )

  val reference1 = MReference(
    name = referenceName1,
    sourceDeletionDeletesTarget = true,
    targetDeletionDeletesSource = true,
    source = Set(classLinkDef1, classLinkDef2),
    target = Set.empty,
    attributes = Set(enumAttribute, stringAttribute)
  )

  val reference2 = MReference(
    name = referenceName2,
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    source = Set.empty,
    target = Set(classLinkDef1, classLinkDef2),
    attributes = Set(intAttribute, doubleAttribute)
  )

  val metaModel1 = MetaModel(
    name = "metaModel1",
    classes = Set(class1, class2),
    references = Set(reference1, reference2),
    enums = Set(enum1, enum2),
    uiState = "uiState1"
  )

  val metaModel2: MetaModel = MetaModel(
    name = "metaModel2",
    classes = Set(class1),
    references = Set(reference1),
    enums = Set(enum1),
    uiState = "uiState2"
  )

  val entity1 = MetaModelEntity(
    id = UUID.randomUUID,
    rev = "rev1",
    name = "metaModelEntity1",
    metaModel = metaModel1,
    dsl = dsl1,
    validator = Some("validator1"),
    links = Some(links1)
  )

  val entity2 = MetaModelEntity(
    id = UUID.randomUUID,
    rev = "rev2",
    name = "metaModelEntity2",
    metaModel = metaModel2,
    dsl = dsl2,
    validator = Some("validator2"),
    links = Some(links2)
  )

  val entity2Updated: MetaModelEntity = entity2.copy(dsl = dsl1)

  val entity3 = MetaModelEntity(
    id = UUID.randomUUID,
    rev = "rev3",
    name = "metaModelEntity3",
    metaModel = metaModel1,
    dsl = dsl2,
    validator = Some("validator3"),
    links = Some(links2)
  )

}