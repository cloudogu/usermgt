package de.triology.universeadm;

import org.apache.shiro.cas.CasAuthenticationException;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CasRestClientTest {

    @Test
    public void createServiceTicket_sucess() throws Exception {
        // given
        HttpURLConnection tgtConnMock = mock(HttpURLConnection.class);
        HttpURLConnection stConnMock = mock(HttpURLConnection.class);

        OutputStream tgtOutputStream = mock(OutputStream.class);
        OutputStream stOutputStream = mock(OutputStream.class);
        when(tgtConnMock.getOutputStream()).thenReturn(tgtOutputStream);
        when(stConnMock.getOutputStream()).thenReturn(stOutputStream);
        InputStream tgtInputStream = new ByteArrayInputStream("TGT-123-askdjfhlaksdf".getBytes(StandardCharsets.UTF_8));
        InputStream stInputStream = new ByteArrayInputStream("ST-456-qpoweiruqpoweiru".getBytes(StandardCharsets.UTF_8));
        when(tgtConnMock.getInputStream()).thenReturn(tgtInputStream);
        when(stConnMock.getInputStream()).thenReturn(stInputStream);

        when(tgtConnMock.getHeaderField("Location")).thenReturn("https://something/tgt/TGT-123-askdjfhlaksdf");
        when(stConnMock.getHeaderField("Location")).thenReturn("");
        when(tgtConnMock.getResponseCode()).thenReturn(HttpURLConnection.HTTP_CREATED);
        when(stConnMock.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);

        MockUrlConnection mockConnFactory = new MockUrlConnection(tgtConnMock, stConnMock);
        CasRestClient sut = new CasRestClient("https://fqdn.invalid/cas", "https://fqdn.invalid/usermgt", mockConnFactory);

        // when
        String serviceTicket = sut.createServiceTicket("user", "pass");

        // then
        assertThat(serviceTicket)
            .isNotNull()
            .isEqualTo("ST-456-qpoweiruqpoweiru");
    }

    @Test
    public void createServiceTicket_http500_on_tgt_connection_error() throws Exception {
        // given
        HttpURLConnection tgtConnMock = mock(HttpURLConnection.class);
        HttpURLConnection stConnMock = mock(HttpURLConnection.class);

        OutputStream tgtOutputStream = mock(OutputStream.class);
        OutputStream stOutputStream = mock(OutputStream.class);
        when(tgtConnMock.getOutputStream()).thenReturn(tgtOutputStream);
        when(stConnMock.getOutputStream()).thenReturn(stOutputStream);

        when(tgtConnMock.getHeaderField("Location")).thenReturn("https://something/tgt/TGT-123-askdjfhlaksdf");
        when(stConnMock.getHeaderField("Location")).thenReturn("");
        when(tgtConnMock.getResponseCode()).thenReturn(HttpURLConnection.HTTP_INTERNAL_ERROR);

        MockUrlConnection mockConnFactory = new MockUrlConnection(tgtConnMock, stConnMock);
        CasRestClient sut = new CasRestClient("https://fqdn.invalid/cas", "https://fqdn.invalid/usermgt", mockConnFactory);

        try {
            // when
            sut.createServiceTicket("user", "pass");
        } catch (RuntimeException e) {
            // then
            assertThat(e).isNotOfAnyClassIn(CasAuthenticationException.class);
            assertThat(e).isExactlyInstanceOf(RuntimeException.class);
            assertThat(e.getMessage()).isEqualTo("could not create granting ticket, web service returned 500");
        }
    }

    @Test
    public void createServiceTicket_http500_on_tgt_authentication_failure() throws Exception {
        // given
        HttpURLConnection tgtConnMock = mock(HttpURLConnection.class);
        HttpURLConnection stConnMock = mock(HttpURLConnection.class);

        OutputStream tgtOutputStream = mock(OutputStream.class);
        OutputStream stOutputStream = mock(OutputStream.class);
        when(tgtConnMock.getOutputStream()).thenReturn(tgtOutputStream);
        when(stConnMock.getOutputStream()).thenReturn(stOutputStream);
        InputStream tgtInputStream = new ByteArrayInputStream("TGT-123-askdjfhlaksdf".getBytes(StandardCharsets.UTF_8));
        InputStream stInputStream = new ByteArrayInputStream("ST-456-qpoweiruqpoweiru".getBytes(StandardCharsets.UTF_8));
        when(tgtConnMock.getInputStream()).thenReturn(tgtInputStream);
        when(stConnMock.getInputStream()).thenReturn(stInputStream);

        when(tgtConnMock.getHeaderField("Location")).thenReturn("https://something/tgt/TGT-123-askdjfhlaksdf");
        when(stConnMock.getHeaderField("Location")).thenReturn("");
        when(tgtConnMock.getResponseCode()).thenReturn(HttpURLConnection.HTTP_UNAUTHORIZED);

        MockUrlConnection mockConnFactory = new MockUrlConnection(tgtConnMock, stConnMock);
        CasRestClient sut = new CasRestClient("https://fqdn.invalid/cas", "https://fqdn.invalid/usermgt", mockConnFactory);

        try {
            // when
            sut.createServiceTicket("user", "pass");
        } catch (CasAuthenticationException e) {
            // then
            assertThat(e.getMessage()).isEqualTo("could not create granting ticket, web service returned 401");
        }
    }

    @Test
    public void createServiceTicket_throws_runtime_exception_that_is_not_CasAuthenticationExc() throws Exception {        // given
        HttpURLConnection tgtConnMock = mock(HttpURLConnection.class);

        when(tgtConnMock.getOutputStream()).thenThrow(new IOException("whoopsie"));


        when(tgtConnMock.getHeaderField("Location")).thenReturn("https://something/tgt/TGT-123-askdjfhlaksdf");
        when(tgtConnMock.getResponseCode()).thenReturn(HttpURLConnection.HTTP_UNAUTHORIZED);

        MockUrlConnection mockConnFactory = new MockUrlConnection(tgtConnMock, null);
        CasRestClient sut = new CasRestClient("https://fqdn.invalid/cas", "https://fqdn.invalid/usermgt", mockConnFactory);

        try {
            // when
            sut.createServiceTicket("user", "pass");
        } catch (RuntimeException e) {
            // then
            assertThat(e).isExactlyInstanceOf(RuntimeException.class);
            assertThat(e.getMessage()).isEqualTo("cas validation failed");
        }
    }

    @Test
    public void createServiceTicket_fails_on_missing_TGT_location() throws Exception {
        // given
        HttpURLConnection tgtConnMock = mock(HttpURLConnection.class);
        HttpURLConnection stConnMock = mock(HttpURLConnection.class);

        OutputStream tgtOutputStream = mock(OutputStream.class);
        OutputStream stOutputStream = mock(OutputStream.class);
        when(tgtConnMock.getOutputStream()).thenReturn(tgtOutputStream);
        when(stConnMock.getOutputStream()).thenReturn(stOutputStream);
        InputStream tgtInputStream = new ByteArrayInputStream("TGT-123-askdjfhlaksdf".getBytes(StandardCharsets.UTF_8));
        InputStream stInputStream = new ByteArrayInputStream("ST-456-qpoweiruqpoweiru".getBytes(StandardCharsets.UTF_8));
        when(tgtConnMock.getInputStream()).thenReturn(tgtInputStream);
        when(stConnMock.getInputStream()).thenReturn(stInputStream);

        when(tgtConnMock.getHeaderField("Location")).thenReturn(null);
        when(stConnMock.getHeaderField("Location")).thenReturn("");
        when(tgtConnMock.getResponseCode()).thenReturn(HttpURLConnection.HTTP_CREATED);
        when(stConnMock.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);

        MockUrlConnection mockConnFactory = new MockUrlConnection(tgtConnMock, stConnMock);
        CasRestClient sut = new CasRestClient("https://fqdn.invalid/cas", "https://fqdn.invalid/usermgt", mockConnFactory);

        // when
        try {
            sut.createServiceTicket("user", "pass");
        } catch (CasAuthenticationException e) {
            // then
            assertThat(e.getMessage()).isEqualTo("could not create granting ticket, web service returned no location header");
        }
    }

    @Test
    public void createServiceTicket_fails_on_ST_creation() throws Exception {
        // given
        HttpURLConnection tgtConnMock = mock(HttpURLConnection.class);
        HttpURLConnection stConnMock = mock(HttpURLConnection.class);

        OutputStream tgtOutputStream = mock(OutputStream.class);
        OutputStream stOutputStream = mock(OutputStream.class);
        when(tgtConnMock.getOutputStream()).thenReturn(tgtOutputStream);
        when(stConnMock.getOutputStream()).thenReturn(stOutputStream);
        InputStream tgtInputStream = new ByteArrayInputStream("TGT-123-askdjfhlaksdf".getBytes(StandardCharsets.UTF_8));
        InputStream stInputStream = new ByteArrayInputStream("ST-456-qpoweiruqpoweiru".getBytes(StandardCharsets.UTF_8));
        when(tgtConnMock.getInputStream()).thenReturn(tgtInputStream);
        when(stConnMock.getInputStream()).thenReturn(stInputStream);

        when(tgtConnMock.getHeaderField("Location")).thenReturn("https://something/tgt/TGT-123-askdjfhlaksdf");
        when(stConnMock.getHeaderField("Location")).thenReturn("");
        when(tgtConnMock.getResponseCode()).thenReturn(HttpURLConnection.HTTP_CREATED);
        when(stConnMock.getResponseCode()).thenReturn(HttpURLConnection.HTTP_UNAUTHORIZED);

        MockUrlConnection mockConnFactory = new MockUrlConnection(tgtConnMock, stConnMock);
        CasRestClient sut = new CasRestClient("https://fqdn.invalid/cas", "https://fqdn.invalid/usermgt", mockConnFactory);

        // when
        try {
            sut.createServiceTicket("user", "pass");
        }  catch (CasAuthenticationException e) {
        // then
            assertThat(e.getMessage()).isEqualTo("could not create service ticket, web service returned 401");
        }
    }

    @Test
    public void extractTgtFromLocation_returnsTgt() {
        CasRestClient sut = new CasRestClient(null, null, null);
        String actual = sut.extractTgtFromLocation("something/tgt-test-123");
        assertThat(actual).isEqualTo("tgt-test-123");
    }

    @Test
    public void extractTgtFromLocation_throwsCasException() {
        CasRestClient sut = new CasRestClient(null, null, null);
        try {
            sut.extractTgtFromLocation("");
        } catch (CasAuthenticationException e) {
            assertThat(e.getMessage()).isEqualTo("could not create granting ticket, web service returned invalid location header");
        }
    }

    @Test
    public void publicConstructorShouldReturnValidObject() {
        CasRestClient sut = new CasRestClient(null, null);
        assertThat(sut).isNotNull();
    }
}

class MockUrlConnection implements CasRestUrlConnectionFactory {
    private final HttpURLConnection mockTgtConn;
    private final HttpURLConnection mockStConn;
    private int createCounter = 0;

    public MockUrlConnection(HttpURLConnection mockTgtConn, HttpURLConnection mockStConn) {
        this.mockTgtConn = mockTgtConn;
        this.mockStConn = mockStConn;
    }

    @Override
    public HttpURLConnection create(String url) throws IOException {
        switch (createCounter) {
            case 0:
                createCounter++;
                return this.mockTgtConn;
            case 1:
                createCounter++;
                return this.mockStConn;
            default:
                createCounter++;
                throw new UnsupportedOperationException("unsupported connection creation count of " + createCounter);
        }
    }
}
