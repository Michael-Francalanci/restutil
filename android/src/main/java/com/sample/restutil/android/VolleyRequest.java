package com.sample.restutil.android;

import android.util.Log;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mark Hofmann (mark@mark-hofmann.de)
 */
public class VolleyRequest extends Request<JsonResponse> {
    private static final String TAG = VolleyRequest.class.getName();
    private String body;
    private Map<String, String> headers;
    private Map<String, String> params;
    private Response.Listener<JsonResponse> listener;

    public VolleyRequest(int method, String url, String body, Map<String, String> params,
        Map<String, String> headers, Response.Listener<JsonResponse> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.body = body;
        this.headers = headers;
        this.params = params;
        this.listener = listener;
    }

    @Override
    protected Map<String, String> getParams() {
        return params;
    }

    @Override
    public String getBodyContentType() {
        return "application/json; charset=" + super.getParamsEncoding();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if (headers == null || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<>();
        }
        headers.put("Accept", "application/json");
        return headers;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if (body != null) {
            try {
                return body.getBytes(super.getParamsEncoding());
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("UTF-8 == unknown encoding???", e);
            }
        } else {
            return super.getBody();
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        listener = null;
    }

    @Override
    protected void deliverResponse(JsonResponse response) {
        if (listener != null) {
            listener.onResponse(response);
        }
    }

    @Override
    protected Response<JsonResponse> parseNetworkResponse(NetworkResponse response) {
        Log.d(TAG, "recieved response:" + response);
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response
            .success(new JsonResponse(parsed, response.headers), HttpHeaderParser.parseCacheHeaders(response));
    }

}
