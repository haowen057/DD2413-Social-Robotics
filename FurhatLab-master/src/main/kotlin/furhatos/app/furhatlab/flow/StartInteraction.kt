package furhatos.app.furhatlab.flow

import furhatos.app.furhatlab.flow.hotel.HotelGreeting
import furhatos.flow.kotlin.State
import furhatos.flow.kotlin.state

val StartInteraction: State = state {
    onEntry {
        // 启动酒店预订系统
        goto(HotelGreeting)
    }
}