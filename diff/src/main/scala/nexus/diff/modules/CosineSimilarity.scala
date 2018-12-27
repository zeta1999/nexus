package nexus.diff.modules

import nexus.diff._
import nexus.diff.ops._
import nexus._
import nexus.diff.syntax._

/**
 * Computes the cosine similarity between two vectors.
 * @author Tongfei Chen
 */
object CosineSimilarity extends PolyModule2 {
  implicit def cosineSimilarityF[T[_], R, A](implicit T: IsRealTensorK[T, R]): F[T[A], T[A], R] = F { (x1, x2) =>
    Dot(x1, x2) / L2Norm(x1) / L2Norm(x2)
  }
}
