package com.example.voiceassistent.db

import com.example.voiceassistent.message.Message
import java.text.SimpleDateFormat

class MessageEntity {
    var text: String = ""
    var date: String = ""
    var isSend: Boolean = false

    constructor(text: String, date: String, isSend: Boolean) {
        this.text = text
        this.date = date
        this.isSend = isSend
    }

    constructor(message: Message) {
        this.text = message.text
        val format = SimpleDateFormat("HH:mm")
        this.date = format.format(message.date)
        this.isSend = message.isSend
    }
}