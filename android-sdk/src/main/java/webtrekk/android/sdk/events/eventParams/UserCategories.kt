package webtrekk.android.sdk.events.eventParams

import webtrekk.android.sdk.UserCategoriesParam
import webtrekk.android.sdk.extension.addNotNull

/**
 * Created by Aleksandar Marinkovic on 3/11/21.
 * Copyright (c) 2021 MAPP.
 */
data class UserCategories(
    var customCategories: Map<Int, String> = emptyMap()
) : BaseEvent {
    data class Birthday(var day: Int, var month: Int, var year: Int) {
        fun formatIt(): String {
            return if (day > 31 || month > 12 || year < 1000 || day < 1 || month < 1) {
                ""
            } else {
                var string = year.toString() + { if (month < 10) "0$month" else month.toString() }
                string += if (day < 10) "0$day" else day.toString()
                string
            }
        }
    }

    enum class Gender {
        UNKNOWN,
        MALE,
        FEMALE
    }

    var gender: Gender? = null
    var birthday: Birthday? = null
    var city: String? = null
    var country: String? = null
    var emailAddress: String? = null
    var emailReceiverId: String? = null
    var firstName: String? = null
    var customerId: String? = null
    var lastName: String? = null
    var phoneNumber: String? = null
    var street: String? = null
    var streetNumber: String? = null
    var zipCode: String? = null
    var newsletterSubscribed: Boolean = false

    override fun toHasMap(): MutableMap<String, String> {
        val map = mutableMapOf<String, String>()

        if (!customCategories.isNullOrEmpty()) {
            customCategories.forEach { (key, value) ->
                map["${UserCategoriesParam.URM_CATEGORY}$key"] = value
            }
        }
        if (birthday != null)
            map.addNotNull(UserCategoriesParam.BIRTHDAY, birthday!!.formatIt())
        map.addNotNull(UserCategoriesParam.CITY, city)
        map.addNotNull(UserCategoriesParam.COUNTRY, country)
        map.addNotNull(UserCategoriesParam.EMAIL_ADDRESS, emailAddress)
        map.addNotNull(UserCategoriesParam.EMAIL_RECEIVER_ID, emailReceiverId)
        map.addNotNull(UserCategoriesParam.FIRST_NAME, firstName)
        if (gender != null)
            map.addNotNull(UserCategoriesParam.GANDER, gender?.ordinal.toString())
        map.addNotNull(UserCategoriesParam.CUSTOMER_ID, customerId)
        map.addNotNull(UserCategoriesParam.LAST_NAME, lastName)
        map.addNotNull(
            UserCategoriesParam.NEW_SELLER_SUBSCRIBED,
            if (newsletterSubscribed) "1"
            else "2"
        )
        map.addNotNull(UserCategoriesParam.PHONE_NUMBER, phoneNumber)
        map.addNotNull(UserCategoriesParam.STREET, street)
        map.addNotNull(UserCategoriesParam.STREET_NUMBER, streetNumber)
        map.addNotNull(UserCategoriesParam.ZIP_CODE, zipCode)
        return map
    }
}