
import java.util.HashMap

fun mapToContact(map: Map<String, Any>): NativeContact {
    val name = valueOrDefault(map["name"])
    val company = valueOrDefault(map["company"])
    val jobTitle = valueOrDefault(map["jobTitle"])
    val website = valueOrDefault(map["website"])
    val avatar = if (map["avatar"] != null) map["avatar"] as ByteArray else byteArrayOf(0)

    val mapEmails = map["emails"] as List<Map<Any, String>>?
    val emails = mutableListOf<Item>()
    if (mapEmails != null) {
        for (mapEmail in mapEmails) {
            emails.add(mapToItem(mapEmail))
        }
    }

    val mapPhones = map["phones"] as List<Map<Any, String>>?
    val phones = mutableListOf<Item>()
    if (mapPhones != null) {
        for (mapPhone in mapPhones) {
            phones.add(mapToItem(mapPhone))
        }
    }
    val mapPostalAddresses = map["postalAddresses"] as List<Map<String, String>>?
    val postalAddresses = mutableListOf<PostalAddress>()
    if (mapPostalAddresses != null) {
        for (mapPostalAddress in mapPostalAddresses) {
            postalAddresses.add(mapToPostalAddress(mapPostalAddress))
        }
    }

    return NativeContact(
        name = name,
        company = company,
        jobTitle = jobTitle,
        website = website,
        avatar = avatar,
        emails = emails,
        phones = phones,
        postalAddresses = postalAddresses
    )
}

fun contactToMap(contact: NativeContact): Map<String, Any> {
    val contactMap = HashMap<String, Any>()

    contactMap["name"] = contact.name
    contactMap["company"] = contact.company
    contactMap["jobTitle"] = contact.jobTitle
    contactMap["website"] = contact.website
    contactMap["avatar"] = contact.avatar

    val emailsMap = mutableListOf<Map<Any, String>>()
    for (email in contact.emails) {
        emailsMap.add(itemToMap(email))
    }
    contactMap["emails"] = emailsMap

    val phonesMap = mutableListOf<Map<Any, String>>()
    for (phone in contact.phones) {
        phonesMap.add(itemToMap(phone))
    }
    contactMap["phones"] = phonesMap

    val addressesMap = mutableListOf<Map<String, String>>()
    for (address in contact.postalAddresses) {
        addressesMap.add(postalAddressToMap(address))
    }
    contactMap["postalAddresses"] = addressesMap

    return contactMap
}

fun postalAddressToMap(postalAddress: PostalAddress): Map<String, String> {
    return mapOf(
        postalAddress.label to "label",
        postalAddress.street to "street",
        postalAddress.city to "city",
        postalAddress.postcode to "postcode",
        postalAddress.region to "region",
        postalAddress.country to "country"
    )
}

fun mapToPostalAddress(map: Map<String, Any>): PostalAddress {
    val label = valueOrDefault(map["label"])
    val street = valueOrDefault(map["street"])
    val city = valueOrDefault(map["city"])
    val postcode = valueOrDefault(map["postcode"])
    val region = valueOrDefault(map["region"])
    val country = valueOrDefault(map["country"])

    return PostalAddress(
        label = label,
        street = street,
        city = city,
        postcode = postcode,
        region = region,
        country = country
    );
}

fun itemToMap(item: Item): Map<Any, String> {
    return mapOf(
        item.label to "label",
        item.value to "value"
    )
}

fun mapToItem(map: Map<Any, String>): Item {
    val label = if (map["label"] != null) map["label"] as Int else 1
    val value = valueOrDefault(map["value"])
    return Item(label=label, value=value)
}

private fun valueOrDefault(value: Any?): String {
    if (value != null && value is String) {
        return value;
    } else {
        return ""
    }
}