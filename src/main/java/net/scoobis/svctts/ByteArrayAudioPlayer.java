package net.scoobis.svctts;

import com.sun.speech.freetts.audio.AudioPlayer;

import javax.sound.sampled.AudioFormat;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteArrayAudioPlayer implements AudioPlayer {
    private ByteArrayOutputStream byteOutputStream;
    private AudioFormat audioFormat;

    public ByteArrayAudioPlayer() {
        this.audioFormat = new AudioFormat(16000, 16, 1, true, false);
        this.byteOutputStream = new ByteArrayOutputStream();
    }

    public ByteArrayAudioPlayer(AudioFormat format) {
        this.audioFormat = format;
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
