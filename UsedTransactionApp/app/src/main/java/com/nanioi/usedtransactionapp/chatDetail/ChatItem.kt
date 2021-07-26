package com.nanioi.usedtransactionapp.chatDetail

import java.security.MessageDigest

data class ChatItem(
    val senderId: String,
    val message: String
){
    constructor(): this("","")
}