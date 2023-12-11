package org.sui.cli.settings;

import kotlin.reflect.KProperty1

public interface ApplicationSettingTopic {

    fun suiCliPathChanged(e: SuiCliPathSettingsChangedEvent)

}


data class SuiCliPathSettingsChangedEvent(
    val oldState: SuiSettingsPanel.PanelData,
    val newState: SuiSettingsPanel.PanelData,
) {
    /** Use it like `event.isChanged(State::foo)` to check whether `foo` property is changed or not */
    fun isChanged(prop: KProperty1<SuiSettingsPanel.PanelData, *>): Boolean =
        prop.get(oldState) != prop.get(newState)
}
