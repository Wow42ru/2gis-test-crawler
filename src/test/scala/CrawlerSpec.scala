import cats.effect._
import cats.effect.unsafe.implicits.global
import org.http4s._
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.dsl.io._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

class CrawlerSpec extends AnyFlatSpec with Matchers with Http4sClientDsl[IO] {
  implicit val logger: Logger[IO] = Slf4jLogger.getLogger[IO]

  val testUrl = "https://example.com"
  val testUrl2 = "https://example2.com"

  "Crawler" should "successfully crawl a website and extract the title" in {
    val client = Client.fromHttpApp(
      HttpApp[IO] {
        case GET -> Root =>
          Ok(
            s"""
               <html>
                 <head>
                   <title>Example Website</title>
                 </head>
                 <body>
                   <h1>Example Website</h1>
                 </body>
               </html>
             """.stripMargin)
      }
    )

    val crawler = Crawler(client)

    val result = crawler.crawl(List(testUrl)).unsafeRunSync()

    result.size should be(1)
    result.head.url shouldBe testUrl
    result.head.status shouldBe 200
    result.head.result shouldBe "Example Website"
  }
  "Crawler" should "successfully crawl several website and extract the title" in {
    val client = Client.fromHttpApp(
      HttpApp[IO] {
        case GET -> Root =>
          Ok(
            s"""
               <html>
                 <head>
                   <title>Example Website</title>
                 </head>
                 <body>
                   <h1>Example Website</h1>
                 </body>
               </html>
             """.stripMargin)
      }
    )

    val crawler = Crawler(client)

    val result = crawler.crawl(List(testUrl, testUrl2)).unsafeRunSync()

    result.size should be(2)
    result.head.url shouldBe testUrl
    result.tail.head.url shouldBe testUrl2
    result.forall(_.status == 200) shouldBe true
    result.forall(_.result == "Example Website") shouldBe true

  }

  it should "return an error message if the website is not found" in {
    val client = Client.fromHttpApp(
      HttpApp[IO] {
        case GET -> Root =>
          NotFound()
      }
    )

    val crawler = Crawler(client)

    val result = crawler.crawl(List(testUrl)).unsafeRunSync()

    result.size should be(1)
    result.head.url shouldBe testUrl
    result.head.status shouldBe 404
    result.head.result.isEmpty shouldBe true
  }

  it should "return an error message if there is an error during crawling" in {
    val client = Client.fromHttpApp(
      HttpApp[IO] {
        case _ =>
          throw new Exception("Error during crawling")
      }
    )

    val crawler = Crawler(client)

    val result = crawler.crawl(List(testUrl)).unsafeRunSync()

    result.size should be(1)
    result.head.url shouldBe testUrl
    result.head.status shouldBe 500
    result.head.result shouldBe "Error occurred: Error during crawling"
  }

  it should "handle invalid HTML" in {
    val client = Client.fromHttpApp(
      HttpApp[IO] {
        case GET -> Root =>
          Ok("<html><body><h1>Invalid HTML</h1></body></html>")
      }
    )

    val crawler = Crawler(client)

    val result = crawler.crawl(List(testUrl)).unsafeRunSync()

    result.size should be(1)
    result.head.url shouldBe testUrl
    result.head.status shouldBe 204
    result.head.result shouldBe "No Content"
  }
}