package net.scoobis.svctts.providers;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.audio.AudioPlayer;

import javax.sound.sampled.AudioFormat;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

// most of this is stolen from stack overflow credit: https://stackoverflow.com/questions/79470344/distorted-audio-when-capturing-freetts-output-as-a-byte-array
public class FreeTtsProvider implements TtsProvider {
    public FreeTtsProvider() {}

    @Override
    public short[] synthesizeAudio(String text) {
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

        short[] finalShorts = new short[shorts.length * 3];

        // interpolate
        for (int i = 0; i < finalShorts.length; i++) {
            double srcIndex = (double) i / 3;
            int index = (int) srcIndex;
            double fraction = srcIndex - index;

            short s1 = shorts[Math.min(index, shorts.length - 1)];
            short s2 = shorts[Math.min(index + 1, shorts.length - 1)];

            finalShorts[i] = (short) ((1 - fraction) * s1 + fraction * s2);
        }

        return finalShorts;
    }

    public static class ByteArrayAudioPlayer implements AudioPlayer {
        private final ByteArrayOutputStream byteOutputStream;
        private AudioFormat audioFormat;

        public ByteArrayAudioPlayer() {
            this.audioFormat = new AudioFormat(16000, 16, 1, true, false);
            this.byteOutputStream = new ByteArrayOutputStream();
        }

        @Override
        public void begin(int size) {
            byteOutputStream.reset();
        }

        @Override
        public boolean end() {
            return true;
        }

        @Override
        public void cancel() {
        }

        @Override
        public void close() {
            try {
                byteOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public float getVolume() {
            return 0;
        }

        @Override
        public void setVolume(float v) {
        }

        @Override
        public long getTime() {
            return 0;
        }

        @Override
        public void resetTime() {
        }

        @Override
        public AudioFormat getAudioFormat() {
            return audioFormat;
        }

        @Override
        public void pause() {}

        @Override
        public void resume() {}

        @Override
        public void reset() {
            byteOutputStream.reset();
        }

        @Override
        public boolean drain() {
            return true;
        }

        @Override
        public void startFirstSampleTimer() {}

        @Override
        public boolean write(byte[] audioData) {
            try {
                byteOutputStream.write(audioData);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public boolean write(byte[] audioData, int offset, int length) {
            try {
                byteOutputStream.write(audioData, offset, length);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public void showMetrics() {
        }

        public byte[] getAudioBytes() {
            return ByteBuffer.wrap(byteOutputStream.toByteArray()).order(ByteOrder.LITTLE_ENDIAN).array();
        }

        public void setAudioFormat(AudioFormat format) {
            this.audioFormat = format;
        }
    }
}
