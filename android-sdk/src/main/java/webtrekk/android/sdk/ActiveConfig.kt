package webtrekk.android.sdk

import webtrekk.android.sdk.data.model.GenerationMode

class ActiveConfig(
    val trackDomains: String,
    val trackIds: List<String> = emptyList(),
    val everId: String?,
    val everIdMode: GenerationMode?,
    val userAgent: String?,
    val userMatchingId: String?,
    val anonymousParams: Set<String> = emptySet(),
    val logLevel: Logger.Level = Logger.Level.NONE,
    val requestInterval: Long,
    val requestsPerBatch: Int,
    val exceptionLogLevel: ExceptionType? = ExceptionType.NONE,
    val appFirstOpen: Boolean,
    val isOptOut: Boolean,
    val isAnonymous: Boolean,
    val isFragmentAutoTracking: Boolean,
    val isActivityAutoTracking: Boolean,
    val isAutoTracking: Boolean,
    val isBatchSupport: Boolean,
    val shouldMigrate: Boolean,
    val sendVersionInEachRequest: Boolean,
    val isUserMatching: Boolean,
    val temporarySessionId: String?,
) {
    fun calculateUsageParam(): Int {
        /*
            Activity Auto Tracking	2^9 (512)
            Fragments Auto Tracking	2^8 (256)
            Auto Tracking	2^7 (128)
            Background Sendout	2^6 (64)
            User Matching	2^5 (32)
            Webview	2^4 (16)
            Set EverId	2^3 (8)
            AppVersion in every Request	2^2 (4)
            Crash Tracking	2^1 (2)
            Batch Support	2^0 (1)
        */

        var usage = 0
        if (isActivityAutoTrackingEnabled()) usage += 512
        if (isFragmentAutoTrackingEnabled()) usage += 256
        if (isAutoTrackingEnabled()) usage += 128
        // background sendout only for iOS, so there is no addition of 64
        if (isUserMatchingEnabled()) usage += 32
        // webview is for Pixel side, so there is no addition of 16
        if (isEverIdSetByUser()) usage += 8
        if (sendVersionInEveryRequest()) usage += 4
        if (exceptionLogLevel != null && exceptionLogLevel != ExceptionType.NONE) usage += 2
        if (isBatchSupportEnabled()) usage += 1

        return usage
    }

    fun isActivityAutoTrackingEnabled(): Boolean = isActivityAutoTracking

    fun isFragmentAutoTrackingEnabled(): Boolean = isFragmentAutoTracking

    fun isUserMatchingEnabled(): Boolean = isUserMatching

    fun sendVersionInEveryRequest(): Boolean = sendVersionInEachRequest

    fun isBatchSupportEnabled(): Boolean = isBatchSupport

    fun isAutoTrackingEnabled(): Boolean {
        return isAutoTracking
    }

    fun isEverIdSetByUser(): Boolean {
        return everId?.isNotBlank() == true && everIdMode == GenerationMode.USER_GENERATED
    }

    fun printUsageStatisticCalculation(): String {
        val sb = StringBuffer()
        sb.appendLine()
        sb.appendLine("================================================")
        sb.appendLine("================USAGE STATISTICS================")
        sb.appendLine("================================================")
        sb.appendLine("Activity auto tracking: ${if (isActivityAutoTracking) 512 else 0}")
        sb.appendLine("Fragment auto tracking: ${if (isFragmentAutoTracking) 256 else 0}")
        sb.appendLine("Auto tracking: ${if (isAutoTracking) 128 else 0}")
        sb.appendLine("Background sendout: ${0}")
        sb.appendLine("User matching: ${if (isUserMatching) 32 else 0}")
        sb.appendLine("Webview: ${0}")
        sb.appendLine("Set EverId: ${if (isEverIdSetByUser()) 8 else 0}")
        sb.appendLine("App Version in every request: ${if (sendVersionInEachRequest) 4 else 0}")
        sb.appendLine("Crash tracking: ${if (exceptionLogLevel != ExceptionType.NONE) 2 else 0}")
        sb.appendLine("Batch support: ${if (isBatchSupport) 1 else 0}")
        sb.appendLine("================================================")

        return sb.toString()
    }

    override fun toString(): String {
        return "ActiveConfig(trackDomains='$trackDomains', trackIds=$trackIds, everId=$everId, everIdMode=$everIdMode, userAgent=$userAgent, userMatchingId=$userMatchingId, anonymousParams=$anonymousParams, logLevel=$logLevel, requestInterval=$requestInterval, requestsPerBatch=$requestsPerBatch, exceptionLogLevel=$exceptionLogLevel, appFirstOpen=$appFirstOpen, isOptOut=$isOptOut, isAnonymous=$isAnonymous, isFragmentAutoTracking=$isFragmentAutoTracking, isActivityAutoTracking=$isActivityAutoTracking, isAutoTracking=$isAutoTracking, isBatchSupport=$isBatchSupport, shouldMigrate=$shouldMigrate, sendVersionInEachRequest=$sendVersionInEachRequest, isUserMatchingEnabled=$isUserMatching, temporarySessionId=$temporarySessionId)"
    }

}