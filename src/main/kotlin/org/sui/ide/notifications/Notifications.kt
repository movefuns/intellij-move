package org.sui.ide.notifications

import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationGroupManager
import org.sui.common.NOTIFACATION_GROUP

object Notifications {
    fun pluginNotifications(): NotificationGroup {
        return NotificationGroupManager.getInstance().getNotificationGroup(NOTIFACATION_GROUP)
    }
}
