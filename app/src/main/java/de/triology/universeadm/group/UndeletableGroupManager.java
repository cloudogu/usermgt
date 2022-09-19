package de.triology.universeadm.group;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

public class UndeletableGroupManager {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static List<String> getNonDeleteClassList(){
        List<String> groups = new ArrayList<>();
        String adminGroup = System.getenv("ADMIN_GROUP");
        String cesManagerGroup = System.getenv("CES_MANAGER_GROUP");
        // fallback to defaults if not set
        if(adminGroup == null || "".equals(adminGroup)){
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

    public static boolean isGroupUndeletable(String group){
        return getNonDeleteClassList().contains(group);
    }

}
