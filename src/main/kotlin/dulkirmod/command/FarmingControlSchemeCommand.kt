package dulkirmod.command

import dulkirmod.config.DulkirConfig
import dulkirmod.utils.TextUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraft.command.CommandException
import net.minecraft.command.ICommandSender

class FarmingControlSchemeCommand : ClientCommandBase("farmcontrols") {
    private var enabled = false
    @Throws(CommandException::class)
    override fun processCommand(sender: ICommandSender, args: Array<String>) {
        toggleControls()
    }

    companion object {
        private var enabled = false

        /**
         * Method to do the brunt work of the command. This is separate, so I can also let the user set a
         * keybind to do the same thing.
         */
        fun toggleControls() {
            val minecraft = Minecraft.getMinecraft()
            val breakingKey: KeyBinding = minecraft.gameSettings.keyBindAttack
            val jumpKey: KeyBinding = minecraft.gameSettings.keyBindJump
            val forwardKey: KeyBinding = minecraft.gameSettings.keyBindForward
            val backwardKey: KeyBinding = minecraft.gameSettings.keyBindBack
            val leftKey: KeyBinding = minecraft.gameSettings.keyBindLeft
            val rightKey: KeyBinding = minecraft.gameSettings.keyBindRight

            if (!enabled) {
                KeyBinding.setKeyBindState(breakingKey.keyCode, false)
                breakingKey.keyCode = 57 // 57 = Space key code

                KeyBinding.setKeyBindState(jumpKey.keyCode, false)
                jumpKey.keyCode = -100 // -100 = Left click key code

                // Arrow key codes
                forwardKey.keyCode = 200 // 200 = Arrow Up key code
                backwardKey.keyCode = 208 // 208 = Arrow Down key code
                leftKey.keyCode = 203 // 203 = Arrow Left key code
                rightKey.keyCode = 205 // 205 = Arrow Right key code

                minecraft.gameSettings.mouseSensitivity = 0.0f
            } else {
                KeyBinding.setKeyBindState(breakingKey.keyCode, false)
                breakingKey.keyCode = -100 // -100 = Left click key code

                KeyBinding.setKeyBindState(jumpKey.keyCode, false)
                jumpKey.keyCode = 57 // 57 = Space key code

                // Restore default key codes
                forwardKey.keyCode = 17 // 17 = W key code
                backwardKey.keyCode = 31 // 31 = S key code
                leftKey.keyCode = 30 // 30 = A key code
                rightKey.keyCode = 32 // 32 = D key code

                minecraft.gameSettings.mouseSensitivity = DulkirConfig.defaultSens / 2
            }

            // Save the changes to the control settings
            minecraft.gameSettings.saveOptions()
            minecraft.gameSettings.loadOptions()

            enabled = !enabled
            TextUtils.toggledMessage("lil bru i think you just enabled the farming controls :3 oil up or else im gonna do something really to u :D", enabled)
        }
    }
}
