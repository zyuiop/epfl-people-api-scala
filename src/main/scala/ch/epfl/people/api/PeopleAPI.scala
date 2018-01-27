package ch.epfl.people.api

import java.net.URLEncoder

import play.api.libs.json._

import scala.io.Source
/**
  * This is a Scala API for the people.epfl.ch website.<br/>
  * The API is completely synchronous. Streams are computed on request (i.e. not all values are requested at once)
  * @author Louis Vialar
  */
object PeopleAPI {
  case class Person(email: String, firstname: String, name: String, profile: String, sciper: Int, complete: JsObject) {
    val fullName: String = firstname + " " + name
  }

  object Person {
    implicit var formatter: Reads[Person] = new PersonSerializer
  }

  trait BackendAPI {
    def search(q: String, locale: String = "en"): String
  }

  object BackendAPI {
    implicit val defaultApi: BackendAPI = new BackendAPIImpl
  }

  private class BackendAPIImpl extends BackendAPI {
    private val API_URL: String = "https://search.epfl.ch/json/ws_search.action"

    private def buildSearchUrl(q: String, locale: String = "en"): String =
      API_URL + "?q=" + URLEncoder.encode(q, "UTF-8") + "&request_locale=" + locale

    override def search(q: String, locale: String): String =
      Source.fromURL(buildSearchUrl(q, locale)).mkString
  }

  private class PersonSerializer extends Reads[Person] {
    override def reads(json: JsValue): JsResult[Person] = json match {
      case o: JsObject =>
        val map = o.value
        try {
          JsSuccess(Person(map("email").as[String], map("firstname").as[String], map("name").as[String],
            map("profile").as[String], map("sciper").as[String].toInt, o))
        } catch {
          case JsResultException(msg) => JsError(msg)
        }
      case _ => JsError()
    }
  }


  /**
    * Returns a raw [[JsValue]] (most of the times it will be a [[JsArray]]) of the profiles matching the given query.<br/>
    * The query itself will be URLEncoded before being sent.
    * @param q the query
    * @param locale the locale (by default, english)
    * @param api the backend API to use (by default, the people.epfl.ch website)
    * @return the json result returned by the backend for the given query in the given locale
    */
  def search(q: String, locale: String = "en")(implicit api: BackendAPI): JsValue = Json.parse(api.search(q, locale))

  /**
    * Returns a [[Stream]] of [[Person]] of the profiles matching each of the queries in the input stream.<br>
    * The queries will be URLEncoded before being sent.
    * @param queries the queries to send (names, scipers, ...)
    * @param api the backend API to use (by default, the people.epfl.ch website)
    * @return a stream of persons, corresponding to the people found on the backend (a single query might return
    *         multiple persons at once, or none, depending on the precision)
    */
  def getPeople(queries: Stream[String])(implicit api: BackendAPI): Stream[Person] =
    queries.flatMap(e => search(e).as[List[Person]].toStream)

  /**
    * Returns a [[List]] of [[Person]] of the profiles matching a given query<br>
    * The query will be URLEncoded before being sent.
    * @param query the query to send (names, scipers, ...)
    * @param api the backend API to use (by default, the people.epfl.ch website)
    * @return a list of persons, corresponding to the people found on the backend (a single query might return
    *         multiple persons at once, or none, depending on the precision)
    */
  def getPeople(query: String)(implicit api: BackendAPI): List[Person] =
    getPeople(Stream(query)).toList

  /**
    * Returns a [[Stream]] of [[Person]] of the profiles matching each of the scipers in the input stream
    * @param scipers the scipers to look for
    * @param api the backend API to use (by default, the people.epfl.ch website)
    * @return a stream of persons, corresponding to the people found on the backend (a single sciper should usually
    *         return only zero or one person)
    */
  def getPeopleBySciper(scipers: Stream[Int])(implicit api: BackendAPI): Stream[Person] =
    getPeople(scipers map (_.toString))

  /**
    * Returns an [[Option]] of [[Person]] of the profiles matching the given sciper
    * @param sciper the sciper to look for
    * @param api the backend API to use (by default, the people.epfl.ch website)
    * @return a person if a person existed on the backend for this sciper, or nothing
    */
  def getPeopleBySciper(sciper: Int)(implicit api: BackendAPI): Option[Person] =
    getPeopleBySciper(Stream(sciper)) match {
      case head #:: _ => Option.apply(head)
      case _ => Option.empty
    }
}
