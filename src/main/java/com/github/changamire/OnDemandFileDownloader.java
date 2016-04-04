package com.github.changamire;

import java.io.IOException;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;

public class OnDemandFileDownloader extends FileDownloader {

    public interface OnDemandStreamResource extends StreamSource {
        String getFilename ();
    }

    private static final long serialVersionUID = 1L;
    private final StreamResource onDemandStreamResource;

    public OnDemandFileDownloader (StreamResource onDemandStreamResource) {
        super(new StreamResource(onDemandStreamResource.getStreamSource(), ""));
        this.onDemandStreamResource = onDemandStreamResource;
    }

    @Override
    public boolean handleConnectorRequest (VaadinRequest request, VaadinResponse response, String path)
            throws IOException {
        //getResource().setFilename("geo.json");
        return super.handleConnectorRequest(request, response, path);
    }

    private StreamResource getResource () {
        return (StreamResource) this.getResource("dl");
    }

}