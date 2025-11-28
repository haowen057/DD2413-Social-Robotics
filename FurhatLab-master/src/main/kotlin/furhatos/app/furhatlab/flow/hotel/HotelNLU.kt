package furhatos.app.furhatlab.flow.hotel

import furhatos.nlu.*
import furhatos.nlu.common.*
import furhatos.util.Language

// 房间类型定义
class RoomType : EnumEntity(stemming = true) {
    companion object {
        val STANDARD_SINGLE = listOf("standard single", "basic single", "single standard", "single basic",
            "a", "option a", "type a", "first option", "cheapest single")
        val DELUXE_SINGLE = listOf("deluxe single", "nice single", "premium single", "single deluxe",
            "b", "option b", "type b", "second option", "better single")
        val STANDARD_DOUBLE = listOf("standard double", "basic double", "double standard", "double basic",
            "c", "option c", "type c", "third option", "cheapest double")
        val DELUXE_DOUBLE = listOf("deluxe double", "nice double", "premium double", "double deluxe",
            "d", "option d", "type d", "fourth option", "best room", "best double")
    }

    override fun getEnum(lang: Language): List<String> {
        return STANDARD_SINGLE + DELUXE_SINGLE + STANDARD_DOUBLE + DELUXE_DOUBLE
    }
}

// 楼层偏好
class FloorPreference : EnumEntity(stemming = true) {
    override fun getEnum(lang: Language): List<String> {
        return listOf(
            "lower floor", "lower", "ground floor", "first floor", "second floor",
            "higher floor", "higher", "upper floor", "top floor", "high floor",
            "i don't mind", "doesn't matter", "no preference", "any floor", "whatever",
            "surprise me", "you choose", "doesn't care"
        )
    }
}

// 特殊请求类型
class SpecialRequestType : EnumEntity() {
    override fun getEnum(lang: Language): List<String> {
        return listOf(
            "sea view", "ocean view", "city view", "garden view", "view",
            "quiet room", "quiet", "no noise", "peaceful",
            "accessible", "disabled access", "wheelchair",
            "non-smoking", "no smoking", "smoking room",
            "early check in", "late check out", "early check-in", "late check-out",
            "extra bed", "extra towel", "extra pillow"
        )
    }
}

// 日期实体
class CheckInDate : EnumEntity() {
    override fun getEnum(lang: Language): List<String> {
        return listOf("today", "now", "right now", "this evening", "tonight")
    }
}

class CheckOutDate : EnumEntity() {
    override fun getEnum(lang: Language): List<String> {
        return listOf("tomorrow", "next day", "day after", "following day")
    }
}

// 人数实体
class NumberOfGuests : EnumEntity() {
    override fun getEnum(lang: Language): List<String> {
        return listOf("one", "1", "just me", "only me", "single", "alone",
            "two", "2", "couple", "pair", "both of us",
            "three", "3", "family", "group",
            "four", "4")
    }
}

// 意图定义
class QuickBookIntent(val roomType: RoomType? = null) : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf(
            "book @roomType",
            "I want @roomType",
            "@roomType please",
            "get me @roomType",
            "I'll take @roomType",
            "give me @roomType"
        )
    }
}

class AskPriceIntent : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf(
            "how much is it",
            "what's the price",
            "how much does it cost",
            "what's the cost",
            "price please",
            "tell me the price"
        )
    }
}

class AskRecommendationIntent : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf(
            "what do you recommend",
            "what's your recommendation",
            "which one is better",
            "what's the best option",
            "suggest something",
            "help me choose"
        )
    }
}

class CompareRoomsIntent : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf(
            "compare the rooms",
            "what's the difference",
            "difference between rooms",
            "which room is better",
            "compare option a and b"
        )
    }
}

// 紧急情况处理
class EmergencyIntent : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf(
            "help",
            "emergency",
            "I need help",
            "urgent",
            "problem",
            "trouble"
        )
    }
}

// 服务询问
class AskServiceIntent : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf(
            "what services do you have",
            "what facilities",
            "what amenities",
            "what's included",
            "what features"
        )
    }
}

// 修改信息意图
class ChangeInformationIntent(val informationType: InformationType? = null) : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf(
            "I want to change @informationType",
            "change @informationType",
            "modify @informationType",
            "I need to update @informationType",
            "wrong @informationType"
        )
    }
}

class InformationType : EnumEntity() {
    override fun getEnum(lang: Language): List<String> {
        return listOf("room type", "room", "date", "check in", "check out",
            "number of guests", "people", "floor", "breakfast")
    }
}

// 确认意图
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
