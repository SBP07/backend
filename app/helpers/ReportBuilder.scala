package helpers

import java.time.LocalDate

import models.{Volunteer, Child}
import org.apache.poi.ss.usermodel._
import helpers.DateTime.exportFormat

object ReportBuilder {

  def addFrontPage(date: LocalDate, workbook: Workbook): Unit = {
    val sheet: Sheet = workbook createSheet "Voorblad"

    sheet.createRow(0).createCell(0).setCellValue(s"Dit rapport werd gemaakt op ${date.format(exportFormat)}.")
    sheet.createRow(1).createCell(0).setCellValue("Dit is een export van alle kinderen en vrijwilligers.")

  }

  def addChildrenSheet(children: Seq[Child], workbook: Workbook): Unit = {
    val sheet: Sheet = workbook createSheet "Kinderen"

    val cols: List[(String, Child => String)] = List(
      "Volgnummer" -> (_.id.fold("")(_.toString)),
      "Voornaam" -> (_.firstName),
      "Achternaam" -> (_.lastName),
      "Geboortedatum" -> (_.birthDate.fold("")(_.format(exportFormat))),
      "GSM" -> (_.mobilePhone.getOrElse("")),
      "Thuistelefoon" -> (_.landline.getOrElse("")),
      "Straat" -> (_.street.getOrElse("")),
      "Stad" -> (_.city.getOrElse(""))
    )

    // Header row

    val headerRow = sheet.createRow(0)

    cols.zipWithIndex.foreach { case (col, idx) =>
      headerRow.createCell(idx).setCellValue(col._1)
    }

    // Add all children

    val rowOffset = 2

    children.zipWithIndex foreach { case (child, idx) =>
      val row = sheet.createRow(idx + rowOffset)
      cols.zipWithIndex.foreach { case (col, idx) =>
        row.createCell(idx).setCellValue(col._2.apply(child).toString)
      }
    }

    cols.indices.foreach(sheet.autoSizeColumn)

  }

  def addVolunteerSheet(volunteers: Seq[Volunteer], workbook: Workbook): Unit = {
    val sheet: Sheet = workbook createSheet "Vrijwilligers"

    val cols: List[Tuple2[String, Volunteer => String]] = List(
      "Volgnummer" -> (_.id.fold("")(_.toString)),
      "Voornaam" -> (_.firstName),
      "Achternaam" -> (_.lastName),
      "Geboortedatum" -> (_.birthDate.fold("")(_.format(exportFormat))),
      "GSM" -> (_.mobilePhone.getOrElse("")),
      "Thuistelefoon" -> (_.landline.getOrElse("")),
      "Emailadres" -> (_.email.getOrElse("")),
      "Straat" -> (_.street.getOrElse("")),
      "Stad" -> (_.city.getOrElse("")),
      "Rekeningnummer" -> (_.bankAccount.getOrElse("")),
      "Gestart in jaar" -> (_.yearStartedVolunteering.fold("")(_.toString))
    )

    // Header row

    val headerRow = sheet.createRow(0)

    cols.zipWithIndex.foreach { case (col, idx) =>
      headerRow.createCell(idx).setCellValue(col._1)
    }

    // Add all volunteers

    val rowOffset = 2

    volunteers.zipWithIndex foreach { case (volunteer, idx) =>
      val row = sheet.createRow(idx + rowOffset)
      cols.zipWithIndex.foreach { case (col, idx) =>
        row.createCell(idx).setCellValue(col._2.apply(volunteer).toString)
      }
    }

    cols.indices.foreach(sheet.autoSizeColumn)

  }
}
