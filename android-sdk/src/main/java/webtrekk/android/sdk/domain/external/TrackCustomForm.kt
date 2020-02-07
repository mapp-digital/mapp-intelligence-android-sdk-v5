/*
 *  MIT License
 *
 *  Copyright (c) 2019 Webtrekk GmbH
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 */

package webtrekk.android.sdk.domain.external

import android.view.View
import android.view.ViewGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import webtrekk.android.sdk.Logger
import webtrekk.android.sdk.api.RequestType
import webtrekk.android.sdk.api.UrlParams
import webtrekk.android.sdk.util.CoroutineDispatchers
import webtrekk.android.sdk.util.coroutineExceptionHandler
import webtrekk.android.sdk.data.entity.TrackRequest
import webtrekk.android.sdk.data.model.FormField
import webtrekk.android.sdk.domain.ExternalInteractor
import webtrekk.android.sdk.domain.internal.CacheTrackRequestWithCustomParams
import webtrekk.android.sdk.extension.toInt
import webtrekk.android.sdk.extension.orderList
import webtrekk.android.sdk.extension.isTrackable
import webtrekk.android.sdk.extension.notTrackedView
import webtrekk.android.sdk.extension.parseView
import webtrekk.android.sdk.extension.toFormField
import webtrekk.android.sdk.extension.toRequest

import kotlin.coroutines.CoroutineContext

/**
 * Track custom event use case. The track custom event will append the 'ct' param automatically to the custom params.
 */
internal class TrackCustomForm(
    coroutineContext: CoroutineContext,
    private val cacheTrackRequestWithCustomParams: CacheTrackRequestWithCustomParams
) : ExternalInteractor<TrackCustomForm.Params>, KoinComponent {

    private val _job = Job()
    override val scope =
        CoroutineScope(_job + coroutineContext) // Starting a new job with context of the parent.

    /**
     * [logger] the injected logger from Webtrekk.
     */
    private val logger by inject<Logger>()

    override fun invoke(invokeParams: Params, coroutineDispatchers: CoroutineDispatchers) {
        // If opt out is active, then return
        if (invokeParams.isOptOut) return

        scope.launch(
            coroutineDispatchers.ioDispatcher + coroutineExceptionHandler(
                logger
            )
        ) {
            val params = emptyMap<String, String>().toMutableMap()
            params[RequestType.FORM.value] =
                invokeParams.formName + "|" + invokeParams.confirmButton.toInt()
            params[UrlParams.FORM_FIELD] = createField(
                invokeParams.viewGroup,
                invokeParams.trackingIds,
                invokeParams.renameFields,
                invokeParams.changeFieldsValue,
                invokeParams.anonymous,
                invokeParams.fieldsOrder
            )
            // Cache the track request with its custom parafms.
            cacheTrackRequestWithCustomParams(
                CacheTrackRequestWithCustomParams.Params(
                    invokeParams.trackRequest,
                    params
                )
            )
                .onSuccess { logger.debug("Cached form request: $it") }
                .onFailure { logger.error("Error while caching form request: $it") }
        }
    }

    private fun createField(
        viewGroup: ViewGroup,
        trackingIds: List<Int>,
        renameFields: Map<Int, String>,
        changeFieldsValue: Map<Int, String>,
        anonymous: Boolean,
        fieldsOrder: List<Int>
    ): String {

        val array: MutableList<View> = mutableListOf()
        viewGroup.parseView(array)
        array.notTrackedView(trackingIds)
        var listFormField = mutableListOf<FormField>()
        array.forEach { view: View ->
            if (view.isTrackable()) {
                val name: String? = renameFields[view.id]
                val value: String? = changeFieldsValue[view.id]
                listFormField.add(view.toFormField(name, anonymous, value))
            }
        }
        listFormField = listFormField.orderList(fieldsOrder)

        return listFormField.toRequest()
    }

    /**
     * A data class encapsulating the specific params related to this use case.
     *
     * @param trackRequest the track request that is created and will be cached in the data base.
     * @param isOptOut the opt out value.
     * @param viewGroup viewGroup for the analysis
     * @param formName form name can be activity name or changed
     * @param trackingIds only track specific elements of the view
     * @param renameFields rename specific fields
     * @param confirmButton clicked confirm or cancel button for the form
     * @param anonymous hide content
     * @param changeFieldsValue in some case is good to change value of the specific fields
     */
    data class Params(
        val trackRequest: TrackRequest,
        val isOptOut: Boolean,
        val viewGroup: ViewGroup,
        val formName: String,
        val trackingIds: List<Int>,
        val renameFields: Map<Int, String>,
        val confirmButton: Boolean,
        val anonymous: Boolean,
        val changeFieldsValue: Map<Int, String>,
        val fieldsOrder: List<Int>
    )
}
