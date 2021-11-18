package io.radien.api.service.ecm.model;

public interface SystemMandatoryEnterpriseContent extends EnterpriseContent {
    void setMandatoryView(boolean mandatoryView);

    boolean isMandatoryView();

    void setMandatoryApproval(boolean mandatoryApproval);

    boolean isMandatoryApproval();
}
