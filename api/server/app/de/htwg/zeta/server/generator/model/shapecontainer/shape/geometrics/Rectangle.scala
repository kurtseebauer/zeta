package de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics

import de.htwg.zeta.server.generator.model.shapecontainer.shape.Compartment
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.layouts.CommonLayoutParser
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.layouts.RectangleEllipseLayout
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.layouts.CommonLayout
import de.htwg.zeta.server.generator.model.style.Style
import de.htwg.zeta.server.generator.parser.Cache
import de.htwg.zeta.server.generator.parser.GeoModel

/**
 * Created by julian on 19.10.15.
 * representation of  Rectangle
 */
class Rectangle(
    parent: Option[GeometricModel] = None,
    commonLayout: CommonLayout,
    val compartment: Option[Compartment],
    wrapping: List[GeoModel] = List[GeoModel]()
) extends GeometricModel(parent) with RectangleEllipseLayout with Wrapper {

  override val style: Option[Style] = commonLayout.style
  override val position: Option[(Int, Int)] = commonLayout.position
  override val size_width: Int = commonLayout.size_width
  override val size_height: Int = commonLayout.size_height
  override val children: List[GeometricModel] = wrapping.map(_.parse(Some(this), style).get)
}

object Rectangle {
  /**
   * parses a GeoModel into an actual GeometricModel, in this case a Rectangle
   * @param geoModel is the sketch to parse into a GeometricModel
   * @param parent is the parent instance that wraps the new GeometricModel
   */
  def apply(geoModel: GeoModel, parent: Option[GeometricModel] = None, parentStyle: Option[Style], diagram: Cache) = {
    parse(geoModel, parent, parentStyle, diagram)
  }
  private def parse(
    geoModel: GeoModel,
    parent: Option[GeometricModel] = None,
    parentStyle: Option[Style],
    diagram: Cache): Option[Rectangle] = {

    // mapping
    val commonLayout: Option[CommonLayout] = CommonLayoutParser.parse(geoModel, parentStyle, diagram)
    val compartmentInfo: Option[Compartment] = Compartment(geoModel.attributes)

    if (commonLayout.isEmpty) {
      None
    } else {
      Some(new Rectangle(parent, commonLayout.get, compartmentInfo, geoModel.children))
    }
  }
}
