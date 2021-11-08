package io.radien.security.openid.model;

import java.io.Serializable;

public interface GrantedAuthority extends Serializable {

    String getAuthority();

}

