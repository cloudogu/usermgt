package de.triology.universeadm.group;

import de.triology.universeadm.configuration.ApplicationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

public class UndeletableGroupManager {

    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ApplicationConfiguration applicationConfiguration;

    public UndeletableGroupManager(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    public List<String> getNonDeleteClassList() {
        List<String> groups = new ArrayList<>();
        String adminGroup = applicationConfiguration.getAdminGroup();
        String cesManagerGroup = applicationConfiguration.getManagerGroup();
        // fallback to defaults if not set
        if (adminGroup == null || "".equals(adminGroup)) {
            logger.warn("Env variable ADMIN_GROUP not set. Falling back to default \"admin\"");
            adminGroup = "admin";
        }
        if (cesManagerGroup == null || "".equals(cesManagerGroup)) {
            logger.warn("Env variable CES_MANAGER_GROUP not set. Falling back to default \"cesManager\"");
            cesManagerGroup = "cesManager";
        }
        groups.add(adminGroup);
        groups.add(cesManagerGroup);
        return groups;
    }

    public boolean isGroupUndeletable(String group) {
        return getNonDeleteClassList().contains(group);
    }

}
