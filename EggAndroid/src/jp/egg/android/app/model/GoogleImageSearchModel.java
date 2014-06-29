package jp.egg.android.app.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown=true)
public class GoogleImageSearchModel {

	public ResponseData responseData;
	public String responseDetails;
	public Integer responseStatus;


}
