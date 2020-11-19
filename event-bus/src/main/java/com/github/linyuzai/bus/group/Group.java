package com.github.linyuzai.bus.group;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Group {

    public static final Group DEFAULT = Group.exclude();

    private Set<String> groups;

    private Type type;

    private Group() {
    }

    public static Group include(String... groups) {
        return include(Arrays.asList(groups));
    }

    public static Group include(Collection<String> groups) {
        Group group = new Group();
        group.groups = new HashSet<>(groups);
        group.type = Type.INCLUDE;
        return group;
    }

    public static Group exclude(String... groups) {
        return exclude(Arrays.asList(groups));
    }

    public static Group exclude(Collection<String> groups) {
        Group group = new Group();
        group.groups = new HashSet<>(groups);
        group.type = Type.EXCLUDE;
        return group;
    }

    public Collection<String> getGroups() {
        return groups;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        INCLUDE, EXCLUDE
    }
}
