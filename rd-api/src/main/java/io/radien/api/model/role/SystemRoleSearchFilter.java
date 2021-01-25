package io.radien.api.model.role;

/**
 * @author Bruno Gama
 */
public interface SystemRoleSearchFilter {

    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    boolean isExact();

    void setExact(boolean exact);

    boolean isLogicConjunction();

    void setLogicConjunction(boolean logicConjunction);
}
