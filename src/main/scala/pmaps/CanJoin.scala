package pmaps

import shapeless.IsTuple
import shapeless.ops.tuple.Prepend
import scala.language.higherKinds


trait CanJoin[A, B] {
  type Out
  def join(a: A, b: B): Out
}

object CanJoin extends CanJoinPriority1 {

  type Aux[A, B, C] = CanJoin[A, B] {type Out = C}

  implicit def tuple4tuple1[A1, A2, A3, A4, B1]: CanJoin.Aux[(A1, A2, A3, A4), B1, (A1, A2, A3, A4, B1)] = new CanJoin[(A1, A2, A3, A4), B1]{
    override type Out = (A1, A2, A3, A4, B1)
    override def join(a: (A1, A2, A3, A4), b: B1): Out = (a._1, a._2, a._3, a._4, b)
  }

  implicit def tuple3tuple1[A1, A2, A3, B1]: CanJoin.Aux[(A1, A2, A3), B1, (A1, A2, A3, B1)] = new CanJoin[(A1, A2, A3), B1]{
    override type Out = (A1, A2, A3, B1)
    override def join(a: (A1, A2, A3), b: B1): Out = (a._1, a._2, a._3, b)
  }

  implicit def tuple2tuple1[A1, A2, B1]: CanJoin.Aux[(A1, A2), B1, (A1, A2, B1)] = new CanJoin[(A1, A2), B1]{
    override type Out = (A1, A2, B1)
    override def join(a: (A1, A2), b: B1): Out = (a._1, a._2, b)
  }

}

trait CanJoinPriority1{

  implicit def tuple1tuple1[A1, B1]: CanJoin.Aux[A1, B1, (A1, B1)] = new CanJoin[A1, B1]{
    override type Out = (A1, B1)
    override def join(a: A1, b: B1): Out = (a, b)
  }

}
