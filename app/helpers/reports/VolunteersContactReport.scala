package helpers.reports

import models.Animator

import scala.xml.Elem

object VolunteersContactReport {
  def generate(volunteers: Seq[Animator]): Elem = {
    <html>
      <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
      </head>
      <body>
        <h1>Contact vrijwilligers</h1>
        <table>
          <tr>
            <th>Vrijwilliger</th>
            <th>Telefoonnummer</th>
            <th>Thuistelefoon</th>
          </tr>
          {
          for(volunteer <- volunteers.sortBy(_.firstName)) yield {
            <tr>
              <td>{volunteer.firstName + " " + volunteer.lastName}</td>
              <td>{volunteer.mobilePhone.filterNot(_ == "").fold(<em>Geen</em>)(tel => <span>{tel}</span>)}</td>
              <td>{volunteer.landline.filterNot(_ == "").fold(<em>Geen</em>)(tel => <span>{tel}</span>)}</td>
              <td>{volunteer.email.filterNot(_ == "").fold(<em>Geen</em>)(email => <a href="mailto:${email}">{email}</a>)}</td>
            </tr>
          }
          }
        </table>

        {ReportHelper.generatedTimestamp}

      </body>
    </html>
  }
}
