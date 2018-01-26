epfl-people-api-scala
=====================

This project is a simple API in scala for the [people.epfl.ch] website. It enables you to query EPFL profiles
using names, scipers, or email addresses. It might not work all the times, so feel free to report me all bugs
or features you might want to see added.

The API is completely synchronous. The streams are computed value-per-value (meaning the API request will actually
be sent when you will request the value in the stream and not before).

I might eventually make an asynchronous version of this stuff in a short time.

Enjoy :D