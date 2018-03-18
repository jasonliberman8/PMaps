package pmaps

import scala.language.higherKinds


object PMapJoins extends PMapJoins

trait PMapJoins {
  
  implicit class PMapWDefaultJoins[K, V](left: PMapWDefault[K, V]){

    def outerJoin[VV, Out](right: PMapWDefault[K, VV])(implicit canJoin: CanJoin.Aux[V, VV, Out]): PMapWDefault[K, Out] = {
      val resultMap = PMapJoinHelper.outerJoin(left.dropDefault.toMap, right.dropDefault.toMap, left.defaultOpt, right.defaultOpt)(canJoin)
      PMapWDefault(resultMap, (k: K) => canJoin.join(left.default(k), right.default(k)))
    }

    def innerJoin[VV, Out](right: PMap[K, VV])(implicit canJoin: CanJoin.Aux[V, VV, Out]): PMap[K, Out] = {
      right.mapValuesWith { case (k, v) => canJoin.join(left(k), v) }
    }
  }

  implicit class PMapNoDefaultJoins[K, V](left: PMap[K, V]) {

    def leftJoin[VV, Out](right: PMap[K, VV])(implicit canJoin: CanJoin.Aux[V, VV, Out]): PMap[K, Out] = {
      left.mapValuesWith { case (k, v) =>
        val rightValue = (right.get(k) orElse right.defaultOpt.map(d => d(k)))
          .getOrElse(throw new IllegalArgumentException(s"Left Join fails. Right collection is missing key: $k found in left collection!"))
        canJoin.join(v, rightValue)
      }
    }

    def leftJoin[VV, Out](right: PMapWDefault[K, VV])(implicit canJoin: CanJoin.Aux[V, VV, Out]): PMap[K, Out] = {
      left.mapValuesWith { case (k, v) => canJoin.join(v, right(k))}
    }

    def outerJoin[VV, Out](right: PMap[K, VV])(implicit canJoin: CanJoin.Aux[V, VV, Out]): PMap[K, Out] = {
      val joinedMap = PMapJoinHelper.outerJoin(left.toMap, right.toMap, left.defaultOpt, right.defaultOpt)
      PMap(joinedMap)
    }

    def innerJoin[VV, Out](right: PMap[K, VV])(implicit canJoin: CanJoin.Aux[V, VV, Out]): PMap[K, Out] = {
      left
        .filter{case (k, _) => right.isDefinedAt(k)}
        .map {case (k, v) => (k, canJoin.join(v, right(k))) }
    }

    def innerJoin[VV, Out](right: PMapWDefault[K, VV])(implicit canJoin: CanJoin.Aux[V, VV, Out]): PMap[K, Out] = {
      left leftJoin right
    }

  }

}

object PMapJoinHelper{

    def outerJoin[K, V, VV, Out](
      left: Map[K, V],
      right: Map[K, VV],
      leftDefault: Option[K => V] = None,
      rightDefault: Option[K => VV] = None)(
      implicit canJoin: CanJoin.Aux[V, VV, Out]
    ): Map[K, Out] = {

      val keys = (left.keys ++ right.keys).toSeq.distinct

      keys.map { k =>

        val leftValue = (left.get(k) orElse leftDefault.map(d => d(k)))
          .getOrElse(throw new IllegalArgumentException(s"Outer Join fails. Left collection is missing key $k found in right collection"))
        val rightValue = (right.get(k) orElse rightDefault.map(d => d(k)))
          .getOrElse(throw new IllegalArgumentException(s"Outer Join fails. Right collection is missing key $k found in left collection"))

        k -> canJoin.join(leftValue, rightValue)

      }.toMap
    }
}
