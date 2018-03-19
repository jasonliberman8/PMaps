package pmaps

import algebra.ring.AdditiveCommutativeGroup
import spire.algebra._
import shapeless.ops.tuple.Prepend
import shapeless.syntax.std.tuple._


import scala.language.higherKinds
import scala.language.postfixOps
import scala.language.implicitConversions

trait PMapAlgebraImplementations extends LowPriority0


trait LowPriority0 extends LowPriority1{

  implicit def additiveMonoidIsMonoid[T](implicit ev: AdditiveMonoid[T]): Monoid[T] = ev.additive

  implicit def pmapWDefaultField[K, V](implicit field: Field[V]): Field[PMapWDefault[K, V]] = new Field[PMapWDefault[K, V]] {

    override def one: PMapWDefault[K, V] = PMap.empty[K, V].withDefaultValue(field.one)

    override def gcd(a: PMapWDefault[K, V], b: PMapWDefault[K, V])(implicit ev: Eq[PMapWDefault[K, V]]): PMapWDefault[K, V] = {
      implicit val eq: Eq[V] = new Eq[V]{ def eqv(x: V, y: V): Boolean = ev.eqv(PMapWDefault.constant(x), PMapWDefault.constant(y))}
      (a outerJoin b).mapValues(field.gcd _ tupled)
    }

    override def lcm(a: PMapWDefault[K, V], b: PMapWDefault[K, V])(implicit ev: Eq[PMapWDefault[K, V]]): PMapWDefault[K, V] = {
      implicit val eq: Eq[V] = new Eq[V]{ def eqv(x: V, y: V): Boolean = ev.eqv(PMapWDefault.constant(x), PMapWDefault.constant(y))}
      (a outerJoin b).mapValues(field.lcm _ tupled)
    }

    override def times(x: PMapWDefault[K, V], y: PMapWDefault[K, V]): PMapWDefault[K, V] =
      (x outerJoin y).mapValues(field.times _ tupled)

    override def negate(x: PMapWDefault[K, V]): PMapWDefault[K, V] = x andThenApply field.negate

    override def zero: PMapWDefault[K, V] = PMap.empty[K, V].withDefaultValue(field.zero)

    override def div(x: PMapWDefault[K, V], y: PMapWDefault[K, V]): PMapWDefault[K, V] = {
      (x outerJoin y).mapValues(field.div _ tupled)
    }

    override def plus(x: PMapWDefault[K, V], y: PMapWDefault[K, V]): PMapWDefault[K, V] =
      (x outerJoin y).mapValues(field.plus _ tupled)
  }

}


trait LowPriority1 extends LowPriority2{
  
  implicit def pmapWDefaultVectorSpace[K, V, D](implicit ev: VectorSpace[V, D], prepend: Prepend[V, V]): VectorSpace[PMapWDefault[K, V], D] = new VectorSpace[PMapWDefault[K, V], D] {

    override implicit def scalar: Field[D] = implicitly

    override def timesl(r: D, v: PMapWDefault[K, V]): PMapWDefault[K, V] = v.mapValues(vv => ev.timesl(r, vv))

    override def negate(x: PMapWDefault[K, V]): PMapWDefault[K, V] = x.mapValues(ev.negate)

    override def zero: PMapWDefault[K, V] = PMap.empty[K, V].withDefaultValue(ev.zero)

    override def plus(x: PMapWDefault[K, V], y: PMapWDefault[K, V]): PMapWDefault[K, V] =
      (x outerJoin y).mapValues(ev.plus _ tupled)

  }

  implicit def pmapVectorSpace[K, V, D](implicit ev: VectorSpace[V, D], prepend: Prepend[V, V]): VectorSpace[PMap[K, V], D] = new VectorSpace[PMap[K, V], D] {

    override implicit def scalar: Field[D] = implicitly

    override def timesl(r: D, v: PMap[K, V]): PMap[K, V] = v.mapValues(vv => ev.timesl(r, vv))

    override def negate(x: PMap[K, V]): PMap[K, V] = x.mapValues(ev.negate)

    override def zero: PMap[K, V] = PMap.empty[K, V]

    override def plus(x: PMap[K, V], y: PMap[K, V]): PMap[K, V] =
      (x outerJoin y).mapValues(ev.plus _ tupled)

  }
    
}


trait LowPriority2 extends LowPriority3{

  implicit def pmapWDefaultIsRing[K, V](implicit ev: Ring[V]): Ring[PMapWDefault[K, V]] = new Ring[PMapWDefault[K, V]]{

    override def zero: PMapWDefault[K, V] = PMapWDefault.constant(ev.zero)

    override def one: PMapWDefault[K, V] = PMapWDefault.constant(ev.one)

    override def negate(x: PMapWDefault[K, V]): PMapWDefault[K, V] = x.mapValues(ev.negate)

    override def times(x: PMapWDefault[K, V], y: PMapWDefault[K, V]): PMapWDefault[K, V] =
      (x outerJoin y).mapValues(ev.times _ tupled)

    override def plus(x: PMapWDefault[K, V], y: PMapWDefault[K, V]): PMapWDefault[K, V] =
      (x outerJoin y).mapValues(ev.plus _ tupled)
  }


}

trait LowPriority3{

  implicit def pmapWDefaultIsCommutativeGrp[K, V](implicit ev: AdditiveCommutativeGroup[V]): AdditiveCommutativeGroup[PMapWDefault[K, V]] = new AdditiveCommutativeGroup[PMapWDefault[K, V]]{

    override def zero: PMapWDefault[K, V] = PMap.empty[K, V].withDefaultValue(ev.zero)

    override def negate(x: PMapWDefault[K, V]): PMapWDefault[K, V] = x.mapValues(ev.negate)

    override def plus(x: PMapWDefault[K, V], y: PMapWDefault[K, V]): PMapWDefault[K, V] =
      (x outerJoin y).mapValues(ev.plus _ tupled)
  }

  implicit def pmapIsCommutativeGrp[K, V](implicit ev: AdditiveCommutativeGroup[V]): AdditiveCommutativeGroup[PMap[K, V]] = new AdditiveCommutativeGroup[PMap[K, V]]{

    override def zero: PMap[K, V] = PMap.empty[K, V]

    override def negate(x: PMap[K, V]): PMap[K, V] = x.mapValues(ev.negate)

    override def plus(x: PMap[K, V], y: PMap[K, V]): PMap[K, V] =
      (x outerJoin y).mapValues(ev.plus _ tupled)
  }
}
