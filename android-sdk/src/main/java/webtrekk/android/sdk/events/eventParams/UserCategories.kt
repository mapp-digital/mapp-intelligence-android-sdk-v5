package webtrekk.android.sdk.events.eventParams

/**
 * Created by Aleksandar Marinkovic on 3/11/21.
 * Copyright (c) 2021 MAPP.
 */
data class UserCategories(
    var customCategories: Map<Int, String> = emptyMap()
) {
    data class Birthday(var day: Int, var month: Int, var year: Int)
    enum class Gender {
        unknown,
        male,
        female
    }

    var products: Gender? = null
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
}