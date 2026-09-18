package de.triology.universeadm.dogu;

import com.cloudogu.cescontrol.dogu.DoguAdministrationGrpc;
import com.cloudogu.cescontrol.dogu.DoguListRequest;
import com.cloudogu.cescontrol.dogu.DoguListResponse;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class CesControlDoguProviderTest {
    private Server server;
    private String previousHost;
    private String previousPort;

    @Before
    public void setUp() throws Exception {
        previousHost = System.getProperty(CesControlDoguProvider.HOST_PROPERTY);
        previousPort = System.getProperty(CesControlDoguProvider.PORT_PROPERTY);
        server = ServerBuilder.forPort(0)
                .addService(new DoguAdministrationGrpc.DoguAdministrationImplBase() {
                    @Override
                    public void getDoguList(DoguListRequest request,
                                            StreamObserver<DoguListResponse> observer) {
                        observer.onNext(DoguListResponse.newBuilder()
                                .addDogus(com.cloudogu.cescontrol.dogu.Dogu.newBuilder()
                                        .setName("jenkins")
                                        .setDisplayName("Jenkins")
                                        .setCategory("Development")
                                        .addTags("ci")
                                        .addTags("build"))
                                .addDogus(com.cloudogu.cescontrol.dogu.Dogu.newBuilder()
                                        .setName("scm")
                                        .setDisplayName("SCM-Manager"))
                                .build());
                        observer.onCompleted();
                    }
                })
                .build()
                .start();
        System.setProperty(CesControlDoguProvider.HOST_PROPERTY, "localhost");
        System.setProperty(CesControlDoguProvider.PORT_PROPERTY, String.valueOf(server.getPort()));
    }

    @After
    public void tearDown() throws Exception {
        server.shutdownNow().awaitTermination();
        restoreProperty(CesControlDoguProvider.HOST_PROPERTY, previousHost);
        restoreProperty(CesControlDoguProvider.PORT_PROPERTY, previousPort);
    }

    @Test
    public void shouldMapCesControlResponseToDogus() {
        List<Dogu> dogus = new CesControlDoguProvider().getDogus();

        assertEquals(2, dogus.size());
        assertEquals("jenkins", dogus.get(0).getName());
        assertEquals("Jenkins", dogus.get(0).getDisplayName());
        assertEquals("Development", dogus.get(0).getCategory());
        assertEquals(2, dogus.get(0).getTags().size());
        assertEquals("ci", dogus.get(0).getTags().get(0));
        assertEquals("scm", dogus.get(1).getName());
        assertEquals("", dogus.get(1).getCategory());
    }

    private void restoreProperty(String name, String value) {
        if (value == null) {
            System.clearProperty(name);
        } else {
            System.setProperty(name, value);
        }
    }
}
