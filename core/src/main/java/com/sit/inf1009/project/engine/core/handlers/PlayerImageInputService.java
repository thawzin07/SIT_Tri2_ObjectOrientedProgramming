package com.sit.inf1009.project.engine.core.handlers;

import com.sit.inf1009.project.engine.interfaces.IOListener;
import com.sit.inf1009.project.engine.managers.IOEvent;
import com.sit.inf1009.project.engine.managers.InputOutputManager;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

/**
 * IO-side service that reacts to player image upload requests and emits
 * image selection result events back through InputOutputManager.
 */
public class PlayerImageInputService implements IOListener {

    @FunctionalInterface
    public interface ImageFilePicker {
        String chooseImagePath() throws Exception;
    }

    private final InputOutputManager ioManager;
    private final ImageFilePicker picker;

    public PlayerImageInputService(InputOutputManager ioManager) {
        this(ioManager, createDefaultPicker());
    }

    public PlayerImageInputService(InputOutputManager ioManager, ImageFilePicker picker) {
        if (ioManager == null) {
            throw new IllegalArgumentException("InputOutputManager cannot be null");
        }
        if (picker == null) {
            throw new IllegalArgumentException("ImageFilePicker cannot be null");
        }

        this.ioManager = ioManager;
        this.picker = picker;
        this.ioManager.addListener(IOEvent.Type.PLAYER_IMAGE_UPLOAD_REQUEST, this);
    }

    @Override
    public void onIOEvent(IOEvent event) {
        if (event == null || event.getType() != IOEvent.Type.PLAYER_IMAGE_UPLOAD_REQUEST) {
            return;
        }

        try {
            String selectedPath = picker.chooseImagePath();
            if (selectedPath == null || selectedPath.isBlank()) {
                ioManager.handleEvent(new IOEvent(IOEvent.Type.PLAYER_IMAGE_SELECTION_FAILED, "cancelled"));
                return;
            }

            ioManager.handleEvent(new IOEvent(IOEvent.Type.PLAYER_IMAGE_SELECTED, selectedPath));
        } catch (Exception e) {
            String reason = (e.getMessage() == null || e.getMessage().isBlank())
                    ? "image selection failed"
                    : e.getMessage();
            ioManager.handleEvent(new IOEvent(IOEvent.Type.PLAYER_IMAGE_SELECTION_FAILED, reason));
        }
    }

    private static ImageFilePicker createDefaultPicker() {
        return () -> {
            if (GraphicsEnvironment.isHeadless()) {
                throw new IllegalStateException("file chooser is unavailable in headless mode");
            }

            AtomicReference<String> selectedPath = new AtomicReference<>();
            AtomicReference<Exception> pickerError = new AtomicReference<>();

            Runnable task = () -> {
                try {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setDialogTitle("Choose Player Image");
                    chooser.setFileFilter(new FileNameExtensionFilter("Images (*.png, *.jpg, *.jpeg)", "png", "jpg", "jpeg"));
                    chooser.setAcceptAllFileFilterUsed(false);

                    int result = chooser.showOpenDialog(null);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File file = chooser.getSelectedFile();
                        if (file != null) {
                            selectedPath.set(file.getAbsolutePath());
                        }
                    }
                } catch (Exception e) {
                    pickerError.set(e);
                }
            };

            if (SwingUtilities.isEventDispatchThread()) {
                task.run();
            } else {
                SwingUtilities.invokeAndWait(task);
            }

            if (pickerError.get() != null) {
                throw pickerError.get();
            }
            return selectedPath.get();
        };
    }
}
