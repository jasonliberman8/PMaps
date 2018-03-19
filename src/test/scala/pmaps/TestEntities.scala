package pmaps

import spire.implicits._


private [pmaps] object TestEntities {

  case class Author(name: String)

  case class Book(title: String, authors: Seq[Author])

  type Rating = Double
  type QuantitySold = Int

  val noelWelsh = Author("Noel Welsh")

  val fpInScala: Book = Book("Functional Programming In Scala", Seq(Author("Paul Chiusano"), Author("Rúnar Bjarnason")))
  val programmingInScala: Book = Book("Programming in Scala", Seq(Author("Martin Odersky"), Author("Lex Spoon"), Author("Bill Venners")))
  val essentialScala: Book = Book("Essential Scala", Seq(Author("Noel Welsh"), Author("Dave Gurnell")))
  val advancedScalaWithCats: Book = Book("AdvancedScalaWithCats", Seq(Author("Noel Welsh"), Author("Dave Gurnell")))
  val GEB: Book = Book("Gödel, Escher, Bach: An Eternal Golden Braid", Seq(Author("Douglas Hofstadter")))

  val usBookSalesPMap: PMap[Book, QuantitySold] = PMap.build(
    fpInScala -> 1000,
    programmingInScala -> 500,
    essentialScala -> 250
  )
  val usBookSales: PMapWDefault[Book, QuantitySold] = usBookSalesPMap.withDefaultZero

  val eurBookSalesPMap: PMap[Book, QuantitySold] = PMap.build(
    fpInScala -> 450,
    programmingInScala -> 750,
    essentialScala -> 300
  )
  val eurBookSales: PMapWDefault[Book, QuantitySold] = eurBookSalesPMap.withDefaultZero

  val asiaBookSalesPMap: PMap[Book, QuantitySold] = PMap.build(
    fpInScala -> 500,
    programmingInScala -> 500,
    essentialScala -> 500
  )
  val asiaBookSales: PMapWDefault[Book, QuantitySold] = asiaBookSalesPMap.withDefaultZero

  val africaBookSalesPMap: PMap[Book, QuantitySold] = PMap.build(
    fpInScala -> 150,
    programmingInScala -> 650,
    essentialScala -> 1000
  )
  val africaBookSales: PMapWDefault[Book, QuantitySold] = africaBookSalesPMap.withDefaultZero
}
