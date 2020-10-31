package xyz.cssxsh.weibo.data.blog

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NumberDisplayStrategy(
    @SerialName("apply_scenario_flag")
    val applyScenarioFlag: Int,
    @SerialName("display_text")
    val displayText: String,
    @SerialName("display_text_min_number")
    val displayTextMinNumber: Int
)