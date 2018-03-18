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

  implicit def tuplePrependJoinBothTuple1s[A, B, Out](implicit prepend: Prepend.Aux[Tuple1[A], Tuple1[B], Out]): CanJoin.Aux[A, B, Out] = new CanJoin[A, B]{

    override type Out = prepend.Out

    override def join(a: A, b: B): Out = prepend(Tuple1(a), Tuple1(b))
  }

}

trait CanJoinPriority1 extends CanJoinPriority2{

  implicit def tuplePrependJoinTuple1[A, B, Out](implicit prepend: Prepend.Aux[Tuple1[A], B, Out]): CanJoin.Aux[A, B, Out] = new CanJoin[A, B]{

    override type Out = prepend.Out

    override def join(a: A, b: B): Out = prepend(Tuple1(a), b)
  }
}


trait CanJoinPriority2{

  implicit def tuplePrependJoin[A: IsTuple, B, Out](implicit prepend: Prepend.Aux[A, B, Out]): CanJoin.Aux[A, B, Out] = new CanJoin[A, B]{

    override type Out = prepend.Out

    override def join(a: A, b: B): Out = prepend(a, b)
  }
}
