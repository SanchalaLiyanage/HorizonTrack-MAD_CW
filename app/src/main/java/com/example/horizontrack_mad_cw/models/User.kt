data class User(
    val name: String = "",
    val email: String = "",
    val gender: String? = null,
    val birthday: String? = null,
    val profileImageUrl: String? = null,
    val isProfileComplete: Boolean = false // Defaults to false
) {
    // Secondary constructor to allow initialization with no arguments
    constructor() : this("", "", null, null, null, false)
}
