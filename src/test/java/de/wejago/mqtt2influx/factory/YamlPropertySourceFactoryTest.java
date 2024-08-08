package de.wejago.mqtt2influx.factory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class YamlPropertySourceFactoryTest {

    public static final String EXAMPLE_FILE = "mqtt2influx-configuration.yaml";
    @Mock
    private EncodedResource encodedResource;
    @Mock
    private Resource resource;

    private final YamlPropertySourceFactory classToTest = new YamlPropertySourceFactory();

    @Test
    void createPropertySource_noResource() {
        when(encodedResource.getResource()).thenReturn(resource);
        when(resource.exists()).thenReturn(false);
        
        assertThatThrownBy(() -> classToTest.createPropertySource("name", encodedResource))
                .isInstanceOf(FileNotFoundException.class);
    }

    @Test
    void createPropertySource_allFine() throws IOException {
        URL fileUrl = classToTest.getClass().getClassLoader().getResource(EXAMPLE_FILE);
        when(encodedResource.getResource()).thenReturn(new FileUrlResource(fileUrl));

        PropertySource<?> result = classToTest.createPropertySource("name", encodedResource);

        assertThat(result).isNotNull();
    }
}
