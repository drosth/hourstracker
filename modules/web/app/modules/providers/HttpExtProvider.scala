package modules.providers

import scala.concurrent.ExecutionContext

class HttpExtProvider @Inject()(
                                 applicationLifecycle: ApplicationLifecycle
                               )(implicit actorSystem: ActorSystem, val executionContext: ExecutionContext, val mat: Materializer)
  extends Provider[HttpExt] {

  override lazy val get: HttpExt = {
    val innerHttp = Http()
    applicationLifecycle.addStopHook(() => innerHttp.shutdownAllConnectionPools())
    innerHttp
  }
}
