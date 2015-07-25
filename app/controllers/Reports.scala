package controllers

import java.io._
import javax.inject.Inject

import io.github.cloudify.scala.spdf._
import models.dao.AnimatorDao
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class Reports @Inject()(animatorDao: AnimatorDao) extends Controller {

  val pdfLandscape = Pdf(new PdfConfig {
    orientation := Landscape
    pageSize := "A4"
    marginTop := "1in"
    marginBottom := "1in"
    marginLeft := "1in"
    marginRight := "1in"
  })

  val pdfPortrait = Pdf(new PdfConfig {
    orientation := Portrait
    pageSize := "A4"
    marginTop := "1in"
    marginBottom := "1in"
    marginLeft := "1in"
    marginRight := "1in"
  })


  def volunteersContact = Action.async { implicit req =>
    animatorDao.findAll
      .map(helpers.reports.VolunteersContactReport.generate)
      .flatMap { report =>
        val tempFile: File = File.createTempFile("volunteersContact", ".pdf.tmp")
        val outputStream = new FileOutputStream(tempFile)
        Future(pdfLandscape.run(report, outputStream))
      .map(res => {
        Ok.sendFile(tempFile, fileName = _ => "volunteersContact.pdf", inline = true)
      })
    }
  }

  def allVolunteerDetails = Action.async { implicit req =>
    animatorDao.findAll
      .map(helpers.reports.VolunteersDetailReport.generate)
      .flatMap { report =>
        val tempFile: File = File.createTempFile("volunteers", ".pdf.tmp")
        val outputStream = new FileOutputStream(tempFile)
        Future(pdfPortrait.run(report, outputStream))
      .map(res => {
        Ok.sendFile(tempFile, fileName = _ => "volunteers.pdf", inline = true)
      })
    }
  }
}
