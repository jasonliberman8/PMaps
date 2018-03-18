package pmaps

import org.scalatest.{FlatSpec, Matchers}
import spire.implicits._
import scala.util.Try
import TestEntities._



class PMapTests extends FlatSpec with Matchers{

  "Equals " should "work as expected on the innerMap and default (if it exists)" in {

    val usBookSalesCopy = PMapWDefault(usBookSales.toMap, usBookSales.default)
    val scaledUsBookSales = usBookSales.mapValues(_ * 3)

    usBookSalesCopy.equals(usBookSales) shouldBe true
    usBookSalesCopy.dropDefault.equals(usBookSales.dropDefault) shouldBe true

    usBookSales.equals(scaledUsBookSales) shouldBe false
    usBookSales.dropDefault.equals(scaledUsBookSales.dropDefault) shouldBe false

  }

  "Size " should "work as expected on the size of the inner Map" in {

    usBookSales.dropDefault.size shouldBe 3

  }

  "Adding and removing key-value pairs " should "work as expected for a PMap" in {

    val missingValue = GEB -> 100
    val existingValue = fpInScala -> 1000

    (usBookSales + (GEB -> 100)).get(GEB) shouldBe 100
    (usBookSales + (fpInScala -> 1000)).get(fpInScala) shouldBe 2000
    (usBookSales - (fpInScala -> 1000)).get(fpInScala) shouldBe 0

    (usBookSales + (GEB -> 100)).toMap shouldBe usBookSales.toMap + (GEB -> 100)

    usBookSales.dropDefault.remove(fpInScala).toMap.get(fpInScala) shouldBe None

    Try(usBookSales.dropDefault.append(fpInScala -> 50)).isFailure shouldBe true


  }

  "Get methods" should "work as expected for a PMap and PMapWDefault" in {

    usBookSales(fpInScala) shouldBe 1000
    usBookSales(programmingInScala) shouldBe 500
    usBookSales(GEB) shouldBe 0

    val usBookSalesPMap = usBookSales.dropDefault

    usBookSalesPMap.get(fpInScala) shouldBe Some(1000)
    usBookSalesPMap.get(programmingInScala) shouldBe Some(500)
    usBookSalesPMap.get(GEB) shouldBe None

    usBookSalesPMap(fpInScala) shouldBe 1000
    usBookSalesPMap(programmingInScala) shouldBe 500
    Try(usBookSalesPMap(GEB)).isFailure shouldBe true

    usBookSalesPMap.getOrElse(fpInScala, 100) shouldBe 1000
    usBookSalesPMap.getOrElse(GEB, 100) shouldBe 100

  }

  "Map and flatMap methods" should "work as if on underlying Map and defaults" in {

    val bookSales: PMap[Book, QuantitySold] = usBookSales.dropDefault + (advancedScalaWithCats -> 300)
    val bookSalesTimes2 = bookSales.mapValues(_ * 2)

    val salesByAuthor: PMap[Author, QuantitySold] = bookSales.flatMapSum{case (book, quantity) =>
      book.authors.map(author => author -> quantity)
    }

    val tryFlatMap: Try[PMap[Author, QuantitySold]] = Try(bookSalesTimes2.flatMap{case (book, quantity) =>
      book.authors.map(author => author -> quantity)
    })

    bookSalesTimes2(fpInScala) shouldBe 2000
    bookSalesTimes2.getOrElse(GEB, 0) shouldBe 0
    bookSalesTimes2.toMap shouldBe bookSales.toMap.mapValues(_ * 2)

    salesByAuthor.getOrElse(noelWelsh, 0) shouldBe 550
    tryFlatMap.isFailure shouldBe true


  }

  "Adding PMaps" should "add the values correctly" in {
    val combinedBookSales = usBookSales + eurBookSales + asiaBookSales + africaBookSales

    combinedBookSales(fpInScala) shouldBe 2100
    combinedBookSales(programmingInScala) shouldBe 2400
    combinedBookSales(essentialScala) shouldBe 2050
    combinedBookSales(GEB) shouldBe 0

  }

  "Algebra on PMaps " should "act on underlying values" in {

    val algebra = (2 * usBookSales + eurBookSales) * -1

    algebra(fpInScala) shouldBe (2 * usBookSales(fpInScala) + eurBookSales(fpInScala)) * -1
    algebra(essentialScala) shouldBe (2 * usBookSales(essentialScala) + eurBookSales(essentialScala)) * -1
    algebra(programmingInScala) shouldBe (2 * usBookSales(programmingInScala) + eurBookSales(programmingInScala)) * -1
    algebra(GEB) shouldBe 0


  }

  "Joins on PMaps" should "work as expected" in {

    val leftJoinedPMap: PMap[Book, (QuantitySold, QuantitySold)] = usBookSalesPMap leftJoin eurBookSalesPMap

    leftJoinedPMap(fpInScala) shouldBe (1000, 450)
    leftJoinedPMap(programmingInScala) shouldBe (500, 750)
    leftJoinedPMap.get(GEB) shouldBe None
    leftJoinedPMap.size shouldBe 3


    val outerJoinedPMap: PMap[Book, (QuantitySold, QuantitySold)] = usBookSalesPMap outerJoin eurBookSalesPMap

    outerJoinedPMap(fpInScala) shouldBe (1000, 450)
    outerJoinedPMap(programmingInScala) shouldBe (500, 750)
    outerJoinedPMap.get(GEB) shouldBe None
    outerJoinedPMap.size shouldBe 3

    val outerJoinWithMissingKeys: Try[PMap[Book, (QuantitySold, QuantitySold)]] =
      Try(usBookSalesPMap outerJoin (eurBookSalesPMap remove fpInScala))

    outerJoinWithMissingKeys.isFailure shouldBe true

    val innerJoinPMap: PMap[Book, (QuantitySold, QuantitySold)] =
      usBookSalesPMap innerJoin (eurBookSalesPMap remove fpInScala)

    innerJoinPMap.get(fpInScala) shouldBe None
    innerJoinPMap.size shouldBe 2
    innerJoinPMap(essentialScala) shouldBe (250, 300)
    innerJoinPMap(programmingInScala) shouldBe (500, 750)

  }

}
