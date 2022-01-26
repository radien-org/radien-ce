/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.radien.api.service.ecm.model;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ECM Content Version object class
 *
 * @author Marco Weiland
 */
public class ContentVersion implements SystemContentVersion {

    private String version;
    private Long majorVersion;
    private Long minorVersion;
    private Long hotfixVersion;

    public ContentVersion() {
    }

    /**
     * ECM Content Version constructor
     * @param version of the ecm content
     */
    public ContentVersion(String version){
        if(version == null)
            throw new IllegalArgumentException("Version can not be null");
        if(!version.matches("[0-9]+(\\.[0-9]+){2}"))
            throw new IllegalArgumentException("Invalid version format");
        List<String> splitVersion = new ArrayList<>(Arrays.asList(version.split("\\.")));
        while(splitVersion.size() < 3) {
            splitVersion.add("0");
        }
        majorVersion = Long.parseLong(splitVersion.get(0));
        minorVersion = Long.parseLong(splitVersion.get(1));
        hotfixVersion = Long.parseLong(splitVersion.get(2));
        this.version = MessageFormat.format("{0}.{1}.{2}", majorVersion, minorVersion, hotfixVersion);
    }

    /**
     * ECM Content version getter version
     * @return ecm content version version
     */
    @Override
    public String getVersion() {
        return version;
    }

    /**
     * ECM Content version version setter
     * @param version to be set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * ECM Content version getter major version
     * @return ecm content version major version
     */
    @Override
    public Long getMajorVersion() {
        return majorVersion;
    }

    /**
     * ECM Content version major version setter
     * @param majorVersion to be set
     */
    @Override
    public void setMajorVersion(Long majorVersion) {
        this.majorVersion = majorVersion;
    }

    /**
     * ECM Content version getter minor version
     * @return ecm content version minor version
     */
    @Override
    public Long getMinorVersion() {
        return minorVersion;
    }

    /**
     * ECM Content version minor version setter
     * @param minorVersion to be set
     */
    @Override
    public void setMinorVersion(Long minorVersion) {
        this.minorVersion = minorVersion;
    }

    /**
     * ECM Content version getter hot fix version
     * @return ecm content version hot fix version
     */
    @Override
    public Long getHotfixVersion() {
        return hotfixVersion;
    }

    /**
     * ECM Content version hot fix version setter
     * @param hotfixVersion to be set
     */
    @Override
    public void setHotfixVersion(Long hotfixVersion) {
        this.hotfixVersion = hotfixVersion;
    }

    /**
     * Compares this object with the specified object for order. Returns a negative integer, zero,
     * or a positive integer as this object is less than, equal to, or greater than the specified object.
     * @param that the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(SystemContentVersion that) {
        if(that == null)
            return 1;
        String[] thisParts = this.getVersion().split("\\.");
        String[] thatParts = that.getVersion().split("\\.");
        int length = Math.max(thisParts.length, thatParts.length);
        for(int i = 0; i < length; i++) {
            int thisPart = i < thisParts.length ?
                    Integer.parseInt(thisParts[i]) : 0;
            int thatPart = i < thatParts.length ?
                    Integer.parseInt(thatParts[i]) : 0;
            if(thisPart < thatPart)
                return -1;
            if(thisPart > thatPart)
                return 1;
        }
        return 0;
    }
}
