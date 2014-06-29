package jp.egg.android.app.model;

import jp.egg.android.app.model.entities.GoogleImageSearchResult;
import jp.egg.android.db.annotation.Egg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ResponseData {

	public Curosr cursor;

	@Egg(save = true)
	public GoogleImageSearchResult[] results;


}
