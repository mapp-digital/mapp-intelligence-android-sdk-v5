package com.example.webtrekk.androidsdk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.example.webtrekk.androidsdk.databinding.DialogResetBinding
import webtrekk.android.sdk.ExceptionType
import webtrekk.android.sdk.Webtrekk

interface ResetCallback {
    fun resetOnly()
    fun resetAndSetNewValues(data: Map<String, Any>)
    fun onlySetNewValues(data: Map<String, Any>)
}

class ResetDialog : DialogFragment() {
    private var _binding:DialogResetBinding?=null
    private val binding:DialogResetBinding
        get() = _binding!!
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
    ): View {
        _binding= DialogResetBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val exceptionTypes = ExceptionType.entries.map { it.name }
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, exceptionTypes)
        binding.spinnerExceptionLog.adapter = adapter

        binding.btnResetOnly.setOnClickListener {
            callback.resetOnly()
            dismissAllowingStateLoss()
        }

        binding.btnResetAndSetValues.setOnClickListener {
            callback.resetAndSetNewValues(getData())
            dismissAllowingStateLoss()
        }

        binding.btnOnlySetValues.setOnClickListener {
            callback.onlySetNewValues(getData())
            dismissAllowingStateLoss()
        }

        initValues()
    }

    private fun initValues() {
        binding.tieTrackIds.setText(Webtrekk.getInstance().getTrackIds().joinToString(separator = ", "))
        binding.tieTrackDomain.setText(Webtrekk.getInstance().getTrackDomain())
        binding.tieEverId.setText(Webtrekk.getInstance().getEverId())
        binding.tieBatchRequestSize.setText("${Webtrekk.getInstance().getRequestsPerBatch()}")
        val anonymousTracking = Webtrekk.getInstance().isAnonymousTracking()
        binding.switchAnonymousTracking.isChecked = anonymousTracking
        binding.switchBatchRequests.isChecked = Webtrekk.getInstance().isBatchEnabled()
        binding.switchSentAppVersion.isChecked = Webtrekk.getInstance().getVersionInEachRequest()
        binding.switchUserMatchingEnabled.isChecked =
            Webtrekk.getInstance().isUserMatchingEnabled()

        binding.switchUserMatchingEnabled.isEnabled = !anonymousTracking
        binding.switchAnonymousTracking.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.switchUserMatchingEnabled.isEnabled = !isChecked
//            if (isChecked) {
//                switchUserMatchingEnabled.isChecked = false
//            }
        }

        val logIndex = ExceptionType.entries.indexOf(Webtrekk.getInstance().getExceptionLogLevel())
        binding.spinnerExceptionLog.setSelection(logIndex)
    }

    private fun getData(): Map<String, Any> {
        val data = HashMap<String, Any>()
        val trackIds = binding.tieTrackIds.text?.toString()?.split(",")?.map { it.trim() }
        val trackDomain = binding.tieTrackDomain.text?.toString()
        val everId = binding.tieEverId.text?.toString()
        val batchRequestSize = binding.tieBatchRequestSize.text?.toString()?.toInt() ?: 20
        val batchEnabled = binding.switchBatchRequests.isChecked
        val sendAppVersionInRequests = binding.switchSentAppVersion.isChecked
        val userMatching = binding.switchUserMatchingEnabled.isChecked
        val anonymousTracking = binding.switchAnonymousTracking.isChecked
        val exceptionLogLevel = ExceptionType.valueOf(
            binding.spinnerExceptionLog.selectedItem?.toString() ?: ExceptionType.ALL.name
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

    override fun onDestroyView() {
        _binding=null
        super.onDestroyView()
    }
}