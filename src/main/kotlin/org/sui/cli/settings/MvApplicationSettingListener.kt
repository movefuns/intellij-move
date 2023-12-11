package org.sui.cli.settings

class MvApplicationSettingListener : ApplicationSettingTopic {
    override fun suiCliPathChanged(e: SuiCliPathSettingsChangedEvent) {
        println(e)
    }
}