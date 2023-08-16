package de.wejago.mqtt2influx.utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.lang.reflect.ParameterizedType;

public abstract class AbstractLoggingTest<T> {

    protected MemoryAppender memoryAppender = new MemoryAppender();
    private Logger logger;
    private final Class<T> typeOfT;

    @SuppressWarnings("unchecked")
    public AbstractLoggingTest() {
        typeOfT = (Class<T>)
                ((ParameterizedType) getClass()
                        .getGenericSuperclass())
                        .getActualTypeArguments()[0];
    }

    @BeforeEach
    public void setUp() {
        memoryAppender.reset();
        logger = (Logger) LogManager.getLogger(typeOfT);
        logger.addAppender(memoryAppender);
        logger.setLevel(Level.INFO);
    }

    @AfterEach
    public void tearDown() {
        logger.removeAppender(memoryAppender);
    }
}
