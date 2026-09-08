package de.triology.universeadm.dogu;

import com.cloudogu.cescontrol.dogu.DoguAdministrationGrpc;
import com.cloudogu.cescontrol.dogu.DoguListRequest;
import com.cloudogu.cescontrol.dogu.DoguListResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CesControlDoguProvider implements DoguProvider {
    private static final Logger logger = LoggerFactory.getLogger(CesControlDoguProvider.class);
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

            logger.debug("ces-control GetDoguList response from {}:{}: {}", host, port, response);
            List<Dogu> dogus = new ArrayList<>();
            for (com.cloudogu.cescontrol.dogu.Dogu dogu : response.getDogusList()) {
                logger.debug("ces-control dogu: name='{}', displayName='{}', category='{}', tags={}",
                        dogu.getName(), dogu.getDisplayName(), dogu.getCategory(), dogu.getTagsList());
                if (dogu.getCategory().isEmpty()) {
                    logger.warn("ces-control returned an empty category for dogu '{}' from {}:{}",
                            dogu.getName(), host, port);
                }
                dogus.add(new Dogu(dogu.getName(), dogu.getDisplayName(), dogu.getTagsList(), dogu.getCategory()));
            }
            return dogus;
        } finally {
            channel.shutdownNow();
        }
    }
}
