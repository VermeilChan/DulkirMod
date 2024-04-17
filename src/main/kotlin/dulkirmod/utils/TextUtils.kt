package dulkirmod.utils

import dulkirmod.DulkirMod
import net.minecraft.util.ChatComponentText

object TextUtils {
	private fun info(text: String, prefix: Boolean = true) {
		if (DulkirMod.mc.thePlayer == null) return

		val textPrefix = if (prefix) "${DulkirMod.CHAT_PREFIX} " else ""
		DulkirMod.mc.thePlayer.addChatMessage(ChatComponentText("$textPrefix$text§r"))
	}

	fun toggledMessage(message: String, state: Boolean) {
		val stateText = if (state) "§aON" else "§cOFF"
		info("§9Toggled $message §8[$stateText§8]§r")
	}

	private fun sendMessage(message: String) {
		DulkirMod.mc.thePlayer.sendChatMessage(message)
	}
}