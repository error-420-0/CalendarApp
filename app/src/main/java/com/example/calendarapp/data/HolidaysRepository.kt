package com.example.calendarapp.data

class HolidaysRepository {

    private val majorHolidays = listOf(
        Holiday(1, 1, "Новый год", "🎄"),
        Holiday(1, 7, "Рождество Христово", "⭐"),
        Holiday(1, 14, "Старый Новый год", "🎉"),
        Holiday(1, 25, "Татьянин день", "👩‍🎓"),
        Holiday(2, 14, "День Святого Валентина", "❤️"),
        Holiday(2, 23, "День защитника Отечества", "🛡️"),
        Holiday(3, 8, "Международный женский день", "🌸"),
        Holiday(4, 1, "День смеха", "🤡"),
        Holiday(4, 12, "День космонавтики", "🚀"),
        Holiday(5, 1, "Праздник Весны и Труда", "🔨"),
        Holiday(5, 9, "День Победы", "🎖️"),
        Holiday(6, 1, "День защиты детей", "👶"),
        Holiday(6, 12, "День России", "🇷🇺"),
        Holiday(7, 8, "День семьи, любви и верности", "💑"),
        Holiday(9, 1, "День знаний", "📚"),
        Holiday(10, 5, "День учителя", "👨‍🏫"),
        Holiday(11, 4, "День народного единства", "🤝"),
        Holiday(12, 12, "День Конституции", "📜"),
        Holiday(12, 31, "Канун Нового года", "🎆")
    )

    fun getMajorHoliday(month: Int, day: Int): Holiday? {
        return majorHolidays.find { it.month == month && it.day == day }
    }
}