package dao

class DatabaseException(msg: String) extends RuntimeException(msg)
class NonExistantChildOrContactPersonOrDontBelongToTenant(tenantCanonicalName: String)
  extends DatabaseException(s"Non-existant child or contact person, or child/contact person does not belong to $tenantCanonicalName")
