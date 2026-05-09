package net.realplayer11.realclient;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RealClientMod implements ClientModInitializer {

    public static final String MOD_ID = "realclient";
    public static final String MOD_NAME = "Real Client";
    public static final String MOD_VERSION = "1.0.0";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("[Real Client] Mod caricata con successo! by Real_Player_11");
    }
}
