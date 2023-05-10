package io.radien.api.service.ecm.model;

import java.util.Comparator;

public class MandatoryEnterpriseContent extends AbstractECMModel implements SystemMandatoryEnterpriseContent {
    private boolean mandatoryView;
    private boolean mandatoryApprove;


    @Override
    public void setMandatoryView(boolean mandatoryView) {
        this.mandatoryView = mandatoryView;
    }

    @Override
    public boolean isMandatoryView() {
        return mandatoryView;
    }

    @Override
    public void setMandatoryApproval(boolean mandatoryApproval) {
        this.mandatoryApprove = mandatoryApproval;
    }

    @Override
    public boolean isMandatoryApproval() {
        return mandatoryApprove;
    }

    @Override
    public int compareTo(EnterpriseContent o) {
        return Comparator.comparing(EnterpriseContent::getName)
                .compare(this, o);
    }
}
