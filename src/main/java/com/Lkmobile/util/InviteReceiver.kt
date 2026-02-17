package com.Lkmobile.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class InviteReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_INVITE = "com.Lkmobile.ACTION_INVITE"
        const val EXTRA_FROM_USER_NAME = "from_user_name"
        const val EXTRA_INVITE_ID = "invite_id"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_INVITE) {
            val fromUserName = intent.getStringExtra(EXTRA_FROM_USER_NAME) ?: "Someone"
            val inviteId = intent.getIntExtra(EXTRA_INVITE_ID, 0)
            NotificationHelper.showInviteNotification(context, fromUserName, inviteId)
        }
    }
}
