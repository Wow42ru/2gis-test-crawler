package models

object Models {
  type Url = String

  case class TitleResponse(url: Url, status: Int, result: String)
}