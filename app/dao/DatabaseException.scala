package dao

class DatabaseException(msg: String) extends RuntimeException(msg)
class NonExistantChildOrContactPersonOrDontBelongToTenantException(tenantCanonicalName: String)
  extends DatabaseException(s"Non-existant child or contact person, or child/contact person does not belong to $tenantCanonicalName")

class RowAlreadyExistsException(tenantCanonicalName: String)
  extends DatabaseException("This row already exists in the database")
