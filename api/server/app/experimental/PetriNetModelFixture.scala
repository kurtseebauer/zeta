package experimental

import java.util.UUID

import de.htwg.zeta.common.models.entity.ModelEntity
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.EnumSymbol
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MBool
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MInt
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MString
import de.htwg.zeta.common.models.modelDefinitions.model.Model
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import de.htwg.zeta.common.models.modelDefinitions.model.elements.ToEdges
import de.htwg.zeta.common.models.modelDefinitions.model.elements.ToNodes


object PetriNetModelFixture {

  private val sPlace = "Place"
  private val sTransition = "Transition"
  private val sConsumer = "Consumer"
  private val sProducer = "Producer"

  private val sPlace1 = "Place1"
  private val sTransition1 = "Transition1"
  private val sConsumer1 = "Consumer1"
  private val sProducer1 = "Producer1"

  private val sPlace2 = "Place2"
  private val sTransition2 = "Transition2"
  private val sConsumer2 = "Consumer2"
  private val sProducer2 = "Producer2"

  private val sTokens = "tokens"
  private val sName = "name"
  private val sFired = "fired"


  val transition1: Node = Node(
    name = sTransition1,
    className = sTransition,
    outputs = List(ToEdges(
      referenceName = sConsumer,
      edgeNames = List(sConsumer1)
    )),
    inputs = List(ToEdges(
      referenceName = sProducer,
      edgeNames = List(sProducer1)
    )),
    attributes = Map(
      sName -> List(MString(sTransition1)),
      sFired -> List(MBool(false))
    )
  )

  val transition2: Node = Node(
    name = sTransition2,
    className = sTransition,
    outputs = List(ToEdges(
      referenceName = sConsumer,
      edgeNames = List(sConsumer2)
    )),
    inputs = List(ToEdges(
      referenceName = sProducer,
      edgeNames = List(sProducer2)
    )),
    attributes = Map(
      sName -> List(MString(sTransition2)),
      sFired -> List(MBool(false))
    )
  )

  val place1: Node = Node(
    name = sPlace1,
    className = sPlace,
    outputs = List(ToEdges(
      referenceName = sProducer,
      edgeNames = List(sProducer1)
    )),
    inputs = List(ToEdges(
      referenceName = sConsumer,
      edgeNames = List(sConsumer2)
    )),
    attributes = Map(
      sName -> List(MString(sPlace1)),
      sTokens -> List(MInt(3))
    )
  )

  val place2: Node = Node(
    name = sPlace2,
    className = sPlace,
    outputs = List(ToEdges(
      referenceName = sProducer,
      edgeNames = List(sProducer2)
    )),
    inputs = List(ToEdges(
      referenceName = sConsumer,
      edgeNames = List(sConsumer1)
    )),
    attributes = Map(
      sName -> List(MString(sPlace2)),
      sTokens -> List(MInt(0))
    )
  )

  val producer1: Edge = Edge(
    name = sProducer1,
    referenceName = sProducer,
    source = List(ToNodes(
      className = sPlace,
      nodeNames = List(sPlace1)
    )),
    target = List(ToNodes(
      className = sTransition,
      nodeNames = List(sTransition1)
    )),
    attributes = Map(
      sName -> List(MString(sProducer1))
    )
  )

  val producer2: Edge = Edge(
    name = sProducer2,
    referenceName = sProducer,
    source = List(ToNodes(
      className = sPlace,
      nodeNames = List(sPlace2)
    )),
    target = List(ToNodes(
      className = sTransition,
      nodeNames = List(sTransition2)
    )),
    attributes = Map(
      sName -> List(MString(sProducer2))
    )
  )

  val consumer1: Edge = Edge(
    name = sConsumer1,
    referenceName = sConsumer,
    source = List(ToNodes(
      className = sTransition,
      nodeNames = List(sTransition1)
    )),
    target = List(ToNodes(
      className = sPlace,
      nodeNames = List(sPlace2)
    )),
    attributes = Map(
      sName -> List(MString(sConsumer1))
    )
  )

  val consumer2: Edge = Edge(
    name = sConsumer2,
    referenceName = sConsumer,
    source = List(ToNodes(
      className = sTransition,
      nodeNames = List(sTransition2)
    )),
    target = List(ToNodes(
      className = sPlace,
      nodeNames = List(sPlace1)
    )),
    attributes = Map(
      sName -> List(MString(sConsumer2))
    )
  )

  val model: Model = Model(
    name = "SimplePetriNet",
    metaModelId = UUID.randomUUID(),
    nodes = List(place1, place2, transition1, transition2),
    edges = List(producer1, producer2, consumer1, consumer2),
    attributes = Map(
      "state" -> List(EnumSymbol("State", "Resting"))
    ),
    uiState = "uiState"
  )

  val modelEntity: ModelEntity = ModelEntity(
    id = UUID.randomUUID(),
    model = model
  )

}
