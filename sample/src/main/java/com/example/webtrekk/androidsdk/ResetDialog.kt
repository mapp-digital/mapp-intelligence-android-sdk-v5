package com.example.webtrekk.androidsdk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_reset.btnOnlySetValues
import kotlinx.android.synthetic.main.dialog_reset.btnResetAndSetValues
import kotlinx.android.synthetic.main.dialog_reset.btnResetOnly
import kotlinx.android.synthetic.main.dialog_reset.spinnerExceptionLog
import kotlinx.android.synthetic.main.dialog_reset.switchAnonymousTracking
import kotlinx.android.synthetic.main.dialog_reset.switchBatchRequests
import kotlinx.android.synthetic.main.dialog_reset.switchSentAppVersion
import kotlinx.android.synthetic.main.dialog_reset.switchUserMatchingEnabled
import kotlinx.android.synthetic.main.dialog_reset.tieBatchRequestSize
import kotlinx.android.synthetic.main.dialog_reset.tieEverId
import kotlinx.android.synthetic.main.dialog_reset.tieTrackDomain
import kotlinx.android.synthetic.main.dialog_reset.tieTrackIds
import webtrekk.android.sdk.ExceptionType
import webtrekk.android.sdk.Webtrekk

interface ResetCallback {
    fun resetOnly()
    fun resetAndSetNewValues(data: Map<String, Any>)
    fun onlySetNewValues(data: Map<String, Any>)
}

class ResetDialog : DialogFragment() {
    companion object {
        val TAG = ResetDialog::class.java.name

        fun getInstance(callback: ResetCallback): ResetDialog {
            val dialog = ResetDialog()
            dialog.callback = callback
            return dialog
        }
    }

    lateinit var callback: ResetCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ThemeOverlay_AppCompat_Dialog_Alert)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_reset, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val exceptionTypes = ExceptionType.values().map { it.name }
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, exceptionTypes)
        spinnerExceptionLog.adapter = adapter

        btnResetOnly.setOnClickListener {
            callback.resetOnly()
            dismissAllowingStateLoss()
        }

        btnResetAndSetValues.setOnClickListener {
            callback.resetAndSetNewValues(getData())
            dismissAllowingStateLoss()
        }

        btnOnlySetValues.setOnClickListener {
            callback.onlySetNewValues(getData())
            dismissAllowingStateLoss()
        }

        initValues()
    }

    private fun initValues() {
        tieTrackIds.setText(Webtrekk.getInstance().getTrackIds().joinToString(separator = ", "))
        tieTrackDomain.setText(Webtrekk.getInstance().getTrackDomain())
        tieEverId.setText(Webtrekk.getInstance().getEverId())
        tieBatchRequestSize.setText("${Webtrekk.getInstance().getRequestsPerBatch()}")
        val anonymousTracking = Webtrekk.getInstance().isAnonymousTracking()
        switchAnonymousTracking.isChecked = anonymousTracking
        switchBatchRequests.isChecked = Webtrekk.getInstance().isBatchEnabled()
        switchSentAppVersion.isChecked = Webtrekk.getInstance().getVersionInEachRequest()
        switchUserMatchingEnabled.isChecked =
            Webtrekk.getInstance().isUserMatchingEnabled()

        switchUserMatchingEnabled.isEnabled = !anonymousTracking
        switchAnonymousTracking.setOnCheckedChangeListener { buttonView, isChecked ->
            switchUserMatchingEnabled.isEnabled = !isChecked
//            if (isChecked) {
//                switchUserMatchingEnabled.isChecked = false
//            }
        }

        val logIndex = ExceptionType.values().indexOf(Webtrekk.getInstance().getExceptionLogLevel())
        spinnerExceptionLog.setSelection(logIndex)
    }

    private fun getData(): Map<String, Any> {
        val data = HashMap<String, Any>()
        val trackIds = tieTrackIds.text?.toString()?.split(",")?.map { it.trim() }
        val trackDomain = tieTrackDomain.text?.toString()
        val everId = tieEverId.text?.toString()
        val batchRequestSize = tieBatchRequestSize.text?.toString()?.toInt() ?: 20
        val batchEnabled = switchBatchRequests.isChecked
        val sendAppVersionInRequests = switchSentAppVersion.isChecked
        val userMatching = switchUserMatchingEnabled.isChecked
        val anonymousTracking = switchAnonymousTracking.isChecked
        val exceptionLogLevel = ExceptionType.valueOf(
            spinnerExceptionLog.selectedItem?.toString() ?: ExceptionType.ALL.name
        )

        trackIds?.let { data.put("trackIds", it) }
        trackDomain?.let { data.put("trackDomain", it) }
        everId?.let { data.put("everId", it) }
        data["batchRequestSize"] = batchRequestSize
        data["batchEnabled"] = batchEnabled
        data["sendAppVersionInRequest"] = sendAppVersionInRequests
        data["exceptionLogLevel"] = exceptionLogLevel
        data["userMatching"] = userMatching
        data["anonymousTracking"] = anonymousTracking
        return data
    }
}