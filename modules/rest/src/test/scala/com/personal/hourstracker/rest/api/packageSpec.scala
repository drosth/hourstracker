package com.personal.hourstracker.rest.api

class packageSpec extends AnyFlatSpec with BeforeAndAfter with should.Matchers with MockitoSugar {

  behavior of "Version"

  it should "return correct version" in {
    val m: Map[Version, String] = Map[Version, String](
      Version(0, 0, 0) -> "0",
      Version(0, 0, 1) -> "0.0.1",
      Version(0, 1, 0) -> "0.1",
      Version(0, 1, 1) -> "0.1.1",
      Version(1, 0, 0) -> "1",
      Version(1, 0, 1) -> "1.0.1",
      Version(1, 1, 0) -> "1.1",
      Version(1, 1, 1) -> "1.1.1"
    )
    m.foreach {
      case (version, result) => version.toString shouldEqual result
    }
  }
}
