package io.radien.ms.usermanagement.client.entities;

import io.radien.api.security.TokensPlaceHolder;
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

@Default @Named
public class GlobalHeaders implements ClientHeadersFactory {
    @Inject
    private TokensPlaceHolder tokensPlaceHolder;

    @Override
    public MultivaluedMap<String, String> update(MultivaluedMap<String, String> incomingHeaders, MultivaluedMap<String, String> outgoingHeaders) {
        tokensPlaceHolder =  CDI.current().select(TokensPlaceHolder.class).get();
        MultivaluedMap<String, String> result = new MultivaluedHashMap<>();
        if(tokensPlaceHolder == null) {
            result.add(HttpHeaders.AUTHORIZATION.toString(),"Bearer " +"superSeed");
            return result;
        }
        result.add(HttpHeaders.AUTHORIZATION.toString(),"Bearer " +tokensPlaceHolder.getAccessToken());
        result.putAll(incomingHeaders);
        result.putAll(outgoingHeaders);

        return result;
    }
}
