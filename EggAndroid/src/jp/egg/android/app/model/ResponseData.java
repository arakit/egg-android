package jp.egg.android.app.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jp.egg.android.app.model.entities.GoogleImageSearchResult;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseData {

    public Curosr cursor;

    //@Egg(save = true)
    public GoogleImageSearchResult[] results;


}
