
import cats.effect.IO
import cats.implicits._
import org.http4s._
import org.http4s.client.Client
import org.jsoup.Jsoup

import scala.util.Try
import models.Models._
import org.typelevel.log4cats.Logger

trait Crawler {
  def crawl(urls: List[Url]): IO[List[TitleResponse]]
}

object Crawler {
  def apply(client: Client[IO])(implicit logger: Logger[IO]): Crawler = new Crawler {
    override def crawl(urls: List[Url]): IO[List[TitleResponse]] = urls.parTraverse(getWebsiteTitle) //при перегрузке parTraverseN?

    private def getWebsiteTitle(url: Url): IO[TitleResponse] = {
      val response = for {
        _ <- logger.debug(s"crawling url с $url")
        req = Request[IO](Method.GET, Uri.unsafeFromString(url))
        _ <- logger.debug(s"Request: $req")
        resp <- client.run(req).use {
          processResponse(_, url)
        }
        _ <- logger.debug(s"Response: $resp")
      } yield resp

      response.recoverWith {
        case e: Throwable =>
          logger.warn(s"Error during crawling: ${e.toString}") *>
            IO.pure(TitleResponse(url, 500, s"Error occurred: ${e.getMessage}"))
      }
    }
  }

  private def processResponse(response: Response[IO], url: Url): IO[TitleResponse] =
    response.bodyText.compile.string.map { body =>
      Try(Jsoup.parse(body).title())
        .toEither
        .fold(x => TitleResponse(url, 500, x.getMessage), {
          case title if title.isEmpty && response.status.code == 200 =>
            TitleResponse(url, 204, "No Content")
          case title =>
            TitleResponse(url, response.status.code, title)
        })
    }
}


