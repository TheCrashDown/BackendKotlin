package utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Util {
    companion object {
        fun getCurrentDateTime(): String {
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
        }
    }
}
