package com.zl.netlib;

import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;

/**
 * @author ZL @朱林</a>
 * @Version 1.0
 * @Description TODO
 * @date 2023/01/04  10:56
 */
public abstract class CronetHttpCallBack {

    /**
     * 接口请求成功
     *
     * @param request
     * @param info
     * @param body
     * @param latencyTime
     */
    abstract void onSuccess(UrlRequest request, UrlResponseInfo info, byte[] body, long latencyTime);
    /**
     * 接口请求失败
     *
     * @param request
     * @param info
     * @param error
     */
    abstract void onFailed(UrlRequest request, UrlResponseInfo info, CronetException error);

    public void onCanceled(UrlRequest request, UrlResponseInfo info) {
    }
}