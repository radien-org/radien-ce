package io.radien.api.service.ecm.model;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Marco Weiland
 */
public class ContentVersion implements SystemContentVersion {

    private String version;
    private Long majorVersion;
    private Long minorVersion;
    private Long hotfixVersion;

    public ContentVersion(String version){
        if(version == null)
            throw new IllegalArgumentException("Version can not be null");
        if(!version.matches("[0-9]+(\\.[0-9]+)*"))
            throw new IllegalArgumentException("Invalid version format");
        List<String> splitVersion = new ArrayList<String>(Arrays.asList(version.split("\\.")));
        while(splitVersion.size() < 3) {
            splitVersion.add("0");
        }
        majorVersion = Long.parseLong(splitVersion.get(0));
        minorVersion = Long.parseLong(splitVersion.get(1));
        hotfixVersion = Long.parseLong(splitVersion.get(2));
        this.version = MessageFormat.format("{0}.{1}.{2}", majorVersion, minorVersion, hotfixVersion);
    }

    @Override
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public Long getMajorVersion() {
        return majorVersion;
    }

    @Override
    public void setMajorVersion(Long majorVersion) {
        this.majorVersion = majorVersion;
    }

    @Override
    public Long getMinorVersion() {
        return minorVersion;
    }

    @Override
    public void setMinorVersion(Long minorVersion) {
        this.minorVersion = minorVersion;
    }

    @Override
    public Long getHotfixVersion() {
        return hotfixVersion;
    }

    @Override
    public void setHotfixVersion(Long hotfixVersion) {
        this.hotfixVersion = hotfixVersion;
    }

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
