package com.example.webtrekk.androidsdk

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import webtrekk.android.sdk.MediaParam
import webtrekk.android.sdk.TrackingParams
import webtrekk.android.sdk.Webtrekk
import webtrekk.android.sdk.events.eventParams.MediaParameters

class RapidScrollMediaTestFragment : Fragment(R.layout.fragment_rapid_scroll_media_test) {

    private val pageName = "RapidScrollMediaTest"
    private val durationSeconds = 100
    private val viewDurationMs = 100L
    private val videosCount = 100
    private val mediaNamePrefix = "rapid-scroll-video-"

    private lateinit var statusTextView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        statusTextView = view.findViewById(R.id.textRapidScrollMediaTestStatus)
        val runButton = view.findViewById<Button>(R.id.buttonRunRapidScrollMediaTest)
        runButton.setOnClickListener {
            runButton.isEnabled = false
            viewLifecycleOwner.lifecycleScope.launch {
                statusTextView.text = "Preparing database..."
                val clearResult = clearAllTrackingRecords()
                if (!clearResult.success) {
                    statusTextView.text = "Failed to clear DB before run: ${clearResult.message}"
                    runButton.isEnabled = true
                    return@launch
                }

                statusTextView.text = "Running simulation for $videosCount videos..."
                simulateRapidScrollTracking()
                statusTextView.text = "Waiting for async writes..."
                delay(2000)
                val validation = validateRecordedEvents()
                statusTextView.text = validation.toDisplayText()
                runButton.isEnabled = true
            }
        }
    }

    private suspend fun simulateRapidScrollTracking() {
        repeat(videosCount) { index ->
            val mediaName = "$mediaNamePrefix$index"
            val startPosition = index
            Webtrekk.getInstance().trackMedia(
                pageName = pageName,
                mediaName = mediaName,
                trackingParams = mediaParams(
                    action = MediaParameters.Action.INIT,
                    position = startPosition
                )
            )
            Webtrekk.getInstance().trackMedia(
                pageName = pageName,
                mediaName = mediaName,
                trackingParams = mediaParams(
                    action = MediaParameters.Action.PLAY,
                    position = startPosition
                )
            )

            delay(viewDurationMs)

            Webtrekk.getInstance().trackMedia(
                pageName = pageName,
                mediaName = mediaName,
                trackingParams = mediaParams(
                    action = MediaParameters.Action.STOP,
                    position = startPosition + 1
                )
            )
        }
    }

    private fun clearAllTrackingRecords(): DbOperationResult {
        return runCatching {
            val db = requireContext().openOrCreateDatabase(
                "webtrekk-db",
                Context.MODE_PRIVATE,
                null
            )
            db.use {
                if (!tableExists(it, "tracking_data") || !tableExists(it, "custom_params")) {
                    throw IllegalStateException(
                        "Expected tables tracking_data/custom_params do not exist. " +
                            "Ensure SDK is initialized before running the test."
                    )
                }
                it.beginTransaction()
                try {
                    it.execSQL("DELETE FROM custom_params")
                    it.execSQL("DELETE FROM tracking_data")
                    it.setTransactionSuccessful()
                } finally {
                    it.endTransaction()
                }
            }
            DbOperationResult(success = true, message = "DB cleared")
        }.getOrElse { error ->
            DbOperationResult(success = false, message = error.message ?: error.toString())
        }
    }

    private fun validateRecordedEvents(): ValidationResult {
        val expectedTotal = videosCount * 3
        val db = requireContext().openOrCreateDatabase(
            "webtrekk-db",
            Context.MODE_PRIVATE,
            null
        )

        db.use {
            val totalCount = rawInt(
                it,
                """
                SELECT COUNT(*)
                FROM tracking_data t
                JOIN custom_params mi ON mi.track_id = t.id AND mi.param_key = 'mi'
                JOIN custom_params mk ON mk.track_id = t.id AND mk.param_key = 'mk'
                WHERE mi.param_value LIKE '$mediaNamePrefix%';
                """.trimIndent()
            )

            val initCount = countByAction(it, MediaParameters.Action.INIT.code())
            val playCount = countByAction(it, MediaParameters.Action.PLAY.code())
            val stopCount = countByAction(it, MediaParameters.Action.STOP.code())
            val invalidGroupedRows = rawInt(
                it,
                """
                SELECT COUNT(*)
                FROM (
                    SELECT mi.param_value, mk.param_value, COUNT(*) AS c
                    FROM tracking_data t
                    JOIN custom_params mi ON mi.track_id = t.id AND mi.param_key = 'mi'
                    JOIN custom_params mk ON mk.track_id = t.id AND mk.param_key = 'mk'
                    WHERE mi.param_value LIKE '$mediaNamePrefix%'
                    GROUP BY mi.param_value, mk.param_value
                    HAVING c <> 1
                ) invalid_rows;
                """.trimIndent()
            )

            val isValid = totalCount == expectedTotal &&
                initCount == videosCount &&
                playCount == videosCount &&
                stopCount == videosCount &&
                invalidGroupedRows == 0

            return ValidationResult(
                expectedTotal = expectedTotal,
                actualTotal = totalCount,
                initCount = initCount,
                playCount = playCount,
                stopCount = stopCount,
                invalidGroupedRows = invalidGroupedRows,
                isValid = isValid
            )
        }
    }

    private fun countByAction(db: SQLiteDatabase, action: String): Int {
        return rawInt(
            db,
            """
            SELECT COUNT(*)
            FROM tracking_data t
            JOIN custom_params mi ON mi.track_id = t.id AND mi.param_key = 'mi'
            JOIN custom_params mk ON mk.track_id = t.id AND mk.param_key = 'mk'
            WHERE mi.param_value LIKE '$mediaNamePrefix%'
            AND mk.param_value = '$action';
            """.trimIndent()
        )
    }

    private fun rawInt(db: SQLiteDatabase, sql: String): Int {
        db.rawQuery(sql, null).use { cursor ->
            return if (cursor.moveToFirst()) cursor.getInt(0) else 0
        }
    }

    private fun tableExists(db: SQLiteDatabase, table: String): Boolean {
        db.rawQuery(
            "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
            arrayOf(table)
        ).use { cursor ->
            return cursor.moveToFirst()
        }
    }

    private fun mediaParams(action: MediaParameters.Action, position: Int): TrackingParams {
        return TrackingParams().apply {
            putAll(
                mapOf(
                    MediaParam.MEDIA_ACTION to action.code(),
                    MediaParam.MEDIA_POSITION to position.toString(),
                    MediaParam.MEDIA_DURATION to durationSeconds.toString()
                )
            )
        }
    }

    private data class DbOperationResult(
        val success: Boolean,
        val message: String
    )

    private data class ValidationResult(
        val expectedTotal: Int,
        val actualTotal: Int,
        val initCount: Int,
        val playCount: Int,
        val stopCount: Int,
        val invalidGroupedRows: Int,
        val isValid: Boolean
    ) {
        fun toDisplayText(): String {
            val header = if (isValid) {
                "Result: PASS"
            } else {
                "Result: FAIL"
            }
            return "$header\nExpected total: $expectedTotal\nActual total: $actualTotal\n" +
                "INIT: $initCount, PLAY: $playCount, STOP: $stopCount\n" +
                "Invalid grouped rows (duplicates/missing): $invalidGroupedRows"
        }
    }
}
