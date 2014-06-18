/*
 * Copyright(c) 2014 RichMedia Co., Ltd. All Rights Reserved.
 */

package jp.egg.android.request.volley;

import com.android.volley.Response;

/**
 * HTTPレスポンスリスナーインタフェース。
 */
public interface HttpResponseListener<T> extends Response.Listener<T>, Response.ErrorListener {
    /**
     * レスポンスを処理する。
     */
    void onResponse(T response);


}
