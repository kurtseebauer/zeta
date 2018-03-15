package de.htwg.zeta.common.format.project.gdsl.shape

import de.htwg.zeta.common.format.project.gdsl.style.StyleFormat
import de.htwg.zeta.common.models.project.gdsl.shape.Node
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import play.api.libs.json.Reads
import play.api.libs.json.Writes

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
class NodeFormat(
    styleFormat: StyleFormat,
    edgeFormat: EdgeFormat,
    sName: String = "name",
    sConceptElement: String = "conceptElement",
    sEdges: String = "edges",
    sSize: String = "size",
    sStyle: String = "style",
    sResizing: String = "resizing",
    sGeoModels: String = "geoModels"
) extends OFormat[Node] {

  override def writes(clazz: Node): JsObject = Json.obj(
    sName -> clazz.name,
    sConceptElement -> clazz.conceptElement,
    sEdges -> Writes.list(edgeFormat).writes(clazz.edges),
    sSize -> ???,
    sStyle -> styleFormat.writes(clazz.style),
    sResizing -> ???,
    sGeoModels -> ???
  )

  override def reads(json: JsValue): JsResult[Node] = for {
    name <- (json \ sName).validate[String]
    conceptElement <- (json \ sConceptElement).validate[String]
    edges <- (json \ sEdges).validate(Reads.list(edgeFormat))
    size <- (json \ sSize).validate(???)
    style <- (json \ sStyle).validate(styleFormat)
    resizing <- (json \ sResizing).validate(???)
    geoModels <- (json \ sGeoModels).validate(Reads.list(???))
  } yield {
    Node(
      name,
      conceptElement,
      edges,
      size,
      style,
      resizing,
      geoModels
    )
  }

}
