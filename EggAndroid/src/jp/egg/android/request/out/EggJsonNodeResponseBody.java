package jp.egg.android.request.out;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;


public abstract class EggJsonNodeResponseBody extends EggResponseBody<com.fasterxml.jackson.databind.JsonNode> {


    //イベント系


    public static com.fasterxml.jackson.databind.JsonNode parseToJsonNoe(NetworkResponse response) throws JsonProcessingException, IOException {

        String jsonString = parseToString(response);

        ObjectMapper om = new ObjectMapper();
        com.fasterxml.jackson.databind.JsonNode jnode = om.readTree(jsonString);
//        String jsonString =
//            new String(response.data, HttpHeaderParser.parseCharset(response.headers));


        return jnode;


    }


    //ツールa

    @Override
    protected final Response<com.fasterxml.jackson.databind.JsonNode> onParseNetworkResponse(NetworkResponse response) {
        try {
//            String jsonString = parseToString(response);
//
//        	ObjectMapper om = new ObjectMapper();
//        	com.fasterxml.jackson.databind.JsonNode jnode = om.readTree(jsonString);
//            String jsonString =
//                new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            com.fasterxml.jackson.databind.JsonNode jnode = parseToJsonNoe(response);

            return Response.success(jnode,
                    HttpHeaderParser.parseCacheHeaders(response));

        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }


}
