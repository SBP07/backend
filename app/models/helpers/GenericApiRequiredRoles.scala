package models.helpers

import models.Role

case class GenericApiRequiredRoles(requiredToGet: Role, requiredToCreate: Role, requiredToUpdate: Role, requiredToDelete: Role)
