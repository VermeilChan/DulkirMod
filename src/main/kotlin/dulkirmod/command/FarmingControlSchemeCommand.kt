package dulkirmod.command

import dulkirmod.utils.TextUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraft.command.CommandException
import net.minecraft.command.ICommandSender

class FarmingControlSchemeCommand : ClientCommandBase("farmcontrols") {
    @Throws(CommandException::class)
    override fun processCommand(sender: ICommandSender, args: Array<String>) {
        toggleControls()
    }

    companion object {
        private var enabled = false

        private val minecraft: Minecraft
            get() = Minecraft.getMinecraft()

        private val controls = minecraft.gameSettings.run {
            listOf(
                keyBindAttack to Pair(-100, 57), // Left click and Space
                keyBindJump to Pair(57, -100), // Space and Left click
                keyBindForward to Pair(17, 200), // W and Arrow Up
                keyBindBack to Pair(31, 208), // S and Arrow Down
                keyBindLeft to Pair(30, 203), // A and Arrow Left
                keyBindRight to Pair(32, 205) // D and Arrow Right
            )
        }

        fun toggleControls() {
            controls.forEach { (key, codes) ->
                setKeyBinding(key, if (enabled) codes.first else codes.second)
            }

            minecraft.gameSettings.mouseSensitivity = if (enabled) 0.5f else 0.05f

            updateSettings()

            enabled = !enabled
            TextUtils.toggledMessage("Alright Bro", enabled)
        }

        private fun setKeyBinding(key: KeyBinding, code: Int) {
            KeyBinding.setKeyBindState(key.keyCode, false)
            key.keyCode = code
        }

        private fun updateSettings() {
            minecraft.gameSettings.saveOptions()
            minecraft.gameSettings.loadOptions()
        }
    }
}
