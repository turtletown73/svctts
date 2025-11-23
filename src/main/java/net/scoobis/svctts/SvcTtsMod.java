package net.scoobis.svctts;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class SvcTtsMod implements ModInitializer {

	public static final String MOD_ID = "svctts";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ArrayList<short[]> QUEUE = new ArrayList<>();

    @Override
	public void onInitialize() {
        LOGGER.info("SVC TTS initialized!");
	}

    // most of this is stolen from stack overflow credit: https://stackoverflow.com/questions/79470344/distorted-audio-when-capturing-freetts-output-as-a-byte-array
    public static short[] synthesizeAudio(String text) {
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");

        VoiceManager voiceManager = VoiceManager.getInstance();
        Voice voice = voiceManager.getVoice("kevin16");

        ByteArrayAudioPlayer audioPlayer = new ByteArrayAudioPlayer();

        voice.allocate();
        voice.setAudioPlayer(audioPlayer);
        voice.speak(text);
        audioPlayer.close();
        voice.deallocate();

        byte[] rawBytes = audioPlayer.getAudioBytes();
        short[] shorts = new short[rawBytes.length / 2];
        ByteBuffer.wrap(rawBytes).asShortBuffer().get(shorts);

        short[] finalShorts = new short[shorts.length * 2];

        // interpolate
        for (int i = 0; i < rawBytes.length; i++) {
            double srcIndex = (double) i / 2;
            int index = (int) srcIndex;
            double fraction = srcIndex - index;

            short s1 = shorts[Math.min(index, shorts.length - 1)];
            short s2 = shorts[Math.min(index + 1, shorts.length - 1)];

            finalShorts[i] = (short) ((1 - fraction) * s1 + fraction * s2);
        }

        return finalShorts;
    }

    public static void addToQueue(String text) {
        short[] audio = synthesizeAudio(text);
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
