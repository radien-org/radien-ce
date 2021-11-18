package io.radien.api.service.ecm.model;

import java.util.Comparator;
import java.util.Date;

public class VersionableEnterpriseContent extends AbstractECMModel implements SystemVersionableEnterpriseContent {

    private boolean versionable;
    private String versionComment;
    private Date validDate;
    private SystemContentVersion version;
    private String versionableName;

    public VersionableEnterpriseContent() {
        this.version = new ContentVersion("1.0.0");
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

    /**
     * ECM Version name getter
     * @return the versional Name
     */
    public String getVersionableName() {
        return versionableName;
    }

    /**
     * ECM Version name setter
     * @param versionableName the versional Name to set
     */
    public void setVersionableName(String versionableName) {
        this.versionableName = versionableName;
    }

    @Override
    public int compareTo(EnterpriseContent o) {
        return Comparator.comparing(EnterpriseContent::getName)
                .compare(this, o);
    }
}
