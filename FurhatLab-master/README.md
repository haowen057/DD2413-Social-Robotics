# Hotel Booking Dialog System

A Furhat OS-based hotel booking dialog system that handles complete room reservation interactions.

## 📋 Dialog Flow Overview

### 1. Initial Greeting (HotelGreeting)
**Process:**
- Furhat: Greets and asks if user wants to book a room
- User responses:
  - **Yes** → Proceed to date inquiry
  - **No** → Return to idle state
  - **Other** → Re-ask the question

**Possible Paths:**
- `User: "yes"` → AskCheckInDate
- `User: "no"` → Idle
- `User: "maybe"` → Re-ask
- `User: "I want to book"` → AskCheckInDate

### 2. Check-in Date Inquiry (AskCheckInDate)
**Process:**
- Asks for specific check-in date
- Recognizes keywords: today, tomorrow, days of week

**Possible Paths:**
- `User: "today"` → Set today, goto AskCheckOutDate
- `User: "tomorrow"` → Set tomorrow, goto AskCheckOutDate
- `User: "Monday"` → Set Monday, goto AskCheckOutDate
- `User: "next week"` → Default to today, goto AskCheckOutDate

### 3. Check-out Date Inquiry (AskCheckOutDate)
**Process:**
- Asks for check-out date based on check-in date
- Recognizes relative and absolute dates

**Possible Paths:**
- `User: "tomorrow"` → 1 night, goto AskNumberOfGuests
- `User: "in 2 days"` → 2 nights, goto AskNumberOfGuests
- `User: "Friday"` → Set Friday, goto AskNumberOfGuests
- `User: "3 days"` → 3 nights, goto AskNumberOfGuests

### 4. Number of Guests (AskNumberOfGuests)
**Process:**
- Asks for number of guests (1-4 people)
- Supports numeric and textual recognition

**Possible Paths:**
- `User: "1" / "one"` → 1 person, goto AskRoomType
- `User: "2" / "couple"` → 2 people, goto AskRoomType
- `User: "family"` → Default 4 people, goto AskRoomType
- `User: "5"` → Re-ask (out of range)
- `User: "just me"` → 1 person, goto AskRoomType

### 5. Room Type Selection (AskRoomType)
**Process:**
- Provides different room options based on guest count
- Select room by number or name

**Paths for 1 person:**
- `User: "1"` → Standard Single Room, goto AskFloorPreference
- `User: "2"` → Deluxe Single Room, goto AskFloorPreference
- `User: "standard single"` → Standard Single Room, goto AskFloorPreference

**Paths for 2+ people:**
- `User: "1"` → Standard Double Room, goto AskFloorPreference
- `User: "2"` → Deluxe Double Room, goto AskFloorPreference
- `User: "deluxe double"` → Deluxe Double Room, goto AskFloorPreference

### 6. Floor Preference (AskFloorPreference)
**Process:**
- Intelligently recommends floors based on room type
- Collects floor preferences

**Possible Paths:**
- `User: "lower floor"` → Lower floor, goto AskBreakfast
- `User: "higher floor"` → Higher floor, goto AskBreakfast
- `User: "quiet"` → Quiet floor, goto AskBreakfast
- `User: "view"` → View floor, goto AskBreakfast
- `User: other responses` → No preference, goto AskBreakfast

### 7. Breakfast Selection (AskBreakfast)
**Process:**
- Intelligently recommends breakfast options
- Calculates breakfast cost

**Possible Paths:**
- `User: "yes"` → Include breakfast, goto ConfirmBooking
- `User: "no"` → No breakfast, goto ConfirmBooking
- `User: ambiguous responses` → Re-ask

### 8. Final Confirmation (ConfirmBooking)
**Process:**
- Summarizes all booking information
- Final confirmation or modification

**Possible Paths:**
- `User: "yes"` → Complete booking, generate ID, return to Idle
- `User: "no"` → Restart booking process
- `User: "repeat"` → Repeat information, re-confirm
- `User: "price"` → Explain price, re-confirm
- `User: other responses` → Request clear confirmation

## 🔧 Special Interaction Scenarios

### Error Handling
- **Unrecognized responses** → Re-ask current question
- **Out-of-range values** → Prompt for valid range and re-ask
- **Logical conflicts** → Prompt and re-select

### User Interruption
- Saying "no" or negative at any time → May exit flow
- Requesting changes at confirmation → Restart entire flow

## 💾 Data Management

- Uses `userBookings` Map for temporary user data storage
- Clears user data after booking completion
- Supports multiple simultaneous users

## ✨ System Features

1. **Linear Flow**: Strict step-by-step information collection
2. **Conditional Branching**: Intelligent jumps based on user responses
3. **Error Recovery**: Re-asks when unrecognized
4. **Personalization**: Smart recommendations based on previous choices
5. **Confirmation Mechanism**: Final confirmation to prevent booking errors
