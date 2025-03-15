package database.dao.audit

class AuditDataBag(
    val documentsGeneratedDao: DocumentsGeneratedDao,
    val proofOfAddressDao: ProofOfAddressDao,
    val userDao: UserDao,
    val roleDao: RoleDao,
    val permissionDao: PermissionDao
)