package org.sui.cli.settings

import com.intellij.util.messages.Topic

class MvApplicationSettingService {

    companion object {
        val MOVE_APPLICATION_SETTINGS_TOPIC = Topic(
            "move settings changes",
            ApplicationSettingTopic::class.java
        )

        var isValidSuiCli: Boolean = false
    }

}
