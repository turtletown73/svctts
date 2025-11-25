package net.scoobis.svctts;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.scoobis.svctts.providers.TtsProvider;
import net.scoobis.svctts.providers.FreeTtsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;

public class SvcTtsMod implements ModInitializer {

	public static final String MOD_ID = "svctts";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ModConfig CONFIG;
    public static ArrayList<short[]> QUEUE = new ArrayList<>();
    public static TtsProvider TTSPROVIDER;

    @Override
	public void onInitialize() {
        LOGGER.info("SVC TTS initialized!");
        AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        updateFromConfig();
	}
    
    public static void updateFromConfig() {
        if (CONFIG.provider.equals("freetts")) {
            TTSPROVIDER = new FreeTtsProvider();
        } else {
            TTSPROVIDER = null;
        }
    }

    public static void addToQueue(String text) {
        updateFromConfig();
        short[] audio = TTSPROVIDER.synthesizeAudio(text);
        int separator = 960;
        int length = audio.length / separator - 1;
        if (audio.length == 0) return;

        for (int i = 0; i < length; i++) {
            short[] newAudio;
            int offset = separator * i;
            if (i < length - 1) {
                newAudio = Arrays.copyOfRange(audio, offset, offset + separator);
            } else {
                newAudio = Arrays.copyOfRange(audio, offset, audio.length - 1);
            }

            QUEUE.add(newAudio);
        }
    }
}
