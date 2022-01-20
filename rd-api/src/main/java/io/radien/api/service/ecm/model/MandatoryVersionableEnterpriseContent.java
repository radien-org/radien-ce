package io.radien.api.service.ecm.model;

import java.util.Comparator;
import java.util.Date;

public class MandatoryVersionableEnterpriseContent extends AbstractECMModel implements SystemMandatoryEnterpriseContent, SystemVersionableEnterpriseContent {

    private boolean versionable;
    private String versionComment;
    private Date validDate;
    private SystemContentVersion version;
    private boolean mandatoryView;
    private boolean mandatoryApprove;

    public MandatoryVersionableEnterpriseContent() {
        this.version = new ContentVersion("1.0.0");
    }

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

    /**
     * Checks if the requested ecm is versional or not
     * @return the versional
     */
    public boolean isVersionable() {
        return versionable;
    }

    /**
     * Sets a new version to the active ecm
     * @param versionable the versional to set
     */
    public void setVersionable(boolean versionable) {
        this.versionable = versionable;
    }

    /**
     * Retrieves ecm version comment
     * @return the versionComment
     */
    public String getVersionComment() {
        return versionComment;
    }

    /**
     * Sets the ecm version comment
     * @param versionComment the versionComment to set
     */
    public void setVersionComment(String versionComment) {
        this.versionComment = versionComment;
    }

    /**
     * Ecm valid date getter
     * @return the validDate
     */
    public Date getValidDate() {
        return validDate;
    }

    /**
     * ECM set valid date
     * @param validDate the validDate to set
     */
    public void setValidDate(Date validDate) {
        this.validDate = validDate;
    }

    /**
     * ECM Version getter
     * @return the version
     */
    public SystemContentVersion getVersion() {
        return version;
    }

    /**
     * ECM version setter
     * @param version the version to set
     */
    public void setVersion(SystemContentVersion version) {
        this.version = version;
    }

    @Override
    public int compareTo(EnterpriseContent o) {
        return Comparator.comparing(EnterpriseContent::getName)
                .compare(this, o);
    }
}
