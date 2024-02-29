package de.triology.universeadm;

import de.triology.universeadm.mapping.MappingHandler;
import de.triology.universeadm.group.Group;

public class UniqueGroupNameConstraint extends Constraint<Group> {
    private final MappingHandler<Group> mapping;

    public UniqueGroupNameConstraint(final MappingHandler<Group> mapping) {
        this.mapping = mapping;
    }

    @Override
    public boolean violatedBy(final Group group, final Category currentCategory) {
        if (currentCategory != Category.CREATE) {
            return false;
        }

        final Group existingGroup = this.mapping.get(group.getName());

        return existingGroup != null;
    }

    @Override
    public ID getUniqueID() {
        return ID.UNIQUE_GROUP_NAME;
    }

}
