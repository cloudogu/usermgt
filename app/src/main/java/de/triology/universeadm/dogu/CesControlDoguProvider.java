package de.triology.universeadm.dogu;

import com.cloudogu.cescontrol.dogu.DoguAdministrationGrpc;
import com.cloudogu.cescontrol.dogu.DoguListRequest;
import com.cloudogu.cescontrol.dogu.DoguListResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CesControlDoguProvider implements DoguProvider {
    static final String HOST_PROPERTY = "ces.control.host";
    static final String PORT_PROPERTY = "ces.control.port";
    static final String DEFAULT_HOST = "localhost";
    static final int DEFAULT_PORT = 50051;
    static final long TIMEOUT_SECONDS = 10;

    @Override
    public List<Dogu> getDogus() {
        String host = System.getProperty(HOST_PROPERTY, DEFAULT_HOST);
        int port = Integer.parseInt(System.getProperty(PORT_PROPERTY, String.valueOf(DEFAULT_PORT)));
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        try {
            DoguListResponse response = DoguAdministrationGrpc.newBlockingStub(channel)
                    .withDeadlineAfter(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .getDoguList(DoguListRequest.getDefaultInstance());

            List<Dogu> dogus = new ArrayList<>();
            for (com.cloudogu.cescontrol.dogu.Dogu dogu : response.getDogusList()) {
                dogus.add(new Dogu(dogu.getName(), dogu.getDisplayName(), dogu.getTagsList()));
            }
            return dogus;
        } finally {
            channel.shutdownNow();
        }
    }
}
