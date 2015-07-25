package helpers.reports

import models.Animator

import scala.xml.Elem

object VolunteersDetailReport {
  def generate(volunteers: Seq[Animator]): Elem = {
    <html>
      <head>
        <style>
          {"""div.volunteer { page-break-after:always; }"""}
        </style>
        <link rel="stylesheet" href="http://127.0.0.1:9000/assets/webjars/angular-material/0.10.0/angular-material.css"></link>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
      </head>
      <body>
      {
        for(volunteer <- volunteers.sortBy(_.firstName)) yield {
          <div class="volunteer">
            <h1>{volunteer.firstName + " " + volunteer.lastName}</h1>
            <td>{volunteer.mobilePhone.filterNot(_ == "").fold(<em>Geen</em>)(tel => <span>{tel}</span>)}</td>
            <td>{volunteer.landline.filterNot(_ == "").fold(<em>Geen</em>)(tel => <span>{tel}</span>)}</td>
            <td>{volunteer.email.filterNot(_ == "").fold(<em>Geen</em>)(email => <a href="mailto:${email}">{email}</a>)}</td>
          </div>
        }
      }

        {ReportHelper.generatedTimestamp}

      </body>
    </html>
  }
}
