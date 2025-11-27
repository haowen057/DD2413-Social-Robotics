package furhatos.app.furhatlab.flow.hotel

import furhatos.app.furhatlab.flow.Idle
import furhatos.flow.kotlin.*
import furhatos.nlu.common.No
import furhatos.nlu.common.Yes
import furhatos.nlu.common.RequestRepeat
import furhatos.records.User

// 用户数据类，存储预订信息
class HotelBooking {
    var roomType: String? = null
    var checkInDate: String = "today"
    var checkOutDate: String = "tomorrow"
    var numberOfGuests: Int = 1
    var floorPreference: String? = null
    var includeBreakfast: Boolean = false

    fun summarize(): String {
        val breakfastText = if (includeBreakfast) "with breakfast" else "without breakfast"
        val floorText = floorPreference ?: "no specific floor preference"
        val guestText = if (numberOfGuests == 1) "1 person" else "$numberOfGuests people"
        return "$roomType, checking in $checkInDate, checking out $checkOutDate, for $guestText, $floorText, $breakfastText"
    }
}

// 使用简单的Map来存储用户数据
private val userBookings = mutableMapOf<String, HotelBooking>()

// 扩展属性来获取用户的预订信息
val User.booking: HotelBooking
    get() = userBookings.getOrPut(this.id) { HotelBooking() }

// 酒店预订主状态流
val HotelGreeting: State = state {

    onEntry {
        furhat.say("Hello, welcome to KTH Hotel. Do you want to book a room?")
        furhat.listen()
    }

    onResponse<Yes> {
        furhat.say("No problem. I can help you make a reservation.")
        goto(AskCheckInDate)
    }

    onResponse<No> {
        furhat.say("Okay, please let me know if you change your mind.")
        goto(Idle)
    }

    onResponse {
        furhat.say("Please let me know if you'd like to book a room.")
        reentry()
    }
}

val AskCheckInDate: State = state {

    onEntry {
        furhat.ask("When would you like to check in? You can say today, tomorrow, or a specific day like Monday.")
    }

    onResponse {
        val text = it.text.lowercase()
        when {
            text.contains("today") || text.contains("now") -> {
                users.current.booking.checkInDate = "today"
                furhat.say("Alright. You would like to check in today.")
                goto(AskCheckOutDate)
            }
            text.contains("tomorrow") -> {
                users.current.booking.checkInDate = "tomorrow"
                furhat.say("Great. You want to check in tomorrow.")
                goto(AskCheckOutDate)
            }
            text.contains("monday") || text.contains("mon") -> {
                users.current.booking.checkInDate = "Monday"
                furhat.say("Okay, I've noted check-in for Monday.")
                goto(AskCheckOutDate)
            }
            text.contains("tuesday") || text.contains("tue") -> {
                users.current.booking.checkInDate = "Tuesday"
                furhat.say("Okay, I've noted check-in for Tuesday.")
                goto(AskCheckOutDate)
            }
            text.contains("wednesday") || text.contains("wed") -> {
                users.current.booking.checkInDate = "Wednesday"
                furhat.say("Okay, I've noted check-in for Wednesday.")
                goto(AskCheckOutDate)
            }
            text.contains("thursday") || text.contains("thu") -> {
                users.current.booking.checkInDate = "Thursday"
                furhat.say("Okay, I've noted check-in for Thursday.")
                goto(AskCheckOutDate)
            }
            text.contains("friday") || text.contains("fri") -> {
                users.current.booking.checkInDate = "Friday"
                furhat.say("Okay, I've noted check-in for Friday.")
                goto(AskCheckOutDate)
            }
            text.contains("saturday") || text.contains("sat") -> {
                users.current.booking.checkInDate = "Saturday"
                furhat.say("Okay, I've noted check-in for Saturday.")
                goto(AskCheckOutDate)
            }
            text.contains("sunday") || text.contains("sun") -> {
                users.current.booking.checkInDate = "Sunday"
                furhat.say("Okay, I've noted check-in for Sunday.")
                goto(AskCheckOutDate)
            }
            else -> {
                users.current.booking.checkInDate = "today"
                furhat.say("I'll assume you want to check in today.")
                goto(AskCheckOutDate)
            }
        }
    }
}

val AskCheckOutDate: State = state {

    onEntry {
        val checkIn = users.current.booking.checkInDate
        furhat.ask("Okay. You want to check in $checkIn. And when would you like to check out?")
    }

    onResponse {
        val text = it.text.lowercase()
        when {
            text.contains("tomorrow") -> {
                users.current.booking.checkOutDate = "tomorrow"
                furhat.say("So you'll be staying for one night.")
                goto(AskNumberOfGuests)
            }
            text.contains("monday") || text.contains("mon") -> {
                users.current.booking.checkOutDate = "Monday"
                furhat.say("Great. I've noted check-out for Monday.")
                goto(AskNumberOfGuests)
            }
            text.contains("tuesday") || text.contains("tue") -> {
                users.current.booking.checkOutDate = "Tuesday"
                furhat.say("Great. I've noted check-out for Tuesday.")
                goto(AskNumberOfGuests)
            }
            text.contains("wednesday") || text.contains("wed") -> {
                users.current.booking.checkOutDate = "Wednesday"
                furhat.say("Great. I've noted check-out for Wednesday.")
                goto(AskNumberOfGuests)
            }
            text.contains("thursday") || text.contains("thu") -> {
                users.current.booking.checkOutDate = "Thursday"
                furhat.say("Great. I've noted check-out for Thursday.")
                goto(AskNumberOfGuests)
            }
            text.contains("friday") || text.contains("fri") -> {
                users.current.booking.checkOutDate = "Friday"
                furhat.say("Great. I've noted check-out for Friday.")
                goto(AskNumberOfGuests)
            }
            text.contains("saturday") || text.contains("sat") -> {
                users.current.booking.checkOutDate = "Saturday"
                furhat.say("Great. I've noted check-out for Saturday.")
                goto(AskNumberOfGuests)
            }
            text.contains("sunday") || text.contains("sun") -> {
                users.current.booking.checkOutDate = "Sunday"
                furhat.say("Great. I've noted check-out for Sunday.")
                goto(AskNumberOfGuests)
            }
            text.contains("2 days") || text.contains("two days") -> {
                users.current.booking.checkOutDate = "in 2 days"
                furhat.say("So you'll be staying for two nights.")
                goto(AskNumberOfGuests)
            }
            text.contains("3 days") || text.contains("three days") -> {
                users.current.booking.checkOutDate = "in 3 days"
                furhat.say("So you'll be staying for three nights.")
                goto(AskNumberOfGuests)
            }
            else -> {
                users.current.booking.checkOutDate = "tomorrow"
                furhat.say("I'll set your check out for tomorrow.")
                goto(AskNumberOfGuests)
            }
        }
    }
}

val AskNumberOfGuests: State = state {

    onEntry {
        furhat.ask("How many people will be staying? You can say 1, 2, 3, or 4 people.")
    }

    onResponse {
        val text = it.text.lowercase()
        when {
            text.contains("one") || text.contains("1") || text.contains("just me") || text.contains("only me") || text.contains("single") -> {
                users.current.booking.numberOfGuests = 1
                furhat.say("Perfect. One guest.")
                goto(AskRoomType)
            }
            text.contains("two") || text.contains("2") || text.contains("couple") || text.contains("pair") -> {
                users.current.booking.numberOfGuests = 2
                furhat.say("Okay, two guests.")
                goto(AskRoomType)
            }
            text.contains("three") || text.contains("3") -> {
                users.current.booking.numberOfGuests = 3
                furhat.say("Alright, three guests.")
                goto(AskRoomType)
            }
            text.contains("four") || text.contains("4") -> {
                users.current.booking.numberOfGuests = 4
                furhat.say("Got it, four guests.")
                goto(AskRoomType)
            }
            text.contains("family") || text.contains("group") -> {
                users.current.booking.numberOfGuests = 4
                furhat.say("I'll book for a family of four.")
                goto(AskRoomType)
            }
            else -> {
                // 尝试提取数字
                val numbers = Regex("\\d+").findAll(text).map { it.value.toInt() }.toList()
                if (numbers.isNotEmpty() && numbers[0] in 1..4) {
                    users.current.booking.numberOfGuests = numbers[0]
                    furhat.say("Okay, ${numbers[0]} guests.")
                    goto(AskRoomType)
                } else {
                    furhat.say("Please tell me how many people: 1, 2, 3, or 4?")
                    reentry()
                }
            }
        }
    }
}

val AskRoomType: State = state {

    onEntry {
        furhat.ask("What type of room would you like? We have Standard Single Room, Deluxe Single Room, Standard Double Room, and Deluxe Double Room.")
    }

    onResponse {
        val text = it.text.lowercase()
        when {
            text.contains("standard single") -> {
                users.current.booking.roomType = "Standard Single Room"
                furhat.say("Great. I will check the availability for a Standard Single Room.")
                goto(AskFloorPreference)
            }
            text.contains("deluxe single") -> {
                users.current.booking.roomType = "Deluxe Single Room"
                furhat.say("Excellent choice. The Deluxe Single Room is more spacious and comfortable.")
                goto(AskFloorPreference)
            }
            text.contains("standard double") -> {
                users.current.booking.roomType = "Standard Double Room"
                furhat.say("Sure. The Standard Double Room comes with a larger bed.")
                goto(AskFloorPreference)
            }
            text.contains("deluxe double") -> {
                users.current.booking.roomType = "Deluxe Double Room"
                furhat.say("The Deluxe Double Room offers more space and a better view. I'll check availability for that.")
                goto(AskFloorPreference)
            }
            else -> {
                furhat.say("Please choose from Standard Single Room, Deluxe Single Room, Standard Double Room, or Deluxe Double Room.")
                reentry()
            }
        }
    }
}

val AskFloorPreference: State = state {

    onEntry {
        furhat.ask("Would you prefer a lower floor or a higher floor?")
    }

    onResponse {
        val text = it.text.lowercase()
        when {
            text.contains("lower") || text.contains("ground") || text.contains("first") -> {
                users.current.booking.floorPreference = "lower floor"
                furhat.say("Okay, I'll check the availability on the lower floors.")
                goto(AskBreakfast)
            }
            text.contains("higher") || text.contains("upper") || text.contains("top") -> {
                users.current.booking.floorPreference = "higher floor"
                furhat.say("Great, I'll look for a room on the upper floors.")
                goto(AskBreakfast)
            }
            else -> {
                users.current.booking.floorPreference = "no preference"
                furhat.say("No problem. I will assign a suitable floor for you.")
                goto(AskBreakfast)
            }
        }
    }
}

val AskBreakfast: State = state {

    onEntry {
        val guests = users.current.booking.numberOfGuests
        val cost = guests * 100
        furhat.ask("Would you like to add breakfast to your stay? It's 100 SEK per person, so $cost SEK for your group.")
    }

    onResponse<Yes> {
        users.current.booking.includeBreakfast = true
        furhat.say("Perfect. I will include breakfast in your booking.")
        goto(ConfirmBooking)
    }

    onResponse<No> {
        users.current.booking.includeBreakfast = false
        furhat.say("Alright, I won't add breakfast.")
        goto(ConfirmBooking)
    }

    onResponse {
        furhat.say("Please let me know if you'd like to include breakfast.")
        reentry()
    }
}

val ConfirmBooking: State = state {

    onEntry {
        val summary = users.current.booking.summarize()
        furhat.ask("Alright. You are booking a $summary. Is all this information correct?")
    }

    onResponse {
        val text = it.text.lowercase()
        when {
            text.contains("yes") || text.contains("correct") || text.contains("right") -> {
                furhat.say("Great. I will complete your reservation now. Your room has been booked! Thank you for choosing KTH Hotel.")
                // 重置预订信息
                userBookings.remove(users.current.id)
                goto(Idle)
            }
            text.contains("no") || text.contains("wrong") || text.contains("change") -> {
                furhat.say("I see. Please let me know which part you would like to change.")
                // 回到开始重新预订
                userBookings.remove(users.current.id)
                goto(HotelGreeting)
            }
            text.contains("repeat") -> {
                val summary = users.current.booking.summarize()
                furhat.say("Of course. Let me repeat the details for you. $summary. Is this correct?")
                reentry()
            }
            else -> {
                furhat.say("Please confirm if the information is correct or if you need to make changes.")
                reentry()
            }
        }
    }
}