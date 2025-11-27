package furhatos.app.furhatlab.flow.hotel

import furhatos.nlu.*
import furhatos.nlu.common.Yes
import furhatos.nlu.common.No
import furhatos.nlu.common.RequestRepeat
import furhatos.util.Language

// 房间类型
class RoomType : EnumEntity() {
    override fun getEnum(lang: Language): List<String> {
        return listOf(
            "standard single room", "standard single", "single room", "single",
            "deluxe single room", "deluxe single",
            "standard double room", "standard double", "double room", "double",
            "deluxe double room", "deluxe double"
        )
    }
}

// 楼层偏好
class FloorPreference : EnumEntity() {
    override fun getEnum(lang: Language): List<String> {
        return listOf("lower floor", "lower", "ground floor", "first floor",
            "higher floor", "higher", "upper floor", "top floor",
            "i don't mind", "doesn't matter", "no preference", "any floor")
    }
}

// 日期相关
class CheckInDate : EnumEntity() {
    override fun getEnum(lang: Language): List<String> {
        return listOf("today", "now", "right now", "this evening")
    }
}

class CheckOutDate : EnumEntity() {
    override fun getEnum(lang: Language): List<String> {
        return listOf("tomorrow", "next day", "day after")
    }
}

class NumberOfGuests : EnumEntity() {
    override fun getEnum(lang: Language): List<String> {
        return listOf("one", "1", "just me", "only me", "single", "alone")
    }
}

// 意图定义
class BookRoomIntent(val roomType: RoomType? = null) : Intent() {

    override fun getExamples(lang: Language): List<String> {
        return listOf(
            "I want to book a @roomType",
            "book @roomType",
            "@roomType please",
            "I'd like @roomType",
            "can I get @roomType",
            "@roomType"
        )
    }
}

class ConfirmBookingIntent : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf(
            "yes that's correct",
            "correct",
            "that's right",
            "yes please book it",
            "go ahead",
            "confirm",
            "yes"
        )
    }
}

class ChangeBookingIntent : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf(
            "no something is wrong",
            "I want to change something",
            "that's not correct",
            "I need to change",
            "wrong",
            "no"
        )
    }
}