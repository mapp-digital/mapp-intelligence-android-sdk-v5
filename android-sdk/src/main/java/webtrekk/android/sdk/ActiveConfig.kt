package webtrekk.android.sdk

class ActiveConfig(
    val trackDomains: String,
    val trackIds: List<String>,
    val everId: String?,
    val userAgent: String?,
    val userMatchingId: String?,
    val anonymousParams: Set<String>,
    val logLevel: Logger.Level,
    val requestInterval: Long,
    val requestsPerBatch: Int,
    val exceptionLogLevel: ExceptionType,
    val appFirstOpen: Boolean,
    val isOptOut: Boolean,
    val isAnonymous: Boolean,
    val isFragmentAutoTracking: Boolean,
    val isActivityAutoTracking: Boolean,
    val isAutoTracking: Boolean,
    val isBatchSupport: Boolean,
    val shouldMigrate: Boolean,
    val sendVersionInEachRequest: Boolean,
    val isUserMatchingEnabled: Boolean,
) {
    override fun toString(): String {
        return "ActiveConfig(trackDomains='$trackDomains'," +
                " trackIds=${trackIds.toTypedArray().contentToString()}," +
                " everId=$everId," +
                " userAgent=$userAgent," +
                " userMatchingId=$userMatchingId," +
                " anonymousParams=${anonymousParams.toTypedArray().contentToString()}," +
                " logLevel=${logLevel.name}," +
                " requestInterval=$requestInterval (MINUTES)," +
                " requestsPerBatch=$requestsPerBatch," +
                " exceptionLogLevel=${exceptionLogLevel.name}," +
                " appFirstOpen=$appFirstOpen," +
                " isOptOut=$isOptOut," +
                " isAnonymous=$isAnonymous," +
                " isFragmentAutoTracking=$isFragmentAutoTracking," +
                " isActivityAutoTracking=$isActivityAutoTracking," +
                " isAutoTracking=$isAutoTracking," +
                " isBatchSupport=$isBatchSupport," +
                " shouldMigrate=$shouldMigrate," +
                " sendVersionInEachRequest=$sendVersionInEachRequest," +
                " isUserMatchingEnabled=$isUserMatchingEnabled)"
    }
}