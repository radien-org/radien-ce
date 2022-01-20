package io.radien.api.service.ecm.model;

import java.util.Date;

public interface SystemVersionableEnterpriseContent extends EnterpriseContent {

    /**
     * Checks if ECM is versional
     * @return true if ecm is verional
     */
    boolean isVersionable();

    /**
     * ECM versional setter
     * @param versionable to be set
     */
    void setVersionable(boolean versionable);

    /**
     * ECM version comment getter
     * @return the ecm version comment
     */
    String getVersionComment();

    /**
     * ECM version comment setter
     * @param versionComment to be set
     */
    void setVersionComment(String versionComment);

    /**
     * ECM valid date getter
     * @return the ecm valid date
     */
    Date getValidDate();

    /**
     * ECM valid date setter
     * @param validDate to be set
     */
    void setValidDate(Date validDate);

    /**
     * ECM version getter
     * @return the ecm version
     */
    SystemContentVersion getVersion();

    /**
     * ECM version setter
     * @param version to be set
     */
    void setVersion(SystemContentVersion version);

}
