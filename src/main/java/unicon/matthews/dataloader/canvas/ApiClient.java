package unicon.matthews.dataloader.canvas;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import unicon.matthews.dataloader.canvas.exception.CanvasDataConfigurationException;
import unicon.matthews.dataloader.canvas.exception.UnexpectedApiResponseException;
import unicon.matthews.dataloader.canvas.model.CanvasDataDump;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.ClientResponse;

public class ApiClient {

    private final RestUtils rest;

    public ApiClient(final String host, final String key, final String secret) {
        this.rest = new RestUtils(host, key, secret);
    }

    public List<CanvasDataDump> getDumps() throws CanvasDataConfigurationException, UnexpectedApiResponseException {
        final ClientResponse response = rest.makeApiCall("/api/account/self/dump", 200);

        //NOTE: Using object mapper because getting the 'generic' type didn't work well :)
        ObjectMapper mapper = new ObjectMapper();
        String json = response.getEntity(String.class);
        
        // JSON from String to Object
        CanvasDataDump[] obj = null;
        try {
            obj = mapper.readValue(json, CanvasDataDump[].class);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        final List<CanvasDataDump> dumps = Arrays.asList(obj);

        // final List<CanvasDataDump> dumps = response.getEntity();
        for (final CanvasDataDump dump : dumps) {
            dump.setRestUtils(rest);
        }
        return dumps;
    }

    public CanvasDataDump getLatestDump() throws CanvasDataConfigurationException, UnexpectedApiResponseException {
        final ClientResponse response = rest.makeApiCall("/api/account/self/file/latest", 200);

        //NOTE: Using object mapper because getting the 'generic' type didn't work well :)
        ObjectMapper mapper = new ObjectMapper();
        String json = response.getEntity(String.class);
        CanvasDataDump obj = null;
        try {
            obj = mapper.readValue(json, CanvasDataDump.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        obj.setRestUtils(rest);
        return obj;
    }

    public CanvasDataDump getDump(final String id)
            throws CanvasDataConfigurationException, UnexpectedApiResponseException {
        final ClientResponse response = rest.makeApiCall("/api/account/self/file/byDump/" + id, 200);
        final CanvasDataDump dump = response.getEntity(CanvasDataDump.class);
        dump.setRestUtils(rest);
        return dump;
    }

}
