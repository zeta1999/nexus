package nexus.diff

import nexus.diff.exception._

trait Op0[Y] extends Func0[Y] with AnyOp[Y] {
  final def arity = 0
  def forward(): Y
  def apply[D[_]]()(implicit D: Algebra[D]): D[Y] = D.app0(this)
}

/**
 * A unary function in computational graphs.
 * @author Tongfei Chen
 * @since 0.1.0
 */
trait Op1[X, Y] extends Func1[X, Y] with AnyOp[Y] {

  final def arity = 1

  /** Applies this operation to a symbolic expression. */
  def apply[D[_]](x: D[X])(implicit D: Algebra[D]): D[Y] = D.app1(this, x)

  /** Applies this operation to a concrete value (forward computation). */
  def forward(x: X): Y

  /**
   * Performs gradient backpropagation.
   * @param dy Gradient of loss wrt ''y''
   * @param y Value of ''y''
   * @param x Value of ''x''
   * @return Gradient of loss wrt ''x''
   */
  def backward(dy: Y, y: Y, x: X): X


}

object Op1 {
  def fromFunction[A, B](f: A => B): Op1[A, B] = new Op1[A, B] {
    def name = f.toString()
    def tag = Tag.none[B]
    override def differentiable = false
    def forward(x: A) = f(x)
    def backward(dy: B, y: B, x: A) = throw new OperatorNotDifferentiableException(this, 1)
  }
}

/**
 * A binary function in computational graphs.
 * @see [[Op1]]
 * @author Tongfei Chen
 * @since 0.1.0
 */
trait Op2[X1, X2, Y] extends Func2[X1, X2, Y] with AnyOp[Y] {

  final def arity = 2

  /** Applies this operation to two symbolic expressions. */
  def apply[D[_]](x1: D[X1], x2: D[X2])(implicit F: Algebra[D]) = F.app2(this, x1, x2)

  /** Applies this operation to two concrete values (forward computation). */
  def forward(x1: X1, x2: X2): Y

  /**
   * Performs gradient backpropagation wrt the first input.
   * @param dy Gradient of loss wrt ''y''
   * @param y Value of ''y''
   * @param x1 Value of ''x'',,1,,
   * @param x2 Value of ''x'',,2,,
   * @return Gradient of loss wrt ''x'',,1,,
   */
  def backward1(dy: Y, y: Y, x1: X1, x2: X2): X1

  /**
   * Performs gradient backpropagation wrt the second input.
   * @param dy Gradient of loss wrt ''y''
   * @param y Value of ''y''
   * @param x1 Value of ''x'',,1,,
   * @param x2 Value of ''x'',,2,,
   * @return Gradient of loss wrt ''x'',,2,,
   */
  def backward2(dy: Y, y: Y, x1: X1, x2: X2): X2

  def tupledOp: Op1[(X1, X2), Y] = new Op2.Tupled(this)
}

object Op2 {
  def fromFunction[X1, X2, Y](f: (X1, X2) => Y): Op2[X1, X2, Y] = new Op2[X1, X2, Y] {
    def name = f.toString()
    def tag = Tag.none[Y]
    override def differentiable = false
    def forward(x1: X1, x2: X2) = f(x1, x2)
    def backward1(dy: Y, y: Y, x1: X1, x2: X2) = throw new OperatorNotDifferentiableException(this, 1)
    def backward2(dy: Y, y: Y, x1: X1, x2: X2) = throw new OperatorNotDifferentiableException(this, 2)
  }

  class Tupled[X1, X2, Y](val op: Op2[X1, X2, Y]) extends Op1[(X1, X2), Y] {
    def tag = op.tag
    def forward(x: (X1, X2)) = op.forward(x._1, x._2)
    def backward(dy: Y, y: Y, x: (X1, X2)) = (op.backward1(dy, y, x._1, x._2), op.backward2(dy, y, x._1, x._2))
    def name = s"${op.name}.tupled"
  }
}

/**
 * A ternary function in computational graphs.
 * @see [[Op1]], [[Op2]]
 * @author Tongfei Chen
 * @since 0.1.0
 */
trait Op3[X1, X2, X3, Y] extends Func3[X1, X2, X3, Y] with AnyOp[Y] {

  final def arity = 3

  def forward(x1: X1, x2: X2, x3: X3): Y

  def apply[D[_]](x1: D[X1], x2: D[X2], x3: D[X3])(implicit F: Algebra[D]) = F.app3(this, x1, x2, x3)

  def backward1(dy: Y, y: Y, x1: X1, x2: X2, x3: X3): X1
  def backward2(dy: Y, y: Y, x1: X1, x2: X2, x3: X3): X2
  def backward3(dy: Y, y: Y, x1: X1, x2: X2, x3: X3): X3

}
