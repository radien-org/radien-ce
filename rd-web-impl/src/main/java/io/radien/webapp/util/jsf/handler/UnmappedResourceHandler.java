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

package io.radien.webapp.util.jsf.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Map.Entry;

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.application.ResourceHandlerWrapper;
import javax.faces.application.ResourceWrapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

/**
 * @author MarcoWeiland
 *
 */
public class UnmappedResourceHandler extends ResourceHandlerWrapper {

    protected static final Logger log = LoggerFactory.getLogger(UnmappedResourceHandler.class);

    private ResourceHandler wrapped;

    public UnmappedResourceHandler(ResourceHandler wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public Resource createResource(final String resourceName, final String libraryName) {
        final Resource resource = super.createResource(resourceName, libraryName);

        if (resource == null) {
            return null;
        }

        return new ResourceWrapper() {

            @Override
            public String getRequestPath() {
                ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
                String mapping = externalContext.getRequestServletPath();

                if (externalContext.getRequestPathInfo() == null) {
                    mapping = mapping.substring(mapping.lastIndexOf('.'));
                }

                String path = super.getRequestPath();

                if (mapping.charAt(0) == '/') {
                    return path.replaceFirst(mapping, "");
                }
                else if (path.contains("?")) {
                    return path.replace(mapping + "?", "?");
                }
                else {
                    return path.substring(0, path.length() - mapping.length());
                }
            }

            @Override // Necessary because this is missing in ResourceWrapper (will be fixed in JSF 2.2).
            public String getResourceName() {
                return resource.getResourceName();
            }

            @Override // Necessary because this is missing in ResourceWrapper (will be fixed in JSF 2.2).
            public String getLibraryName() {
                return resource.getLibraryName();
            }

            @Override // Necessary because this is missing in ResourceWrapper (will be fixed in JSF 2.2).
            public String getContentType() {
                return resource.getContentType();
            }

            @Override
            public Resource getWrapped() {
                return resource;
            }
        };
    }

    @Override
    public boolean isResourceRequest(FacesContext context) {
        return ResourceHandler.RESOURCE_IDENTIFIER.equals(context.getExternalContext().getRequestServletPath());
    }

    @Override
    public void handleResourceRequest(FacesContext context) throws IOException {
        ExternalContext externalContext = context.getExternalContext();
        String resourceName = externalContext.getRequestPathInfo();
        String libraryName = externalContext.getRequestParameterMap().get("ln");
        Resource resource = context.getApplication().getResourceHandler().createResource(resourceName, libraryName);

        if (resource == null) {
            super.handleResourceRequest(context);
            return;
        }

        if (!resource.userAgentNeedsUpdate(context)) {
            externalContext.setResponseStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return;
        }

        externalContext.setResponseContentType(resource.getContentType());

        for (Entry<String, String> header : resource.getResponseHeaders().entrySet()) {
            externalContext.setResponseHeader(header.getKey(), header.getValue());
        }

        ReadableByteChannel input = null;
        WritableByteChannel output = null;

        try {
            input = Channels.newChannel(resource.getInputStream());
            output = Channels.newChannel(externalContext.getResponseOutputStream());

            for (ByteBuffer buffer = ByteBuffer.allocateDirect(10240); input.read(buffer) != -1; buffer.clear()) {
                output.write((ByteBuffer) buffer.flip());
            }
        }
        finally {
            if (output != null) try { output.close(); } catch (IOException ignore) {log.error(ignore.getMessage());}
            if (input != null) try { input.close(); } catch (IOException ignore) {log.error(ignore.getMessage());}
        }
    }

    @Override
    public ResourceHandler getWrapped() {
        return wrapped;
    }

}