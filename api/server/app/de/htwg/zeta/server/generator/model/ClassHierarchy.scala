package de.htwg.zeta.server.generator.model

import grizzled.slf4j.Logging

/**
 * Created by julian on 24.09.15.
 * ClassHierarchy is able to simulate a class hierarchy it stores nodes, which are linked to parents and children
 * with this inheritance of objects can be achieved
 */
final class ClassHierarchy[T <: ContainerElement](rootClass: T) extends Logging {

  val root = Node(rootClass)
  var nodeView: Map[String, Node] = Map(root.data.name -> root)

  // several apply methods to simplify access on elements
  def apply(parent: Node, className: T): Unit = {
    parent.inheritedBy(className)
  }

  def apply(parent: T, className: T): Unit = apply(parent.name, className)

  def apply(parent: String, className: T): Unit = nodeView.get(parent).foreach(_.inheritedBy(className))

  def apply(className: T): Option[Node] = apply(className.name)

  def apply(className: Option[String]): Option[T] = className.flatMap(name => apply(name)).map(_.data)

  def apply(className: String): Option[Node] = nodeView.get(className)

  override def toString: String = root.toString

  def newBaseClass(className: T): Unit = root.inheritedBy(className)

  def get(className: String): Option[T] = {
    if (className.isEmpty) {
      None
    } else {
      nodeView.get(className).map(_.data)
    }
  }

  def setRelation(parent: T, child: T): Unit = apply(parent).foreach(_.inheritedBy(child))

  def contains(className: String): Boolean = {
    nodeView.contains(className)
  }

  final case class Node(
      data: T,
      var parents: List[Node] = List(),
      var children: List[Node] = List(),
      var depth: Int = 0) {
    def rPrint(): Unit = {
      children.foreach { e =>
        info(s"[$this]: $e")
        e.rPrint()
      }
    }

    override def toString: String = data.name

    def inheritsFrom(className: T): Unit = {
      val newNode = nodeView.get(className.name) match {
        case Some(n) if this.depth > n.depth =>
          n.depth = this.depth - 1
          n
        case Some(n) =>
          n
        case None =>
          val ret = Node(className, depth = this.depth - 1)
          nodeView += className.name -> ret
          ret
      }

      if (!parents.contains(newNode)) parents = parents.::(newNode)
      if (!newNode.children.contains(this)) newNode.children = newNode.children.::(this)
    }

    def inheritedBy(className: T): Unit = {
      val newNode = nodeView.get(className.name) match {
        case Some(n) if this.depth > n.depth =>
          n.depth  = this.depth + 1
          n
        case Some(n) =>
          n
        case None =>
          val ret = Node(className, depth = this.depth + 1)
          nodeView += className.name -> ret
          ret
      }

      if (!children.contains(newNode)) children = children.::(newNode)
      if (!newNode.parents.contains(this)) newNode.parents = newNode.parents.::(this)
    }
  }

}

object ClassHierarchy {
  /**
   * called with a collection of the wished classtype "stack" and a function "f", that solely acts a a getter
   * this methode will find the most relevant parent's attribute of the given type selected by f and return it.
   * If no parent has information about the given attribute None is returned.
   * "stack" should suggerate, that a collection is chosen, which guarantees, that the most relevant is "popped" first
   *
   * @param stack is a List of type C's which will be iterated
   * @param f     is a function, that will be used as a generic getter for any attribute of type T that is stored in a C
   * @return the matching attribute of type T wrapped as an Option or None if the attribute is not defined in the according C type instance
   */
  def mostRelevant[T, C](stack: List[C])(f: C => Option[T]): Option[T] = {
    stack.find(p => f(p).isDefined) match {
      case Some(parent) => f(parent)
      case None => None
    }
  }
}
