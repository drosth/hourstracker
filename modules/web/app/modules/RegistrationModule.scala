package modules

class RegistrationModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[HttpExt]).toProvider(classOf[HttpExtProvider])
  }
}


