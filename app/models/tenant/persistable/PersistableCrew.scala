package models.tenant.persistable

import java.time.LocalDate
import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import models.helpers.BelongsToTenant
import models.tenant.{Crew, Address}

case class PersistableCrew(
                            id: Option[UUID],
                            //loginInfo: LoginInfo,
                            firstName: Option[String],
                            lastName: Option[String],
                            fullName: Option[String],
                            email: Option[String],
                            avatarURL: Option[String],
                            birthDate: Option[LocalDate],
                            street: Option[String],
                            zipCode: Option[Int],
                            city: Option[String],
                            country: Option[String],
                            tenantId: UUID
                          ) extends BelongsToTenant {
  def convert: Crew = {
    val address = for {street <- street; zipCode <- zipCode; city <- city; country <- country }
      yield {
        Address(street, zipCode, city, country)
      }
    Crew(id, /*loginInfo*/ null, firstName, lastName, fullName, email, avatarURL, birthDate, address, tenantId)
  }
}

object PersistableCrew extends ((Option[UUID]/*, LoginInfo*/, Option[String], Option[String], Option[String], Option[String],
  Option[String], Option[LocalDate], Option[String], Option[Int], Option[String], Option[String], UUID)
  => PersistableCrew)
{
  def build(crew: Crew): PersistableCrew = {
    PersistableCrew(
      crew.id,
      //crew.loginInfo,
      crew.firstName,
      crew.lastName,
      crew.fullName,
      crew.email,
      crew.avatarURL,
      crew.birthDate,
      crew.address.map(_.street),
      crew.address.map(_.zipCode),
      crew.address.map(_.city),
      crew.address.map(_.country),
      crew.tenantId
    )
  }
}
