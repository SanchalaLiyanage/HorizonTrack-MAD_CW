data class User(
    val name: String = "",
    val email: String = "",
    val gender: String? = null,
    val birthday: String? = null,
    val profileImageUrl: String? = null,
    val isProfileComplete: Boolean = false // Defaults to false
) {
    constructor() : this("", "")
}
