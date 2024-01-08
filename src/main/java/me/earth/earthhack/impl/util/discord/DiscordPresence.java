package me.earth.earthhack.impl.util.discord;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.modules.misc.rpc.RPC;
import me.earth.earthhack.impl.util.math.StopWatch;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

public class DiscordPresence implements Globals
{
    public static final Logger LOGGER = LogManager.getLogger(DiscordPresence.class);
    public static final DiscordRichPresence presence = new DiscordRichPresence();
    public static final DiscordRPC rpc = DiscordRPC.INSTANCE;
    private final RPC module;
    private Thread thread;

    private static int index;

    static {
        index = 1;
    }

    public DiscordPresence(RPC module)
    {
        this.module = module;
    }

    public synchronized void start()
    {
        if (thread != null)
        {
            thread.interrupt();
        }

        LOGGER.info("Initializing Discord RPC");
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        rpc.Discord_Initialize("1140773139580719234", handlers, true, "");
        presence.startTimestamp = System.currentTimeMillis() / 1000L;
        presence.details = getDetails();
        presence.state = "Killing opps";
        presence.largeImageKey = "minecraft-icon-1024x1024-3v7afpnj";
        presence.smallImageKey = "test1985";
        presence.smallImageText = Earthhack.VERSION;
        presence.largeImageText = "Phobos.eu";
        presence.partyId = "ae488379-351d-4a4f-ad32-2b9b01c91657";
        presence.partySize = 5;
        presence.partyMax = 5;
        presence.joinSecret = "MTI4NzM0OjFpMmhuZToxMjMxMjM= ";
        rpc.Discord_UpdatePresence(DiscordPresence.presence);
        StopWatch timer = new StopWatch();
        timer.reset();
        thread = new Thread(() ->
        {
            while (!Thread.currentThread().isInterrupted())
            {

                rpc.Discord_RunCallbacks();
                presence.details = getDetails();
                presence.state = "Killing opps";
                if (module.mode.getValue() == RPC.Mode.Cats) {
                    if (index == 10) {
                        index = 1;
                    }
                    DiscordPresence.presence.largeImageKey = "cat" + index;
                    ++index;

                }
                else if (module.mode.getValue() == RPC.Mode.Static) {
                    presence.largeImageKey = "minecraft-icon-1024x1024-3v7afpnj";
                }
                else if (module.mode.getValue() == RPC.Mode.Goofy) {
                    presence.largeImageKey = "https://i.imgur.com/SliYlrX.gif";
                }
                else if (module.mode.getValue() == RPC.Mode.Normal) {
                    presence.largeImageKey = "https://i.imgur.com/NNxErCz.gif";
                }
                if (module.customDetails.getValue()) {
                    presence.state = module.state.getValue();
                    presence.details = module.details.getValue();
                }

                rpc.Discord_UpdatePresence(presence);
                timer.reset();


                try
                {
                    // shut the fuck up lil nigga
                    //noinspection BusyWait
                    Thread.sleep(2000);
                }
                catch (InterruptedException ignored)
                {
                    Thread.currentThread().interrupt();
                    return;
                }

            }
        }, "RPC-Callback-Handler");
        thread.start();
    }

    public synchronized void stop()
    {
        LOGGER.info("Shutting down Discord RPC");
        if (thread != null && !thread.isInterrupted())
        {
            thread.interrupt();
            thread = null;
        }

        rpc.Discord_Shutdown();
    }

    private String getDetails()
    {
        return module.customDetails.getValue()
                ? module.details.getValue()
                : mc.player == null
                ? "Main menu"
                : mc.isIntegratedServerRunning() && module.showIP.getValue()
                ? "SinglePlayer"
                : "Playing on " + Minecraft.getMinecraft().getCurrentServerData().serverIP;

    }

}
