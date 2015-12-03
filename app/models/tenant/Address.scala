package models.tenant

import java.util.UUID

import models.helpers.BelongsToTenant

case class Address(
                    street: String,
                    zipCode: Int,
                    city: String,
                    country: String
                  )
