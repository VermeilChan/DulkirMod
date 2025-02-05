package dulkirmod

import dulkirmod.config.DulkirConfig
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent
import org.lwjgl.input.Keyboard
import java.io.File

@Mod(
    modid = DulkirMod.MOD_ID,
    name = DulkirMod.MOD_NAME,
    version = DulkirMod.MOD_VERSION,
    clientSideOnly = true
)
class DulkirMod {
    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        val directory = File(event.modConfigurationDirectory, "dulkirmod")
        directory.mkdirs()
        val cch = ClientCommandHandler.instance

        cch.registerCommand(FarmingControlSchemeCommand())
    }

    @Mod.EventHandler
    fun onInit(event: FMLInitializationEvent) {
        config.init()
        val mcBus = MinecraftForge.EVENT_BUS
        mcBus.register(this)

        keyBinds.forEach(ClientRegistry::registerKeyBinding)
    }

    @SubscribeEvent
    fun onKey(event: KeyInputEvent) {
        if (keyBinds[0].isPressed) config.openGui()
    }

    companion object {
        const val MOD_ID = "dulkirmod"
        const val MOD_NAME = "Dulkir Mod"
        const val MOD_VERSION = "1.2.9"
        const val CHAT_PREFIX = "§f<§3DulkirMod§f>§r"

        val mc: Minecraft = Minecraft.getMinecraft()
        var config = DulkirConfig

        val keyBinds = arrayOf(
            KeyBinding("Open Settings", Keyboard.KEY_RSHIFT, "Dulkir Mod"),
        )
    }
}
