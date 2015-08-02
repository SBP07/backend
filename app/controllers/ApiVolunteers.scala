package controllers

import javax.inject._

import models.Volunteer
import models.dao.VolunteerDao
import models.json.VolunteerJson._


class ApiVolunteers @Inject()(volunteerDao: VolunteerDao)
  extends GenericApiControllerFullCrud[Volunteer]
{
  override val dao = volunteerDao
  override val jsonWrites = volunteerWrites
  override val jsonReads = volunteerReads
}
