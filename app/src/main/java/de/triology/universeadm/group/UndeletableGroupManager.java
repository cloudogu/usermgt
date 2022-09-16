package de.triology.universeadm.group;

import java.util.ArrayList;
import java.util.List;

public class UndeletableGroupManager {
    public static List<String> getNonDeleteClassList(){
        List<String> groups = new ArrayList<>();
        String adminGroup = System.getenv("ADMIN_GROUP");
        String cesManagerGroup = System.getenv("CES_MANAGER_GROUP");
        if(adminGroup.equals("") || cesManagerGroup.equals("")){
            throw new NullPointerException();
        }
        groups.add(adminGroup);
        groups.add(cesManagerGroup);
        return groups;
    }

    public static boolean isGroupUndeletable(String group){
        return getNonDeleteClassList().contains(group);
    }

}
