package helpers

import java.io.FileInputStream

import helpers.DateTime.exportFormat
import models.Child
import org.apache.poi.hwpf.HWPFDocument
import org.apache.poi.hwpf.extractor.WordExtractor

object FiscalCertificateBuilder {
  case class FiscalCertificateInformation(
                                           fiscalCertificateResponsible: FiscalCertificateResponsible,
                                           certificateId: Int,
                                           child: Child,
                                           periodChildReceived: String,
                                           attendances: FiscalCertificateAttendances
                                           )
  {
    def totalReceived: Int = attendances.numberOfVm + attendances.numberOfMid + attendances.numberOfNm * 2
  }

  case class FiscalCertificateAttendances(numberOfVm: Int, numberOfMid: Int, numberOfNm: Int)
  case class FiscalCertificateResponsible(fullname: String, street: String, city: String)

  def build(fiscalCertificateInformation: FiscalCertificateInformation): HWPFDocument = {
    val inputStream = new FileInputStream("originalCertificate.doc")
    val document = new HWPFDocument(inputStream)

    val range = document.getRange

    val replacements: List[(String, String)] = List(
      "{{ verantwoordelijkeVolledigeNaam }}" -> fiscalCertificateInformation.fiscalCertificateResponsible.fullname,
      "{{ verantwoordelijkeStraatEnNummer }}" -> fiscalCertificateInformation.fiscalCertificateResponsible.street,
      "{{ verantwoordelijkeStad }}" -> fiscalCertificateInformation.fiscalCertificateResponsible.city,
      "{{ attestVolgNummer }}" -> fiscalCertificateInformation.certificateId.toString,
      "{{ child.lastName }}" -> fiscalCertificateInformation.child.firstName,
      "{{ child.firstName }}" -> fiscalCertificateInformation.child.lastName,
      "{{ child.birthDate }}" -> fiscalCertificateInformation.child.birthDate.fold("Ongekend")(date => date.format(exportFormat)),
      "{{ periodeOpgevangen }}" -> fiscalCertificateInformation.periodChildReceived,
      "{{ numAttendances.vm }}" -> fiscalCertificateInformation.attendances.numberOfVm.toString,
      "{{ numAttendances.mid }}" -> fiscalCertificateInformation.attendances.numberOfMid.toString,
      "{{ numAttendances.nm }}" -> fiscalCertificateInformation.attendances.numberOfNm.toString,
      "{{ totalReceived }}" -> fiscalCertificateInformation.totalReceived.toString,


      "Jonckheere" -> fiscalCertificateInformation.child.firstName,
      "Louise" -> fiscalCertificateInformation.child.lastName

    )

    replacements.foreach { case (replaceThis, replaceBy) => range.replaceText(replaceThis, replaceBy) }

    document
  }


}
