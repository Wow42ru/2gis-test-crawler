
import io.circe._
import models.Models.TitleResponse

object JsonSerializer {

  def encodeTitleResponses(titles: List[TitleResponse]): Json =
    Json.obj(
      "results" -> Json.fromValues(
        titles.map { case TitleResponse(url, status, result) =>
          Json.obj(
            "url" -> Json.fromString(url),
            "status" -> Json.fromInt(status),
            "result" -> Json.fromString(result)
          )
        }
      )
    )

}