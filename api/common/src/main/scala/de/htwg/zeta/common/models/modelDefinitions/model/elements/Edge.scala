package de.htwg.zeta.common.models.modelDefinitions.model.elements

import java.util.UUID

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.Method
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.HasAttributeValues
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Reads
import play.api.libs.json.Writes


/** Represents an MReference type instance.
 *
 * @param id              the name of the edge
 * @param referenceName   the name of the MReference instance that represents the edge's type
 * @param source          the nodes that are the origin of relationships
 * @param target          the nodes that can be reached
 * @param attributeValues a map with attribute names and the assigned values
 */
case class Edge(
    id: UUID,
    referenceName: String,
    source: Seq[NodeLink],
    target: Seq[NodeLink],
    attributes: Seq[MAttribute],
    attributeValues: Map[String, Seq[AttributeValue]],
    methods: Seq[Method]
) extends ModelElement with HasAttributeValues

object Edge {

  trait HasEdges {

    val edges: Seq[Edge]

    /** Edges mapped to their own names. */
    final val edgeMap: Map[UUID, Edge] = Option(edges).fold(
      Map.empty[UUID, Edge]
    ) { edges =>
      edges.filter(Option(_).isDefined).map(edge => (edge.id, edge)).toMap
    }

  }

  def playJsonReads(metaModel: MetaModel): Reads[Edge] = {
    new Reads[Edge] {
      override def reads(json: JsValue): JsResult[Edge] = {
        for {
          id <- (json \ "id").validate[UUID]
          reference <- (json \ "referenceName").validate[String].map(metaModel.referenceMap)
          source <- (json \ "source").validate(Reads.map[List[UUID]])
          target <- (json \ "target").validate(Reads.map[List[UUID]])
          attributes <- (json \ "attributes").validate(Reads.list(MAttribute.playJsonReads(metaModel.enums)))
          attributeValues <- (json \ "attributeValues").validate(AttributeValue.playJsonReads(metaModel, reference.attributes, attributes))
          methods <- (json \ "methods").validate(Reads.list(Method.playJsonReads(metaModel.enums)))} yield {
          Edge(
            id = id,
            referenceName = reference.name,
            source = source.map(n => NodeLink(n._1, n._2)).toList,
            target = target.map(n => NodeLink(n._1, n._2)).toList,
            attributes = attributes,
            attributeValues = attributeValues,
            methods = methods
          )
        }
      }
    }
  }

  implicit val playJsonWrites: Writes[Edge] = Json.writes[Edge]

}
