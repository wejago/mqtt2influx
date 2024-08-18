package de.wejago.mqtt2influx.factory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

public class YamlPropertySourceFactory implements PropertySourceFactory {

    @NotNull
    @Override
    public PropertySource<?> createPropertySource(@Nullable String name, EncodedResource encodedResource)
            throws IOException {
        final YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        if(encodedResource.getResource().exists()) {
            factory.setResources(encodedResource.getResource());
            return new PropertiesPropertySource(Objects.requireNonNull(encodedResource.getResource().getFilename()),
                    Objects.requireNonNull(factory.getObject()));
        } else {
            throw new FileNotFoundException();
        }
    }
}
