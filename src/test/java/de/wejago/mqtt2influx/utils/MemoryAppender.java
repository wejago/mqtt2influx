package de.wejago.mqtt2influx.utils;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.ErrorHandler;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MemoryAppender implements Appender {
    private final List<LogEvent> logMessages = new ArrayList<>();

    public void assertContains(String text) {
        assertThat(logMessages.stream()
                .anyMatch(event -> event.getMessage().getFormattedMessage().contains(text))).isTrue();
    }

    public void reset() {
        logMessages.clear();
    }

    @Override
    public void append(LogEvent event) {
        logMessages.add(event);
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Layout<? extends Serializable> getLayout() {
        return null;
    }

    @Override
    public boolean ignoreExceptions() {
        return false;
    }

    @Override
    public ErrorHandler getHandler() {
        return null;
    }

    @Override
    public void setHandler(ErrorHandler handler) {

    }

    @Override
    public State getState() {
        return null;
    }

    @Override
    public void initialize() {

    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public boolean isStarted() {
        return true;
    }

    @Override
    public boolean isStopped() {
        return false;
    }
}
