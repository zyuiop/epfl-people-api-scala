package ch.epfl.people.api

import ch.epfl.people.api.PeopleAPI.Person
import org.scalatest._
import play.api.libs.json._
/**
  * @author Louis Vialar
  */
class PeopleAPITest extends FlatSpec with Matchers  {
  val persons: List[Person] = List(
    Person("alice@epfl.ch", "Alice", "Ecila", "alice.ecila", 123456, JsObject(Seq(
      "email" -> JsString("alice@epfl.ch"),
      "firstname" -> JsString("Alice"),
      "profile" -> JsString("alice.ecila"),
      "name" -> JsString("Ecila"),
      "sciper" -> JsNumber(123456)))),
    Person("bob@epfl.ch", "Bob", "Fanta", "bob.fanta", 234567, JsObject(Seq(
      "email" -> JsString("bob@epfl.ch"),
      "firstname" -> JsString("Bob"),
      "profile" -> JsString("bob.fanta"),
      "name" -> JsString("Fanta"),
      "sciper" -> JsNumber(234567)))),
    Person("cecile@epfl.ch", "Cecile", "Elicec", "cecile.elicec", 345678, JsObject(Seq(
      "email" -> JsString("cecile@epfl.ch"),
      "firstname" -> JsString("Cecile"),
      "profile" -> JsString("cecile.elicec"),
      "name" -> JsString("Elicec"),
      "sciper" -> JsNumber(345678)))),

  )

  val jsPersons: List[JsObject] = persons.map(_.complete)

  implicit val backendAPI: BackendAPI = (q: String, locale: String) => {
    println("Searching with q=" + q)
    val arr = JsArray(jsPersons.filter(map => map.values.count{
      case JsNumber(num) => num.toString == q
      case JsString(str) => str == q
      case _ => false
    } > 0)).toString

    println("Returning " + arr)
    arr
  }

  // Just in case, we want to test our mock backend - because if it doesn't work we cannot test
  "The test backend API" should "work, at first" in {
    val list = Json.parse(backendAPI.search("123456")).as[List[Person]]

    list should not be empty
    list.head should be (persons.head)
  }

  "A valid json" should "return a valid person" in {
    val pers = PeopleAPI.getPeopleBySciper(123456)

    pers should not be empty
    pers.head should be (persons.head)

    val byPers = PeopleAPI.getPeople(pers.head.name)
    byPers should not be empty
    byPers.head should be (pers.head)
  }

  "Streams" should "work for scipers" in {
    val list = PeopleAPI.getPeopleBySciper(persons.map(_.sciper).toStream).toList

    list should be (persons)
  }

  "Streams" should "work for names" in {
      val list = PeopleAPI.getPeople(persons.map(_.name).toStream).toList

      list should be (persons)
    }
}
