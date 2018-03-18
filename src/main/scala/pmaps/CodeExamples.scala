//package pmaps
//
//import pmaps._
//import spire.implicits._
//
//
//import scala.language.higherKinds
//
//object CodeExamples {
//
//  trait Author
//  trait Book{
//    def authors: Seq[Author]
//  }
//  type Rating = Double
//  type QuantitySold = Int
//
//  val functionalProgrammingInScala: Book = ???
//
//
//
//
//        val bookRatingsNoDefault: Map[Book, Rating] = ???
//
//
//        val bookRatings: Map[Book, Rating] =
//
//          bookRatingsNoDefault.withDefaultValue(0.0)
//
//        /**
//          *
//          * Later on...downstream......
//          *
//          */
//
//        val redBookRating: Rating =
//
//          bookRatings(functionalProgrammingInScala)
//
//
//
//        val bookSales: Map[Book, QuantitySold] = ???
//
//
//        val quantitySoldByAuthor: Map[Author, QuantitySold] =
//
//          bookSales.flatMap{case (book, quantity) =>
//
//            book.authors.map(author => (author, quantity))
//
//          }
//
//
//      implicit class mapops[K, V](in: Map[K, V]){
//        def +(that: Map[K,V]): Map[K, V] = ???
//      }
//
//
//
//
//        val usBookSales: Map[Book, QuantitySold] = ???
//        val eurBookSales: Map[Book, QuantitySold] = ???
//        val asiaBookSales: Map[Book, QuantitySold] = ???
//        val africaBookSales: Map[Book, QuantitySold] = ???
//
//
//        val combinedSales: Map[Book, QuantitySold] =
//
//          usBookSales + eurBookSales + asiaBookSales + africaBookSales
//
//
//        val allBooks: Seq[Book] =
//          (usBookSales.keys ++
//            eurBookSales.keys ++
//            asiaBookSales.keys).toSeq.distinct
//
//
//        val combinedBookSales: Map[Book, QuantitySold] =
//
//          allBooks.map{book =>
//
//            val total =
//              usBookSales.getOrElse(book, 0) +
//              eurBookSales.getOrElse(book, 0) +
//              asiaBookSales.getOrElse(book, 0) +
//              africaBookSales.getOrElse(book, 0)
//
//            (book, total)}.toMap
//
//
//
//
//
//
//
//}
//
//
//object OtherExamples{
//
//
//
////      trait IPMap[K, V, This[k, v] <: IPMap[K, V, This]]{
////
////        protected def innerMap: Map[K, V]
////
////
////      }
////
////      class PMap[K, V](in: Map[K, V])
////        extends IPMap[K, V, PMap] with PartialFunction[K, V]{
////
////        override protected def innerMap: Map[K, V] = ???
////
////        override def apply(v1: K): V = innerMap.getOrElse(v1, keyNotFoundError())
////
////        override def isDefinedAt(x: K): Boolean = innerMap.contains(x)
////      }
////
////
////      class PMapWDefault[K, V](in: Map[K, V], default: K => V)
////        extends IPMap[K, V, PMapWDefault] with Function[K, V]{
////
////        override protected def innerMap: Map[K, V] = ???
////
////        override
////
////        override def apply(v1: K): V = ???
////      }
//
//
//
//
//}
//
//
//class UsingPMaps{
//
//  trait Author
//  trait Book{
//    def authors: Seq[Author]
//  }
//  type QuantitySold = Double
//
//
//
//
//      val bookSales: PMap[Book, QuantitySold] = ???
//
//      val bookSalesFail: PMap[Book, QuantitySold] =
//
//        bookSales.withDefaultValue(0.0)
//
//      val bookSalesWDefault: PMapWDefault[Book, QuantitySold] =
//
//        bookSales.withDefaultValue(0.0)
//
//
//
//      val quantitySoldByAuthor: PMap[Author, QuantitySold] =
//
//
//        bookSales.flatMap{case (book, quantity) =>
//
//          book.authors.map(author => (author, quantity))
//
//      }
//
//    val functionalProgrammingInScala: Book = ???
//
//
//
//      val bookSales: PMap[Book, QuantitySold] = ???
//
//
//      val bookSalesWithRedBook: PMap[Book, QuantitySold] =
//
//        bookSales.append(functionalProgrammingInScala -> 100.0)
//
//
//
//
//  trait A
//  trait B
//  trait C
//
//
//
//
//
//        def f(a: A): B = ???
//
//        def g(a: A): C = ???
//
//        def h(a: A): (B, C) = (f(a), g(a))
//
//
//
//
//
//
//      val usBookSales: PMap[Book, QuantitySold] = ???
//      val eurBookSales: PMap[Book, QuantitySold] = ???
//      val asiaBookSales: PMap[Book, QuantitySold] = ???
//      val africaBookSales: PMap[Book, QuantitySold] = ???
//
//      val usBookSalesOrZero: PMapWDefault[Book, QuantitySold] = ???
//      val eurBookSalesOrZero: PMapWDefault[Book, QuantitySold] = ???
//      val asiaBookSalesOrZero: PMapWDefault[Book, QuantitySold] = ???
//      val africaBookSalesOrZero: PMapWDefault[Book, QuantitySold] = ???
//
//
//      val joined: PMap[Book, (QuantitySold, QuantitySold)] =
//
//        usBookSales leftJoin eurBookSales.withDefaultZero
//
//
//      val description: PMap[Book, String] =
//
//        (usBookSales leftJoin eurBookSales.withDefaultZero)
//          .mapValuesWith{ case (book, (usSales, eurSales)) =>
//            s"$book sold $usSales copies in the US and $eurSales copies in Europe!"
//      }
//
//
//      val totalSales: PMapWDefault[Book, QuantitySold] =
//
//        usBookSalesOrZero + eurBookSalesOrZero + asiaBookSalesOrZero + africaBookSalesOrZero
//
//
//      val randomAlgebra: PMapWDefault[Book, QuantitySold] =
//
//        (usBookSalesOrZero + eurBookSalesOrZero - africaBookSalesOrZero) * 2
//
//      val x: PartialFunction[Int, Int] = Map.empty[Int, Int]
//
//      val y: Function[Int, Int] = Map.empty[Int, Int]
//
//
//
//
//
//}