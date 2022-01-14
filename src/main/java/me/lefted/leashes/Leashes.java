package me.lefted.leashes;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Leashes implements ModInitializer {
    
    public static final Logger LOGGER = LogManager.getLogger("Leashes");

    @Override
    public void onInitialize() {
        LOGGER.info("Initialized Leashes - (50 Blocks)");
    }
}
