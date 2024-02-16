package dulkirmod

import dulkirmod.command.*
import dulkirmod.config.DulkirConfig
import dulkirmod.utils.*
import kotlinx.coroutines.CoroutineScope
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import org.lwjgl.input.Keyboard
import java.io.File
import kotlin.coroutines.EmptyCoroutineContext

@Mod(
    modid = DulkirMod.MOD_ID,
    name = DulkirMod.MOD_NAME,
    version = DulkirMod.MOD_VERSION,
    clientSideOnly = true
)
class DulkirMod {

    private var lastLongUpdate: Long = 0
    private var lastLongerUpdate: Long = 0

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        val directory = File(event.modConfigurationDirectory, "dulkirmod")
        directory.mkdirs()
        val cch = ClientCommandHandler.instance

        // General
        cch.registerCommand(FarmingControlSchemeCommand())
    }

    @Mod.EventHandler
    fun onInit(event: FMLInitializationEvent) {
        config.init()
        // REGISTER Classes and such HERE
        val mcBus = MinecraftForge.EVENT_BUS
        mcBus.register(this)

        keyBinds.forEach(ClientRegistry::registerKeyBinding)
    }

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (event.phase == TickEvent.Phase.START && display != null) {
            mc.displayGuiScreen(display)
            display = null
        }

        val currTime = System.currentTimeMillis()
        if (currTime - lastLongUpdate > 1000) { // long update
            // Now I don't have to fetch the entries for multiple things, this just updates and caches
            // the data structure on 1s cooldown
            lastLongUpdate = currTime
        }

        if (currTime - lastLongerUpdate > 5000) { // longer update
            lastLongerUpdate = currTime
        }
    }

    companion object {
        const val MOD_ID = "dulkirmod"
        const val MOD_NAME = "Dulkir Mod"
        const val MOD_VERSION = "1.2.8.2"
        const val CHAT_PREFIX = "§f<§3DulkirMod§f>§r"

        val mc: Minecraft = Minecraft.getMinecraft()
        var config = DulkirConfig
        var display: GuiScreen? = null
        val scope = CoroutineScope(EmptyCoroutineContext)

        val keyBinds = arrayOf(
            KeyBinding("Open Settings", Keyboard.KEY_RSHIFT, "Dulkir Mod"),
            KeyBinding("Toggle Selfie Setting", Keyboard.KEY_NONE, "Dulkir Mod"),
            KeyBinding("Toggle Farming Controls", Keyboard.KEY_NONE, "Dulkir Mod"),
            KeyBinding("Dynamic Key", Keyboard.KEY_NONE, "Dulkir Mod")
        )
    }

}
