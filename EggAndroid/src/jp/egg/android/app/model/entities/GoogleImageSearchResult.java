package jp.egg.android.app.model.entities;

import jp.egg.android.db.annotation.Column;
import jp.egg.android.db.annotation.Column.ConflictAction;
import jp.egg.android.db.annotation.JsonParams;
import jp.egg.android.db.annotation.Table;
import jp.egg.android.db.model.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown=true)
@Table(name = "TestResult")
public class GoogleImageSearchResult extends Model{


	@JsonParams()
	@Column(unique = true, onUniqueConflict = ConflictAction.REPLACE)
	public String imageId;

	@Column()
	public String url;

	public String unescapedUrl;
	public String visibleUrl;
	public String originalContextUrl;


	public String GsearchResultClass;
	public String content;
	public String contentNoFormatting;

	public Integer width;
	public Integer height;

	public String tbUrl;
	public Integer tbWidth;
	public Integer tbHeight;

	public String title;
	public String titleNoFormatting;





}
