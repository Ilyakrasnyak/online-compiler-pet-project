package education_platform.routes

import cats.effect.kernel.Concurrent
import cats.implicits.toFlatMapOps
import education_platform.domain.compiler.domain._
import education_platform.routes.validation.refined.RefinedRequestDecoder
import education_platform.services.CompilerService
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import education_platform.domain.compiler.Compiler.pythonCompiler

final case class CompilerRoutes[F[_]: Concurrent](
    compiler: CompilerService[F]
) extends Http4sDsl[F] {

  private[routes] val prefixPath = "/compilation"

  private val compileRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case ar @ POST -> Root =>
      ar.decodeR[CompileParams] { cp =>
        compiler
          .process(cp.code.value)
          .flatMap { result =>
            Ok(result)
          }
      }
  }

  val routes: HttpRoutes[F] = Router(
    prefixPath -> compileRoute
  )

}
