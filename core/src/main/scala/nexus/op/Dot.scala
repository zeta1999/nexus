package nexus.op

import nexus._

/**
 * Euclidean inner product of two vectors.
 * @author Tongfei Chen
 * @since 0.1.0
 */
object Dot extends PolyOp2[DotF]

trait DotF[X1, X2, Y] extends Op2[X1, X2, Y] {
  def name = "Dot"
}

object DotF {
  
  implicit def vector[T[_, _ <: $$], D, A](implicit env: Env[T, D]) = new DotF[T[D, A::$], T[D, A::$], T[D, $]] {
    import env._

    def forward(x1: T[D, A::$], x2: T[D, A::$]) = dot(x1, x2)
    def backward1(dy: T[D, $], y: T[D, $], x1: T[D, A::$], x2: T[D, A::$]) = x2 :* dy
    def backward2(dy: T[D, $], y: T[D, $], x1: T[D, A::$], x2: T[D, A::$]) = x1 :* dy
  }
  
}
