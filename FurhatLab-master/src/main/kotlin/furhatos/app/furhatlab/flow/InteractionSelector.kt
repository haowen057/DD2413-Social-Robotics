package furhatos.app.furhatlab.flow

import furhatos.app.furhatlab.flow.chat.ChatState
import furhatos.app.furhatlab.flow.hotel.HotelGreeting
import furhatos.flow.kotlin.*
import furhatos.nlu.common.No
import furhatos.nlu.common.Yes

val InteractionSelector: State = state {
    onEntry {
        furhat.ask("Welcome! Would you like to book a hotel room or just chat? Say 'hotel' for booking or 'chat' for conversation.")
    }

    onResponse {
        when {
            it.text.contains("hotel", ignoreCase = true) -> {
                furhat.say("I'll help you with hotel booking.")
                goto(HotelGreeting)
            }
            it.text.contains("chat", ignoreCase = true) -> {
                furhat.say("Let's have a conversation then.")
                goto(ChatState)
            }
            else -> {
                furhat.say("Please say 'hotel' for booking or 'chat' for conversation.")
                reentry()
            }
        }
    }
}