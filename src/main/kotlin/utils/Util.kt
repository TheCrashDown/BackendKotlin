package utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Util {
    companion object {
        fun getCurrentDate(): String {
            // Обновленный формат с миллисекундами
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
        }
    }
}
