
import cats.effect._
import com.comcast.ip4s.IpLiteralSyntax
import config.ServiceConf
import org.http4s.ember.server.EmberServerBuilder
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Main extends IOApp {

  implicit val logger: Logger[IO] = Slf4jLogger.getLogger[IO]
  override def run(args: List[String]): IO[ExitCode] = {
    for {
      config <- ServiceConf.load[IO]
      _ <- logger.info("Server starting")
      _ <- logger.info(s"port: ${config.port} ")
      _ <- EmberServerBuilder
        .default[IO]
        .withHost(ipv4"0.0.0.0")
        .withPort(config.port)
        .withHttpApp(ApiApp().httpApp)
        .build
        .use(_ => IO.never)
    } yield ExitCode.Success
  }

}