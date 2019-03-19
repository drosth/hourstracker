package com.personal.hourstracker.config.component

import java.sql.Timestamp
import java.time.LocalDateTime

import com.personal.hourstracker.domain.Registration.{ RegistrationID, Registrations }
import com.personal.hourstracker.domain.{ Registration, SearchParameters }
import com.personal.hourstracker.storage.domain.RegistrationModel
import com.personal.hourstracker.storage.repository.{ RegistrationRepository, RegistrationRepositoryComponent }
import org.squeryl._
import org.squeryl.adapters.H2Adapter

import scala.concurrent.{ ExecutionContext, Future }

trait SquerylRegistrationRepositoryComponent extends RegistrationRepositoryComponent {
  this: SystemComponent =>

  import PrimitiveTypeMode._

  override val registrationRepository = new SquerylRegistrationRepository
  val registrationSchema = RegistrationSchema

  object RegistrationSchema extends Schema {
    implicit def localDateTimeToTimestamp(source: LocalDateTime): Timestamp =
      Timestamp.valueOf(source)

    val registrations: Table[RegistrationModel] = table[RegistrationModel]("REGISTRATION")

    on(registrations)(r => declare(r.job is (indexed), columns(r.job, r.clockedIn, r.clockedOut) are (indexed, unique)))
  }

  class SquerylRegistrationRepository(implicit executionContext: ExecutionContext)
    extends RegistrationRepository {

    import com.personal.hourstracker.storage.repository.RegistrationConverters._
    private val _ = Class.forName("org.h2.Driver")

    SessionFactory.concreteFactory = Some(
      () => Session.create(java.sql.DriverManager.getConnection("jdbc:h2:mem:hourstracker;MODE=MYSQL", "sa", ""), new H2Adapter))

    override def load()(implicit searchParameters: SearchParameters): Future[Registrations] = ???

    override def findById(id: RegistrationID): Future[Option[Registration]] = ???

    override def findAll(): Future[Either[String, Registrations]] =
      Future({
        inTransaction {
          val foundRegistrations: Registrations = from(RegistrationSchema.registrations)(r => select(r))
            .map(_.convert())
            .toList
          Right(foundRegistrations)
        }
      })

    override def save(registration: Registration): Future[Either[String, RegistrationID]] = ???
  }
}
