package com.sit.inf1009.project.engine.core.handlers;

import com.sit.inf1009.project.engine.managers.IOEvent;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Output handler responsible for audio playback.
 *
 * Reacts to:
 *  SOUND_PLAY     -> plays the named clip (payload: String clip name)
 *  SOUND_STOP     -> stops the named clip (payload: String clip name)
 *  SOUND_STOP_ALL -> stops all currently playing clips
 *
 * Clip names map to audio files on the classpath.
 * Usage: ioManager.sendOutput(new IOEvent(IOEvent.Type.SOUND_PLAY, "hit"));
 */
public class SoundOutputHandler extends AbstractOutputHandler {

    private final Map<String, List<Clip>> clipPools = new HashMap<>();
    private static final int DEFAULT_POOL_SIZE = 6;

    public SoundOutputHandler() {
        // Preload common collision sound to reduce first-play latency.
        getOrCreatePool("Droplet");
    }

    @Override
    protected void handleOutput(IOEvent event) {
        if (event.getType() == IOEvent.Type.SOUND_PLAY) {
            play(event.getPayload(String.class));

        } else if (event.getType() == IOEvent.Type.SOUND_STOP) {
            stop(event.getPayload(String.class));

        } else if (event.getType() == IOEvent.Type.SOUND_STOP_ALL) {
            stopAll();
        }
    }

    private void play(String clipName) {
        try {
            if (clipName == null || clipName.isBlank()) return;
            List<Clip> pool = getOrCreatePool(clipName);
            Clip clip = selectAvailableClip(pool);
            if (clip == null) return;
            clip.setFramePosition(0);
            clip.start();
        } catch (Exception e) {
            System.err.println("[SoundOutputHandler] Failed to play: " + clipName + " - " + e.getMessage());
        }
    }

    private void stop(String clipName) {
        List<Clip> pool = clipPools.get(clipName);
        if (pool == null) return;
        for (Clip clip : pool) {
            if (clip != null && clip.isRunning()) {
                clip.stop();
            }
        }
    }

    private void stopAll() {
        clipPools.values().forEach(pool -> {
            for (Clip clip : pool) {
                if (clip != null && clip.isRunning()) clip.stop();
            }
        });
    }

    private List<Clip> getOrCreatePool(String clipName) {
        if (clipPools.containsKey(clipName)) {
            return clipPools.get(clipName);
        }

        String normalizedName = clipName;
        if (normalizedName != null && normalizedName.toLowerCase().endsWith(".wav")) {
            normalizedName = normalizedName.substring(0, normalizedName.length() - 4);
        }

        String[] candidatePaths = new String[] {
                "/Sounds/" + normalizedName + ".wav",
                "/sounds/" + normalizedName + ".wav",
                "/" + normalizedName + ".wav"
        };

        URL url = null;
        String resolvedPath = null;
        for (String path : candidatePaths) {
            url = getClass().getResource(path);
            if (url != null) {
                resolvedPath = path;
                break;
            }
        }

        if (url == null) {
            System.err.println("[SoundOutputHandler] Sound file not found for clip: " + clipName);
            return null;
        }

        try {
            List<Clip> pool = new ArrayList<>();
            for (int i = 0; i < DEFAULT_POOL_SIZE; i++) {
                AudioInputStream stream = AudioSystem.getAudioInputStream(url);
                Clip clip = AudioSystem.getClip();
                clip.open(stream);
                pool.add(clip);
            }
            clipPools.put(clipName, pool);
            return pool;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("[SoundOutputHandler] Could not load: " + resolvedPath + " - " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private Clip selectAvailableClip(List<Clip> pool) {
        if (pool == null || pool.isEmpty()) return null;
        for (Clip clip : pool) {
            if (!clip.isRunning()) {
                return clip;
            }
        }
        return pool.get(0);
    }

    @Override
    public void close() {
        stopAll();
        clipPools.values().forEach(pool -> pool.forEach(Clip::close));
        clipPools.clear();
        deactivate();
    }
}
