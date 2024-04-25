package com.example.voiceassistent.message

import com.example.voiceassistent.db.MessageEntity
import java.text.SimpleDateFormat
import java.util.Date

data class Message(var text: String, var date: Date = Date(), var isSend: Boolean) {
    fun getMessage(messageEntity: MessageEntity) : Message {
        val format = SimpleDateFormat("HH:mm")
        return Message(messageEntity.text, format.parse(messageEntity.date)!!, messageEntity.isSend)
    }
}