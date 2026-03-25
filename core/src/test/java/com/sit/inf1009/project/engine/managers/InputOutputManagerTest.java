package com.sit.inf1009.project.engine.managers;

import com.sit.inf1009.project.engine.interfaces.IOListener;
import com.sit.inf1009.project.engine.interfaces.InputHandler;
import com.sit.inf1009.project.engine.interfaces.OutputHandler;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InputOutputManagerTest {

    @Test
    void routesTypedAndGlobalListeners() {
        InputOutputManager io = new InputOutputManager();
        AtomicInteger typedCount = new AtomicInteger();
        AtomicInteger globalCount = new AtomicInteger();

        io.addListener(IOEvent.Type.KEY_PRESSED, e -> typedCount.incrementAndGet());
        io.addGlobalListener(e -> globalCount.incrementAndGet());

        io.handleEvent(new IOEvent(IOEvent.Type.KEY_PRESSED, 42));

        assertEquals(1, typedCount.get());
        assertEquals(1, globalCount.get());
    }

    @Test
    void continuesDispatchWhenTypedListenerThrows() {
        InputOutputManager io = new InputOutputManager();
        AtomicInteger safeListenerCount = new AtomicInteger();

        io.addListener(IOEvent.Type.KEY_PRESSED, e -> {
            throw new RuntimeException("boom");
        });
        io.addListener(IOEvent.Type.KEY_PRESSED, e -> safeListenerCount.incrementAndGet());

        io.handleEvent(new IOEvent(IOEvent.Type.KEY_PRESSED, 1));

        assertEquals(1, safeListenerCount.get());
    }

    @Test
    void continuesOutputDispatchWhenHandlerThrows() {
        InputOutputManager io = new InputOutputManager();
        AtomicInteger safeHandlerCount = new AtomicInteger();

        io.registerOutputHandler(new OutputHandler() {
            @Override
            public void onIOEvent(IOEvent event) {
                throw new RuntimeException("fail output");
            }

            @Override
            public void close() {
            }
        });

        io.registerOutputHandler(new OutputHandler() {
            @Override
            public void onIOEvent(IOEvent event) {
                safeHandlerCount.incrementAndGet();
            }

            @Override
            public void close() {
            }
        });

        io.sendOutput(new IOEvent(IOEvent.Type.SOUND_PLAY, "Droplet"));

        assertEquals(1, safeHandlerCount.get());
    }

    @Test
    void ignoresNullEventsWithoutThrowing() {
        InputOutputManager io = new InputOutputManager();

        io.handleEvent(null);
        io.sendOutput(null);
    }

    @Test
    void shutdownDisablesHandlersClosesOutputAndClearsListeners() {
        InputOutputManager io = new InputOutputManager();

        TestInputHandler input = new TestInputHandler();
        TestOutputHandler output = new TestOutputHandler();
        AtomicInteger listenerCount = new AtomicInteger();
        IOListener listener = e -> listenerCount.incrementAndGet();

        io.registerInputHandler(input);
        io.registerOutputHandler(output);
        io.addListener(IOEvent.Type.KEY_PRESSED, listener);
        io.addGlobalListener(listener);

        io.shutdown();
        io.handleEvent(new IOEvent(IOEvent.Type.KEY_PRESSED, 1));

        assertTrue(input.disabled);
        assertTrue(input.detached);
        assertTrue(output.closed);
        assertEquals(0, listenerCount.get());
    }

    @Test
    void removeTypedAndGlobalListenersStopsFutureCallbacks() {
        InputOutputManager io = new InputOutputManager();
        AtomicInteger typedCount = new AtomicInteger();
        AtomicInteger globalCount = new AtomicInteger();

        IOListener typed = e -> typedCount.incrementAndGet();
        IOListener global = e -> globalCount.incrementAndGet();

        io.addListener(IOEvent.Type.KEY_PRESSED, typed);
        io.addGlobalListener(global);
        io.removeListener(IOEvent.Type.KEY_PRESSED, typed);
        io.removeGlobalListener(global);

        io.handleEvent(new IOEvent(IOEvent.Type.KEY_PRESSED, 123));

        assertEquals(0, typedCount.get());
        assertEquals(0, globalCount.get());
    }

    @Test
    void enableAllAndDisableAllPropagateToInputHandlers() {
        InputOutputManager io = new InputOutputManager();
        TestInputHandler inputA = new TestInputHandler();
        TestInputHandler inputB = new TestInputHandler();
        io.registerInputHandler(inputA);
        io.registerInputHandler(inputB);

        io.disableAll();
        assertFalse(inputA.isEnabled());
        assertFalse(inputB.isEnabled());

        io.enableAll();
        assertTrue(inputA.isEnabled());
        assertTrue(inputB.isEnabled());
    }

    @Test
    void loggerCountsInputAndOutputEvents() {
        InputOutputManager io = new InputOutputManager();
        io.handleEvent(new IOEvent(IOEvent.Type.KEY_PRESSED, 1));
        io.handleEvent(new IOEvent(IOEvent.Type.KEY_PRESSED, 2));
        io.sendOutput(new IOEvent(IOEvent.Type.SOUND_PLAY, "btn_click"));

        assertEquals(2, io.getLoggedEventCount(IOEvent.Type.KEY_PRESSED));
        assertEquals(1, io.getLoggedEventCount(IOEvent.Type.SOUND_PLAY));
    }

    private static class TestInputHandler implements InputHandler {
        private boolean enabled = true;
        private boolean disabled;
        private boolean detached;

        @Override
        public void enable() {
            enabled = true;
        }

        @Override
        public void disable() {
            enabled = false;
            disabled = true;
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public void detach() {
            detached = true;
        }
    }

    private static class TestOutputHandler implements OutputHandler {
        private boolean closed;

        @Override
        public void onIOEvent(IOEvent event) {
        }

        @Override
        public void close() {
            closed = true;
        }
    }
}
