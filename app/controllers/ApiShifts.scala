package controllers

import models.json.ShiftJson
import models.dao.ShiftDao
import javax.inject.Inject
import models.Shift

class ApiShifts @Inject() (shiftDao: ShiftDao)
  extends GenericApiController[Shift]
  with GenericApiControllerRetrieveSlice[Shift]
{
  override val dao = shiftDao
  override val jsonWrites = ShiftJson.shiftWrites

}
