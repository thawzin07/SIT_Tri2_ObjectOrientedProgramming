package com.sit.inf1009.project.engine.core.handlers;

import com.sit.inf1009.project.engine.managers.IOEvent;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
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

    private final Map<String, Clip> loadedClips = new HashMap<>();

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
            Clip clip = getOrLoad(clipName);
            if (clip != null) {
                if (clip.isRunning()) clip.stop();
                clip.setFramePosition(0);
                clip.start();
            }
        } catch (Exception e) {
            System.err.println("[SoundOutputHandler] Failed to play: " + clipName + " - " + e.getMessage());
        }
    }

    private void stop(String clipName) {
        Clip clip = loadedClips.get(clipName);
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    private void stopAll() {
        loadedClips.values().forEach(clip -> {
            if (clip.isRunning()) clip.stop();
        });
    }

    private Clip getOrLoad(String clipName) {
        if (loadedClips.containsKey(clipName)) {
            return loadedClips.get(clipName);
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
            AudioInputStream stream = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(stream);
            loadedClips.put(clipName, clip);
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("[SoundOutputHandler] Could not load: " + resolvedPath + " - " + e.getMessage());
            return null;
        }
    }

    @Override
    public void close() {
        stopAll();
        loadedClips.values().forEach(Clip::close);
        loadedClips.clear();
        deactivate();
    }
}