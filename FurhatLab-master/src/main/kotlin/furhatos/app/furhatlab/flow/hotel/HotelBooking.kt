package furhatos.app.furhatlab.flow.hotel

import furhatos.app.furhatlab.flow.Idle
import furhatos.flow.kotlin.*
import furhatos.gestures.Gestures
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
    var totalPrice: Int = 0

    // 智能价格计算
    fun calculatePrice(): Int {
        var basePrice = when (roomType) {
            "Standard Single Room" -> 800
            "Deluxe Single Room" -> 1200
            "Standard Double Room" -> 1500
            "Deluxe Double Room" -> 2000
            else -> 1000
        }

        // 早餐费用
        if (includeBreakfast) {
            basePrice += numberOfGuests * 100
        }

        totalPrice = basePrice
        return totalPrice
    }

    fun summarize(): String {
        calculatePrice() // 计算价格
        val breakfastText = if (includeBreakfast) "with breakfast" else "without breakfast"
        val floorText = floorPreference ?: "no specific floor preference"
        val guestText = if (numberOfGuests == 1) "1 person" else "$numberOfGuests people"
        return "$roomType, checking in $checkInDate, checking out $checkOutDate, for $guestText, $floorText, $breakfastText. Total: $totalPrice SEK per night"
    }
}

// 使用简单的Map来存储用户数据
private val userBookings = mutableMapOf<String, HotelBooking>()

// 扩展属性来获取用户的预订信息
val User.booking: HotelBooking
    get() = userBookings.getOrPut(this.id) { HotelBooking() }


// waiting queue

val hotelWaitingQueue = mutableListOf<User>()

fun enqueueHotelUser(u: User) {
    if (hotelWaitingQueue.none { it.id == u.id }) {
        hotelWaitingQueue.add(u)
    }
}

fun removeHotelUser(u: User) {
    hotelWaitingQueue.removeAll { it.id == u.id }
}

//fun removeHotelUserById(userId: String?) {
//    if (userId == null) return
//    hotelWaitingQueue.removeAll { it.id == userId }
//}



enum class NewUserStrategy {
    IGNORE, // fake
    TALK_AND_TURN,
    GLANCE
}

/**
 * do not use IGNORE!!!
 * Instead, go to init.kt, and change maxUsers = 1
 * */

var currentNewUserStrategy = NewUserStrategy.GLANCE   // change distraction mode



/**
 * User2 is our actor, who will be waiting for check-in too, and thus, should not leave.
 * Let User1 not sit in exact front of Furhat. Instead, sit on left front.
 * Then User2 comes to wait and keeps sitting on right front and a little bit behind User1.
 * Before begin, tell User1 that there will be a second user coming and queueing. No need to interact with User2.
 * */



// parent

val HotelInteraction: State = state {

    onUserEnter { newcomer ->


        enqueueHotelUser(newcomer)
        val first = hotelWaitingQueue.firstOrNull() ?: newcomer

        if (newcomer.id != first.id) {

            when (currentNewUserStrategy) {

                NewUserStrategy.IGNORE -> {  // fake
                    furhat.listen()

                }


                NewUserStrategy.TALK_AND_TURN -> {


                    furhat.attend(newcomer)
                    delay(1000)
                    furhat.gesture(Gestures.Smile(duration =  1.0, strength = 1.0))
                    furhat.say("Hi, I see you. I will help you as soon as I finish this booking.")
                    furhat.attend(first)
                    delay(800)
                    reentry()
                }

                NewUserStrategy.GLANCE -> {

                    furhat.attend(newcomer)
                    delay(1000)
                    furhat.gesture(Gestures.Smile(duration =  0.7, strength = 1.0))
                    furhat.attend(first)
                    delay(800)
                    reentry()
                }
            }
        } else {
            furhat.attend(newcomer)
            goto(HotelGreeting)
        }
    }

    onUserLeave { leaving ->
        val wasActive = hotelWaitingQueue.isNotEmpty() &&
                hotelWaitingQueue.first().id == leaving.id

        removeHotelUser(leaving)

        if (wasActive) {
            val next = hotelWaitingQueue.firstOrNull()
            if (next != null) {
                furhat.attend(next)
                goto(HotelGreeting)
            } else {
                goto(Idle)
            }
        } else {
            if (furhat.isListening) {
                furhat.listen()
            } else {
                reentry()
            }
        }
    }

    onResponse<RequestRepeat> {
        reentry()
    }

    onNoResponse {
        furhat.say("Sorry, I didn't hear you.")
        reentry()
    }
}

// 酒店预订主状态流
val HotelGreeting: State = state(HotelInteraction) {

    onEntry {

        users.current?.let { current ->
            removeHotelUser(current)
            // queue tester
            hotelWaitingQueue.add(0, current)
        }

        furhat.gesture(Gestures.Smile(duration =  1.0, strength = 1.0))
        furhat.say("Hello, welcome to KTH Hotel. Do you want to book a room?")
        furhat.listen()
    }

    onResponse<Yes> {
        furhat.gesture(Gestures.Nod(duration =  1.0, strength = 0.7))
        furhat.say("No problem. I can help you make a reservation.")
        goto(AskCheckInDate)
    }

    onResponse<No> {
        furhat.say("Okay, please let me know if you change your mind.")

        removeHotelUser(users.current)

        val next = hotelWaitingQueue.firstOrNull()
        if (next != null) {
            furhat.attend(next)
            reentry()
        } else {
            goto(Idle)
        }
    }

    onResponse {
        furhat.say("Please let me know if you'd like to book a room.")
        reentry()
    }
}

val AskCheckInDate: State = state(HotelInteraction) {

    onEntry {
        delay(500)
        furhat.gesture(Gestures.Smile(duration =  1.0, strength = 1.0))
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

val AskCheckOutDate: State = state(HotelInteraction) {

    onEntry {
        val checkIn = users.current.booking.checkInDate
        delay(800)
        furhat.gesture(Gestures.Smile(duration =  1.0, strength = 1.0))
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

val AskNumberOfGuests: State = state(HotelInteraction) {

    onEntry {
        delay(700)
        furhat.gesture(Gestures.Smile(duration =  1.0, strength = 1.0))
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

val AskRoomType: State = state(HotelInteraction) {

    onEntry {
        val guests = users.current.booking.numberOfGuests

        val roomOptions = when {
            guests == 1 -> {
                """
                What type of room would you like? 
                Option 1: Standard Single Room (800 SEK) - Perfect for solo travelers.
                Option 2: Deluxe Single Room (1200 SEK) - More spacious and comfortable.
                Please choose 1 or 2.
                """.trimIndent()
            }
            guests >= 2 -> {
                """
                What type of room would you like for $guests people? 
                Option 1: Standard Double Room (1500 SEK) - Comfortable for couples.
                Option 2: Deluxe Double Room (2000 SEK) - More space and better view.
                Please choose 1 or 2.
                """.trimIndent()
            }
            else -> {
                """
                What type of room would you like? 
                Option 1: Standard Single Room (800 SEK).
                Option 2: Deluxe Single Room (1200 SEK).
                Option 3: Standard Double Room (1500 SEK).
                Option 4: Deluxe Double Room (2000 SEK).
                Please choose 1, 2, 3, or 4.
                """.trimIndent()
            }
        }

        delay(700)
        furhat.gesture(Gestures.Smile(duration =  1.0, strength = 1.0))
        furhat.ask(roomOptions)
    }

    onResponse {
        val text = it.text.lowercase()
        val guests = users.current.booking.numberOfGuests

        when {
            // 数字识别
            text.contains("1") || text.contains("one") || text.contains("first") -> {
                if (guests == 1) {
                    users.current.booking.roomType = "Standard Single Room"
                    furhat.say("Great choice! Standard Single Room at 800 SEK per night.")
                    goto(AskFloorPreference)
                } else {
                    users.current.booking.roomType = "Standard Double Room"
                    furhat.say("Perfect! Standard Double Room at 1500 SEK per night.")
                    goto(AskFloorPreference)
                }
            }
            text.contains("2") || text.contains("two") || text.contains("second") -> {
                if (guests == 1) {
                    users.current.booking.roomType = "Deluxe Single Room"
                    furhat.say("Excellent! Deluxe Single Room at 1200 SEK per night.")
                    goto(AskFloorPreference)
                } else {
                    users.current.booking.roomType = "Deluxe Double Room"
                    furhat.say("Excellent choice! Deluxe Double Room at 2000 SEK per night.")
                    goto(AskFloorPreference)
                }
            }
            text.contains("3") || text.contains("three") || text.contains("third") -> {
                users.current.booking.roomType = "Standard Double Room"
                furhat.say("Sure. Standard Double Room at 1500 SEK per night.")
                goto(AskFloorPreference)
            }
            text.contains("4") || text.contains("four") || text.contains("fourth") -> {
                users.current.booking.roomType = "Deluxe Double Room"
                furhat.say("Perfect. Deluxe Double Room at 2000 SEK per night.")
                goto(AskFloorPreference)
            }
            // 原有的房间名称识别保持不变
            text.contains("standard single") -> {
                if (guests > 1) {
                    furhat.say("The Standard Single Room is for one person only. Let me show you our double rooms.")
                    reentry()
                } else {
                    users.current.booking.roomType = "Standard Single Room"
                    furhat.say("Great choice! Standard Single Room at 800 SEK per night.")
                    goto(AskFloorPreference)
                }
            }
            // ... 其他房间名称识别逻辑保持不变
            else -> {
                if (guests == 1) {
                    furhat.say("Please choose 1 for Standard Single, 2 for Deluxe Single, or say the room name.")
                } else {
                    furhat.say("Please choose 1 for Standard Double, 2 for Deluxe Double, or say the room name.")
                }
                reentry()
            }
        }
    }
}

val AskFloorPreference: State = state(HotelInteraction) {

    onEntry {
        // 根据房间类型智能推荐楼层
        val roomType = users.current.booking.roomType ?: ""
        val recommendation = when {
            roomType.contains("Deluxe") -> "For our deluxe rooms, I recommend higher floors for better views."
            else -> "We have rooms available on both lower and higher floors."
        }

        delay(700)
        furhat.gesture(Gestures.Smile(duration =  1.0, strength = 1.0))

        furhat.ask("$recommendation Would you prefer a lower floor or a higher floor?")
    }

    onResponse {
        val text = it.text.lowercase()
        when {
            text.contains("lower") || text.contains("ground") || text.contains("first") -> {
                users.current.booking.floorPreference = "lower floor"
                furhat.say("Okay, I'll check availability on the lower floors. Great for easy access.")
                goto(AskBreakfast)
            }
            text.contains("higher") || text.contains("upper") || text.contains("top") -> {
                users.current.booking.floorPreference = "higher floor"
                furhat.say("Great choice! Higher floors usually have better views. I'll look for availability.")
                goto(AskBreakfast)
            }
            text.contains("view") -> {
                users.current.booking.floorPreference = "higher floor with view"
                furhat.say("I'll request a higher floor room with the best view available.")
                goto(AskBreakfast)
            }
            text.contains("quiet") -> {
                users.current.booking.floorPreference = "quiet floor"
                furhat.say("I'll look for a quiet room away from elevators and main streets.")
                goto(AskBreakfast)
            }
            else -> {
                users.current.booking.floorPreference = "no preference for floor"
                furhat.say("No problem. I will assign a suitable floor for you.")
                goto(AskBreakfast)
            }
        }
    }
}

val AskBreakfast: State = state(HotelInteraction) {

    onEntry {
        val guests = users.current.booking.numberOfGuests
        val roomType = users.current.booking.roomType ?: ""
        val cost = guests * 100

        // 智能早餐推荐
        val recommendation = when {
            roomType.contains("Deluxe") -> "Since you've chosen a deluxe room, I highly recommend adding breakfast to complete your premium experience."
            guests >= 2 -> "For $guests people, our breakfast buffet offers great variety that everyone will enjoy."
            else -> "Our breakfast includes fresh pastries, fruits, and hot dishes - a great way to start your day."
        }

        delay(700)
        furhat.gesture(Gestures.Smile(duration =  1.0, strength = 1.0))

        furhat.ask("$recommendation Would you like to add breakfast to your stay? It's 100 SEK per person, so $cost SEK for your group.")
    }

    onResponse<Yes> {
        users.current.booking.includeBreakfast = true
        val guests = users.current.booking.numberOfGuests
        furhat.gesture(Gestures.Smile(duration =  1.0, strength = 1.0))
        furhat.say("Perfect. Breakfast included for $guests ${if (guests == 1) "person" else "people"}.")
        goto(ConfirmBooking)
    }

    onResponse<No> {
        users.current.booking.includeBreakfast = false
        furhat.gesture(Gestures.Smile(duration =  1.0, strength = 1.0))
        furhat.say("Alright, no breakfast included. You can always add it later.")
        goto(ConfirmBooking)
    }

    onResponse {
        furhat.say("Please let me know if you'd like to include breakfast - yes or no?")
        reentry()
    }
}

val ConfirmBooking: State = state(HotelInteraction) {

    onEntry {
        users.current.booking.calculatePrice() // 确保价格已计算
        val summary = users.current.booking.summarize()
        delay(1000)
        furhat.gesture(Gestures.Smile(duration =  1.0, strength = 1.0))
        furhat.ask("Alright. Let me confirm your booking: $summary. Is all this information correct?")
    }

    onResponse {
        val text = it.text.lowercase()
        when {
            text.contains("yes") || text.contains("correct") || text.contains("right") || text.contains("confirm") -> {
                // 生成预订编号
                val reservationNumber = "KTH${(100000..999999).random()}"
                furhat.gesture(Gestures.Smile(duration =  1.0, strength = 1.0))
                furhat.say("""
                    Great! Your room has been booked successfully. 
                    Your reservation number is $reservationNumber.
                    Thank you for choosing KTH Hotel! Bye!
                """.trimIndent())
                furhat.gesture(Gestures.Smile(duration =  1.0, strength = 1.0))
                delay(800)

                // 重置预订信息
                userBookings.remove(users.current.id)
                //furhat.listen()

//                removeHotelUser(users.current)
//                val next = hotelWaitingQueue.firstOrNull()
//                if (next != null) {
//                    furhat.attend(next)
//                    goto(HotelGreeting)
//                } else {
//                    goto(Idle)
//                }




            }



            text.contains("no") || text.contains("wrong") || text.contains("change") -> {
                furhat.gesture(Gestures.Smile(duration =  1.0, strength = 1.0))
                furhat.say("I see. Let's start over to make the changes.")
                // 回到开始重新预订
                userBookings.remove(users.current.id)
                goto(HotelGreeting)
            }
            text.contains("repeat") || text.contains("again") -> {
                val summary = users.current.booking.summarize()
                furhat.gesture(Gestures.Smile(duration =  1.0, strength = 1.0))
                furhat.say("Of course. Let me repeat the details: $summary. Is this correct?")
                reentry()
            }
            text.contains("price") || text.contains("cost") -> {
                users.current.booking.calculatePrice()
                furhat.gesture(Gestures.Smile(duration =  1.0, strength = 1.0))
                furhat.say("The total price is ${users.current.booking.totalPrice} SEK per night. Is this booking correct?")
                reentry()
            }
            else -> {
                furhat.gesture(Gestures.Smile(duration =  1.0, strength = 1.0))
                furhat.say("Please confirm if the information is correct or if you need to make changes.")
                reentry()
            }
        }
    }
}
