package de.htwg.zeta.common.models.modelDefinitions.metaModel.elements

import scala.annotation.tailrec
import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel.MetaModelTraverseWrapper
import play.api.libs.json.Format
import play.api.libs.json.JsArray
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsString
import play.api.libs.json.JsValue
import play.api.libs.json.Json


/** The MClass implementation
 *
 * @param name           the name of the MClass instance
 * @param abstractness   defines if the MClass is abstract
 * @param superTypeNames the names of the supertypes of the MClass
 * @param inputs         the incoming MReferences
 * @param outputs        the outgoing MReferences
 * @param attributes     the attributes of the MClass
 */
case class MClass(
    name: String,
    abstractness: Boolean,
    superTypeNames: Seq[String],
    inputs: Seq[MReferenceLinkDef],
    outputs: Seq[MReferenceLinkDef],
    attributes: Seq[MAttribute],
    methods: Map[MethodDeclaration, MethodImplementation]
) extends MObject

object MClass {

  case class MClassTraverseWrapper(value: MClass, metaModel: MetaModelTraverseWrapper) {
    def superTypes: Seq[MClassTraverseWrapper] = {
      value.superTypeNames.map(name =>
        MClassTraverseWrapper(metaModel.classes(name).value, metaModel)
      )
    }

    /**
     * represents the supertype hierarchy of this particular MClass
     */
    lazy val typeHierarchy: Seq[MClassTraverseWrapper] = getSuperHierarchy(Seq(this), superTypes)

    /**
     * Determines the supertype hierarchy of this particular MClass
     *
     * @param acc     accumulated value of recursion
     * @param inspect the next MClass to check
     * @return MClasses that take part in the supertype hierarchy
     */
    private def getSuperHierarchy(acc: Seq[MClassTraverseWrapper], inspect: Seq[MClassTraverseWrapper]): Seq[MClassTraverseWrapper] = {
      inspect.foldLeft(acc) { (a, m) =>
        if (a.exists(_.value.name == m.value.name)) {
          a
        } else {
          getSuperHierarchy(acc :+ m, m.superTypes)
        }
      }
    }

    /**
     * Checks if certain input relationship is allowed, also based on supertypes
     *
     * @param inputName the name of the incoming relationship
     * @return true if the relationship is defined within the type hierarchy
     */
    def typeHasInput(inputName: String): Boolean = {
      typeHierarchy.exists(
        cls => cls.value.inputs.exists(link => link.referenceName == inputName)
      )
    }

    /**
     * Checks if certain output relationship is allowed, also based on supertypes
     *
     * @param outputName the name of the outgoing relationship
     * @return true if the relationship is defined within the type hierarchy
     */
    def typeHasOutput(outputName: String): Boolean = {
      typeHierarchy.exists(
        cls => cls.value.outputs.exists(link => link.referenceName == outputName)
      )
    }

    /**
     * Checks if MClass has a certain supertype
     *
     * @param superName the name of the supertype in question
     * @return true if the given name belongs to a supertype
     */
    def typeHasSuperType(superName: String): Boolean = {
      typeHierarchy.exists(
        cls => cls.value.name == superName
      )
    }

    /**
     * Returns all effective (inherited) MAttributes of this MClass
     *
     * @return the MAttributes
     */
    def getTypeMAttributes: Seq[MAttribute] = {
      typeHierarchy.flatMap(_.value.attributes)
    }

    /**
     * Finds an MAttribute within supertypes
     *
     * @param attributeName the name of the attribute to find
     * @return the MAttribute, if present
     */
    def findMAttribute(attributeName: String): Option[MAttribute] = {
      @tailrec
      def find(remaining: Seq[MClass]): Option[MAttribute] = {
        if (remaining.isEmpty) {
          None
        } else {
          val head = remaining.head
          val attribute = head.attributes.find(_.name == attributeName)
          if (attribute.isDefined) attribute else find(remaining.filter(_ != head))
        }
      }

      find(typeHierarchy.map(_.value))
    }

  }

  implicit val methodPlayJsonFormat = new Format[Map[MethodDeclaration, MethodImplementation]] {

    private val sString = "string"
    private val sBool = "bool"
    private val sInt = "int"
    private val sDouble = "double"

    private val sName = "name"
    private val sParameters = "parameters"
    private val sType = "type"

    override def writes(map: Map[MethodDeclaration, MethodImplementation]): JsValue = {
      val a = map.toList.map(e => JsObject(Map(
        sName -> JsString(e._1.name),
        sParameters -> JsArray(e._1.parameters.map(parameter => JsObject(Map(
          sName -> JsString(parameter.name),
          sType -> AttributeType.playJsonFormat.writes(parameter.typ)))))

      )))
      null // TODO
    }

    override def reads(json: JsValue): JsResult[Map[MethodDeclaration, MethodImplementation]] = {

      /* json match {
        case JsString(`sString`) => JsSuccess(StringType)
        case JsString(`sBool`) => JsSuccess(BoolType)
        case JsString(`sInt`) => JsSuccess(IntType)
        case JsString(`sDouble`) => JsSuccess(DoubleType)
        case _ => MEnum.playJsonFormat.reads(json)
      } */
      null // TODO
    }

  }

  implicit val playJsonFormat: Format[MClass] = Json.format[MClass]

}
