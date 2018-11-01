data class NativeContact(
        val name: String,
        val company: String,
        val jobTitle: String,
        val website: String,
        val postalAddresses: List<PostalAddress>,
        val emails: List<Item>,
        val phones: List<Item>,
        val avatar: ByteArray)

data class Item(
        val label: Int,
        val value: String)

data class PostalAddress(
        val label: String,
        val street: String,
        val city: String,
        val postcode: String,
        val region: String,
        val country: String)
