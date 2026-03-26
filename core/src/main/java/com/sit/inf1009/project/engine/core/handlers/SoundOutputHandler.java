package com.sit.inf1009.project.engine.core.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.sit.inf1009.project.engine.managers.IOEvent;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

    private static final Set<String> BACKGROUND_TRACKS = new HashSet<>();
    static {
        BACKGROUND_TRACKS.add("foodmenumusic");
        BACKGROUND_TRACKS.add("settingmusic");
        BACKGROUND_TRACKS.add("howtoplaymusic");
        BACKGROUND_TRACKS.add("playersetupmusic");
        BACKGROUND_TRACKS.add("leaderboardmusic");
    }

    private final Map<String, Sound> effectPool = new HashMap<>();
    private final Map<String, Music> musicPool = new HashMap<>();
    private String currentMusicName;
    private Music currentMusic;
    private float musicVolume = 1.0f;

    public SoundOutputHandler() {
        // Prime commonly-used sounds.
        getOrCreateEffect("collisionmusic");
        getOrCreateEffect("btn_click");
    }

    @Override
    protected void handleOutput(IOEvent event) {
        if (event.getType() == IOEvent.Type.SOUND_PLAY) {
            play(event.getPayload(String.class));

        } else if (event.getType() == IOEvent.Type.SOUND_STOP) {
            stop(event.getPayload(String.class));

        } else if (event.getType() == IOEvent.Type.SOUND_STOP_ALL) {
            stopAll();
        } else if (event.getType() == IOEvent.Type.SOUND_SET_MUSIC_VOLUME) {
            Float value = event.getPayloadOrNull(Float.class);
            if (value != null) {
                setMusicVolume(value);
            }
        }
    }

    private void play(String clipName) {
        if (clipName == null || clipName.isBlank()) return;

        String resolvedName = resolveAlias(clipName);
        if (isMusicTrack(resolvedName)) {
            playMusic(resolvedName);
            return;
        }

        Sound effect = getOrCreateEffect(resolvedName);
        if (effect != null) {
            effect.play(1.0f);
        }
    }

    private void stop(String clipName) {
        if (clipName == null || clipName.isBlank()) return;
        String resolvedName = resolveAlias(clipName);

        if (isMusicTrack(resolvedName)) {
            Music music = musicPool.get(resolvedName);
            if (music != null) {
                music.stop();
            }
            if (resolvedName.equals(currentMusicName)) {
                currentMusic = null;
                currentMusicName = null;
            }
            return;
        }

        Sound effect = effectPool.get(resolvedName);
        if (effect != null) effect.stop();
    }

    private void stopAll() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic = null;
            currentMusicName = null;
        }
        effectPool.values().forEach(Sound::stop);
        musicPool.values().forEach(Music::stop);
    }

    private void playMusic(String trackName) {
        if (trackName.equals(currentMusicName) && currentMusic != null) {
            if (!currentMusic.isPlaying()) {
                currentMusic.play();
            }
            return;
        }

        if (currentMusic != null) {
            currentMusic.stop();
        }
        Music next = getOrCreateMusic(trackName);
        if (next == null) return;

        next.setLooping(true);
        next.setVolume(musicVolume);
        next.play();
        currentMusic = next;
        currentMusicName = trackName;
    }

    private void setMusicVolume(float value) {
        float clamped = Math.max(0f, Math.min(1f, value));
        musicVolume = clamped;
        if (currentMusic != null) {
            currentMusic.setVolume(musicVolume);
        }
    }

    private Sound getOrCreateEffect(String clipName) {
        String normalized = normalizeName(clipName);
        if (effectPool.containsKey(normalized)) {
            return effectPool.get(normalized);
        }

        FileHandle file = findAudioFile(normalized);
        if (file == null) {
            System.err.println("[SoundOutputHandler] Sound file not found for clip: " + clipName);
            return null;
        }

        try {
            Sound sound = Gdx.audio.newSound(file);
            effectPool.put(normalized, sound);
            return sound;
        } catch (Exception e) {
            System.err.println("[SoundOutputHandler] Could not load effect: " + file.path() + " - " + e.getMessage());
            return null;
        }
    }

    private Music getOrCreateMusic(String clipName) {
        String normalized = normalizeName(clipName);
        if (musicPool.containsKey(normalized)) {
            return musicPool.get(normalized);
        }

        FileHandle file = findAudioFile(normalized);
        if (file == null) {
            System.err.println("[SoundOutputHandler] Sound file not found for clip: " + clipName);
            return null;
        }

        try {
            Music music = Gdx.audio.newMusic(file);
            musicPool.put(normalized, music);
            return music;
        } catch (Exception e) {
            System.err.println("[SoundOutputHandler] Could not load music: " + file.path() + " - " + e.getMessage());
            return null;
        }
    }

    private FileHandle findAudioFile(String clipName) {
        String[] candidates = new String[] {
                "Sounds/" + clipName + ".mp3",
                "Sounds/" + clipName + ".wav",
                "sounds/" + clipName + ".mp3",
                "sounds/" + clipName + ".wav",
                clipName + ".mp3",
                clipName + ".wav"
        };

        for (String path : candidates) {
            try {
                FileHandle file = Gdx.files.internal(path);
                if (file.exists()) {
                    return file;
                }
            } catch (Exception ignored) {
                // Try next candidate.
            }
        }
        return null;
    }

    private boolean isMusicTrack(String clipName) {
        if (clipName == null) return false;
        return BACKGROUND_TRACKS.contains(clipName.toLowerCase());
    }

    private String resolveAlias(String clipName) {
        String normalized = normalizeName(clipName);
        if ("droplet".equals(normalized)) return "collisionmusic";
        return normalized;
    }

    private String normalizeName(String clipName) {
        if (clipName == null) return "";
        String normalized = clipName.trim();
        String lower = normalized.toLowerCase();
        if (lower.endsWith(".mp3") || lower.endsWith(".wav")) {
            normalized = normalized.substring(0, normalized.length() - 4);
        }
        return normalized;
    }

    @Override
    public void close() {
        stopAll();
        effectPool.values().forEach(Sound::dispose);
        musicPool.values().forEach(Music::dispose);
        effectPool.clear();
        musicPool.clear();
        deactivate();
    }
}
