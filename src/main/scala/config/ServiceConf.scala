package config

import cats.effect.Sync
import com.comcast.ip4s.Port
import pureconfig._
import pureconfig.error.UserValidationFailed
import pureconfig.generic.semiauto.deriveReader
object ServiceConf {

  case class AppConfig(port: Port)

  implicit val portReader: ConfigReader[Port] = ConfigReader.fromString[Port](str =>
    Port.fromString(str).toRight(UserValidationFailed(s"Invalid port: $str"))
  )

  implicit val appConfigReader: ConfigReader[AppConfig] = deriveReader[AppConfig]

  def load[F[_] : Sync]: F[AppConfig] =
    ConfigSource.default.load[AppConfig].fold(
      err => throw new RuntimeException(s"Failed to load config: $err"),
      config => Sync[F].delay(config)
    )
}