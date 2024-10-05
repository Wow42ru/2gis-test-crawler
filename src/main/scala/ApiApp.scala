
import cats.effect._
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.{Logger => SLogger}
import org.typelevel.log4cats.Logger

trait ApiApp {
  def httpApp: HttpApp[IO]
}

object ApiApp {
  def apply()(implicit logger: Logger[IO]): ApiApp = new ApiApp {
    override def httpApp: HttpApp[IO] = SLogger.httpApp(logHeaders = true, logBody = true)(routes)

    private val client = EmberClientBuilder.default[IO].withLogger(logger).build

    private val routes = HttpRoutes.of[IO] {
      case req@POST -> Root / "titles" =>
        client.use { c =>
          req.decode[List[String]] { urls =>
            Crawler(c).crawl(urls).flatMap { titles =>
              Ok(JsonSerializer.encodeTitleResponses(titles))
            }
          }
        }
    }.orNotFound
  }
}