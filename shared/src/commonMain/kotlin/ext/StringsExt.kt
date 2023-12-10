package ext

fun String.truncateIfExceeds(maxLength: Int, placeholder: String = "..."): String {
    return if (length > maxLength) {
        "${substring(0, maxLength - placeholder.length)}$placeholder"
    } else {
        this
    }
}
