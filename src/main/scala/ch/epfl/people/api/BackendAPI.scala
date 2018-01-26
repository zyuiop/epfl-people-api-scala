package ch.epfl.people.api

/**
  * An interface for the EPFL backend
  * @author Louis Vialar
  */
trait BackendAPI {
  def search(q: String, locale: String = "en"): String
}
