package pmaps

import spire.algebra.{AdditiveGroup, Monoid}
import scala.language.higherKinds
import PMapUtil._


sealed trait IPMap[K, V, This[k, v] <: IPMap[k, v, This]] {

  protected val innerMap: Map[K, V]

  def defaultOpt: Option[K => V]

  def updated[VV >: V](kv: (K, VV)): This[K, VV]

  def apply(k: K): V

  def equals(obj: Any): Boolean

  def toString: String

  def toMap: Map[K, V] = innerMap

  def +(kv: (K, V))(implicit ev: Monoid[V]): This[K, V] = {
    val (k, v) = kv
    val currentValue = innerMap.get(k) orElse defaultOpt.map(d => d(k)) getOrElse ev.empty
    val updatedValue = ev.combine(currentValue, v)
    this.updated((k, updatedValue))
  }

  def -(kv: (K, V))(implicit grp: AdditiveGroup[V]): This[K, V] = {
    implicit val monoid: Monoid[V] = grp.additive
    val (key, value) = kv
    this + (key -> grp.negate(value))
  }

}

class PMap[K, V](protected val innerMap: Map[K, V]) extends IPMap[K, V, PMap] with PartialFunction[K, V] {

  /**********************************************************************************************
    * Implementing IPMap
    * *******************************************************************************************/

  override def apply(k: K): V = this.getOrElse(k, throw new IllegalArgumentException(s"No value in PMap for key: $k"))

  override def defaultOpt: Option[K => V] = None

  override def updated[VV >: V](kv: (K, VV)): PMap[K, VV] = PMap(innerMap + kv)

  override def isDefinedAt(x: K): Boolean = innerMap.contains(x)

  override def equals(obj: Any): Boolean = obj match{
    case e: PMap[_, _] => this.innerMap == e.innerMap
    case _ => false
  }

  override def toString: String = innerMap.toString()


  /**********************************************************************************************
    * Lookups, adding, and removing key-value pairs
    * *******************************************************************************************/

  def get(f: K): Option[V] = innerMap.get(f)

  def getOrElse(k: K, default: => V): V = innerMap.getOrElse(k, default)

  def append[VV >: V](kv: (K, VV)): PMap[K, VV] = {
    val key = kv._1
    if(innerMap.contains(key))
      throw new IllegalArgumentException(s"Tried appended key $key that is already contained in the PMap!")
    else
      this.updated(kv)
  }

  def remove(k: K): PMap[K, V] = PMap(innerMap - k)

  def getOrZero(k: K)(implicit ev: Monoid[V]): V = this.getOrElse(k, ev.empty)

  def contains(k: K): Boolean = innerMap.contains(k)


  /**********************************************************************************************
    * Utility functions and conversions
    * *******************************************************************************************/

  def size: Int = innerMap.size

  def isEmpty: Boolean = innerMap.isEmpty

  def nonEmpty: Boolean = innerMap.nonEmpty

  def toSeq: Seq[(K, V)] = innerMap.toSeq

  def keys: Seq[K] = innerMap.keys.toSeq

  def values: Seq[V] = innerMap.values.toSeq

  def iterator: Iterator[(K, V)] = innerMap.iterator

  def withDefault(default: K => V): PMapWDefault[K, V] = PMapWDefault(innerMap, default)

  def withDefaultValue(v: V): PMapWDefault[K, V] = PMapWDefault(innerMap, (_: K) => v)

  def withDefaultZero(implicit ev: Monoid[V]): PMapWDefault[K, V] = this.withDefaultValue(ev.empty)

  def liftToOption: PMapWDefault[K, Option[V]] =
    this.mapValues(v => Option(v)).withDefaultValue(None)


  /**********************************************************************************************
    * Iterations, transforamations, folds
    * *******************************************************************************************/

  def filter(p: (K, V) => Boolean): PMap[K, V] = {
    PMap(innerMap.filter(p.tupled))
  }

  def filterNot(p: (K, V) => Boolean): PMap[K, V] = {
    PMap(innerMap.filterNot(p.tupled))
  }

  def mapValues[VV](f: V => VV): PMap[K, VV] = PMap(innerMap.mapValues(f))

  def foreach[A](f: (K, V) => A): Unit = innerMap.foreach(f.tupled)

  def groupBy[B](f: (K, V) => B): PMap[B, PMap[K, V]] = {
    val grouped = innerMap.groupBy(f.tupled).mapValues(m => PMap(m))
    PMap(grouped)
  }

  def map[KK, VV](f: (K, V) => (KK, VV)): PMap[KK, VV] = {
    val kvs = this.innerMap.toSeq.map(f.tupled)
    val resultMap = kvs.toMap
    if(resultMap.size != innerMap.size)
      throw new IllegalStateException("Called map on PMap and mapped to duplicate keys!")
    else
      PMap(resultMap)
  }

  def mapValuesWith[VV](f: (K, V) => VV): PMap[K, VV] = {
    this.map{case (k, v) => (k, f(k, v))}
  }

  def mapSum[KK, VV](f: (K, V) => (KK, VV))(implicit ev: Monoid[VV]): PMap[KK, VV] = {
    val kvs = this.toSeq.map(f.tupled)
    val kvMap = sumByKey(kvs)(ev)
    PMap(kvMap)
  }

  def sum(implicit ev: Monoid[V]): V = innerMap.foldLeft(ev.empty){case (acc, (_, v)) => ev.combine(acc, v)}

  def flattenBy[KK](f: K => KK)(implicit ev: Monoid[V]): PMap[KK, V] =
    groupBy{case (k, v) => f(k)}.mapValues(_.sum)

  def flatMap[KK, VV](f: (K, V) => Seq[(KK, VV)]): PMap[KK, VV] = {
    val kvSeq: Seq[(KK, VV)] = this.toSeq.flatMap(kv => f.tupled(kv))
    PMap(kvSeq)
  }

  def flatMapSum[KK, VV, P[k, v] <: IPMap[k, v, P]](
     f: (K, V) => Seq[(KK, VV)])(
     implicit ev: Monoid[VV]
  ): PMap[KK, VV] = {
    val kvSeq = this.toSeq.flatMap(kv => f.tupled(kv))
    PMap(sumByKey(kvSeq)(ev))
  }

  def exists(p: (K, V) => Boolean): Boolean = innerMap.exists(p.tupled)

  def count(p: (K, V) => Boolean): Int = innerMap.count(p.tupled)

  def foldLeft[B](z: B)(op: (B, (K, V)) => B): B = this.innerMap.foldLeft(z)(op)

  def fold(z: (K, V))(op: ((K, V), (K, V)) => (K, V)): (K, V) = this.innerMap.fold(z)(op)

  def reduceValues(op: (V, V) => V)(implicit monoid: Monoid[V]): V =
    foldLeft(monoid.empty){case (acc, (_, v)) => monoid.combine(acc, v)}

  def find(p: ((K, V)) => Boolean): Option[(K, V)] = innerMap.find(p)

  def sumBy[B](f: (K, V) => B)(implicit monoid: Monoid[B]): B =
    foldLeft(monoid.empty)((b, kv) => monoid.combine(b, f.tupled(kv)))

  def maxBy[B](f:((K, V)) => B)(implicit ord: Ordering[B]): (K, V) = innerMap.maxBy(f)

  def minBy[B](f:((K, V)) => B)(implicit ord: Ordering[B]): (K, V) = innerMap.minBy[B](f)

  def max[B >: (K, V)](implicit ord: Ordering[(K, V)]): (K, V) = innerMap.max(ord)

  def min[B >: (K, V)](implicit ord: Ordering[(K, V)]): (K, V) = innerMap.min(ord)

}

object PMap {

  def apply[K, V](seq: Seq[(K, V)]): PMap[K, V] =  {

    val valuesMap = seq.toMap

    val hasDuplicateKeys = valuesMap.size != seq.size

    if(hasDuplicateKeys){
      throw new IllegalArgumentException(s"Tried constructing PMap with duplicate keys!")
    }
    else {
      new PMap(valuesMap)

    }
  }

  def build[K, V](elems: (K, V)*): PMap[K, V] = apply(elems)

  def apply[K, V](innerMap: Map[K, V]): PMap[K, V] = new PMap(ensureNoDefault(innerMap))

  def empty[K, V]: PMap[K, V] = new PMap(Map.empty[K, V])



}

private [pmaps] object PMapUtil{


  def sumByKey[K, V](kvs: Seq[(K, V)])(implicit ev: Monoid[V]): Map[K, V] = {
    kvs
      .groupBy { case (k, v) => k }
      .mapValues {
        _.map { case (k, v) => v }.foldLeft(ev.empty)((acc, v) => ev.combine(acc, v))
      }
  }

  def ensureNoDefault[K, V](in: Map[K, V]): Map[K, V] = {
    in match{
      case _ if in.isInstanceOf[Map.WithDefault[K, V]] => throw new IllegalArgumentException(s"Cannot build PMap on Map of type" +
        s"Map.WithDefault!")
      case _ => in
    }
  }
}


class PMapWDefault[K, V](protected val innerMap: Map[K, V], val default: K => V)
  extends IPMap[K, V, PMapWDefault] with Function[K, V] {

  override def defaultOpt: Option[K => V] = Some(default)

  override def updated[VV >: V](kv: (K, VV)): PMapWDefault[K, VV] =
    PMapWDefault(innerMap + kv, default)

  override def apply(k: K): V = innerMap.getOrElse(k, default(k))

  override def equals(obj: Any): Boolean = obj match{
    case e: PMapWDefault[_, _] => this.innerMap == e.innerMap && (this.default == e.default)
    case _ => false
  }

  override def toString = s"Map: ${innerMap.toString()}. Default: ${default.toString()}"

  def get(k: K): V = apply(k)

  def mapValues[VV](f: V => VV): PMapWDefault[K, VV] = PMapWDefault(innerMap.mapValues(f), default andThen f)

  def andThenApply[VV](g: V => VV): PMapWDefault[K, VV] = this.mapValues(g)

  def dropDefault: PMap[K, V] = PMap(innerMap)

  def mapValuesWith[VV](f: (K, V) => VV): PMapWDefault[K, VV] = {
    PMapWDefault(innerMap.map { case (k, v) => (k, f(k, v)) }, (k: K) => f(k, default(k)))
  }

}


object PMapWDefault {

  def apply[K, V](innerMap: Map[K, V], default: K => V): PMapWDefault[K, V] =
    new PMapWDefault(ensureNoDefault(innerMap), default)

  def empty[K, V](default: K => V): PMapWDefault[K, V] = new PMapWDefault(Map.empty[K, V], default)

  def apply[K, V](values: Seq[(K, V)], default: K => V): PMapWDefault[K, V] = {

    val valuesMap = values.toMap
    val hasDuplicateKeys = valuesMap.size != values.size

    if(hasDuplicateKeys){
      throw new IllegalArgumentException(s"Tried constructing PMap with duplicate keys!")
    }
    else{
      new PMapWDefault(valuesMap, default)
    }
  }

  def constant[K, V](v: V): PMapWDefault[K, V] = PMapWDefault(Map.empty[K, V], (_: K) => v)
}




