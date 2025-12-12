package de.triology.universeadm;

import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;

import static org.assertj.core.api.Assertions.assertThat;


public class DefaultCasRestUrlConnectionFactoryTest {

    @Test
    public void create() throws IOException {
        HttpURLConnection httpURLConnection = new DefaultCasRestUrlConnectionFactory().create("https://example.com");
        assertThat(httpURLConnection).isNotNull();
    }
}
