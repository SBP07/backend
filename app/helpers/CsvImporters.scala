package helpers

import java.io.File
import java.time.LocalDate
import java.time.format.{DateTimeFormatter, DateTimeParseException}

import com.github.tototoshi.csv._
import models._
import models.repository._
import play.api.db.slick.Config.driver.simple.Session

object CsvImporters {
  val fmt: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

  def animators(filename: String): List[Animator] = {
    val reader = CSVReader.open(new File(filename))
    val all: List[Map[String, String]] = reader.allWithHeaders()
    all.map { map =>
      val id = None
      val firstName = map.getOrElse("voornaam", "Voornaam")
      val lastName = map.getOrElse("achternaam", "Naam")
      val street = map.get("straat")
      val city = map.get("gemeente")
      val mobilePhone = map.get("gsm")
      val landline = map.get("telefoon")
      val birthDate = {
        try {
          map.get("geboortedatum").map(LocalDate.parse(_, fmt))
        } catch {
          case e: DateTimeParseException => None
        }
      }
      val email = map.get("email")
      val bankAccount = map.get("rekeningnummer")
      val yearStarted = None
      val isPartOfCore = false

      Animator(id, firstName, lastName, mobilePhone, landline, email, street, city, bankAccount, yearStarted,
        isPartOfCore, birthDate)
    }
  }

  def children(filename: String): List[Child] = {
    // expected structure:
    // voornaam,naam,geboortedatum,straat,gemeente

    val reader = CSVReader.open(new File(filename))

    val all: List[Map[String, String]] = reader.allWithHeaders()

    all.map { map =>
      val id = None
      val firstName = map.getOrElse("voornaam", "Voornaam")
      val lastName = map.getOrElse("naam", "Achternaam")
      val mobilePhone = None
      val landline = None
      val street = map.get("straat")
      val city = map.get("gemeente")
      val birthDate = {
        try {
          map.get("geboortedatum").map(LocalDate.parse(_, fmt))
        } catch {
          case e: DateTimeParseException => None
        }
      }

      Child(id, firstName, lastName, mobilePhone, landline, street, city, birthDate)
    }
  }

  def shifts(filename: String)(implicit s: Session): List[Shift] = {
    val reader = CSVReader.open(new File(filename))
    val all: List[Map[String, String]] = reader.allWithHeaders()
    all.map { map =>
      for {
        date <- {
          try {
            map.get("dag").map(LocalDate.parse(_, fmt))
          } catch {
            case e: DateTimeParseException => None
          }
        }

        shiftMnemonic <- map.get("type")

        shiftType <- ShiftTypeRepository.findByMnemonic(shiftMnemonic)
        shiftId <- shiftType.id
      } yield {
        Shift(None, date, "Speelplein", shiftId)
      }
    }.flatten

  }

  def childPresences(filename: String, childRepo: ChildRepository,
                     shiftRepository: ShiftRepository)(implicit s: Session): List[ChildPresence] = {

    val reader = CSVReader.open(new File(filename))
    val all: List[Map[String, String]] = reader.allWithHeaders()
    all.map { map =>
      for {
        date <- {
          try {
            map.get("dag").map(LocalDate.parse(_, fmt))
          } catch {
            case e: DateTimeParseException => None
          }
        }

        shiftMnemonic <- map.get("type")
        firstName <- map.get("voornaam")
        lastName <- map.get("achternaam")

        child <- childRepo.findByFirstAndLastname(firstName, lastName)
        childId <- child.id

        shiftType <- ShiftTypeRepository.findByMnemonic(shiftMnemonic)
        shift <- shiftRepository.findByDateAndType(date, shiftType)
        shiftId <- shift.id

      } yield {
        ChildPresence(childId, shiftId)
      }
    }.flatten
  }
}
