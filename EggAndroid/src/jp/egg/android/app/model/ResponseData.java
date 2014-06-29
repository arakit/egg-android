package jp.egg.android.app.model;

import jp.egg.android.app.model.entities.GoogleImageSearchResult;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ResponseData {

	public Curosr cursor;
	public GoogleImageSearchResult[] results;


}
