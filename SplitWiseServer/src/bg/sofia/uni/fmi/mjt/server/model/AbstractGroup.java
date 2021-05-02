package bg.sofia.uni.fmi.mjt.server.model;

import java.io.Serializable;

public abstract class AbstractGroup implements Serializable {
    private static final long serialVersionUID = -8713878498061871665L;

    private final String name;

    public AbstractGroup(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
