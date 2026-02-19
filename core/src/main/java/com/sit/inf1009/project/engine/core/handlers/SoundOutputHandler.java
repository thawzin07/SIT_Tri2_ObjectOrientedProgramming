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
 *  SOUND_PLAY     → plays the named clip (payload: String clip name)
 *  SOUND_STOP     → stops the named clip (payload: String clip name)
 *  SOUND_STOP_ALL → stops all currently playing clips
 *
 * Clip names map to audio files under /sounds/ on the classpath.
 * Example: "hit" maps to /sounds/hit.wav
 *
 * Usage — other managers trigger sound through IO:
 *   ioManager.sendOutput(new IOEvent(IOEvent.Type.SOUND_PLAY, "hit"));
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
                clip.setFramePosition(0); // rewind so it plays from start every time
                clip.start();
            }
        } catch (Exception e) {
            System.err.println("[SoundOutputHandler] Failed to play: " + clipName + " — " + e.getMessage());
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

        String path = "/sounds/" + clipName + ".wav";
        URL url = getClass().getResource(path);

        if (url == null) {
            System.err.println("[SoundOutputHandler] Sound file not found: " + path);
            return null;
        }

        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(stream);
            loadedClips.put(clipName, clip);
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("[SoundOutputHandler] Could not load: " + path + " — " + e.getMessage());
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