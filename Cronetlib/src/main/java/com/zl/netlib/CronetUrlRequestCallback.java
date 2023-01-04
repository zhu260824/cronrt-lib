package com.zl.netlib;

import android.util.Log;

import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

/**
 * @author ZL @朱林</a>
 * @Version 1.0
 * @Description TODO
 * @date 2023/01/04  10:45
 */
public class CronetUrlRequestCallback extends UrlRequest.Callback {
    private final static String TAG = "Cronet";
    private static final int BYTE_BUFFER_CAPACITY_BYTES = 64 * 1024;
    private ByteArrayOutputStream bytesReceived = new ByteArrayOutputStream();
    private WritableByteChannel receiveChannel = Channels.newChannel(bytesReceived);
    private long startTime;
    private CronetHttpCallBack httpCallBack;

    public CronetUrlRequestCallback() {
    }

    public CronetUrlRequestCallback(CronetHttpCallBack httpCallBack) {
        this.httpCallBack = httpCallBack;
    }

    @Override
    public void onRedirectReceived(UrlRequest request, UrlResponseInfo info, String newLocationUrl) throws Exception {
        Log.i(TAG, "onRedirectReceived  method called.");
        request.followRedirect();
    }

    @Override
    public void onResponseStarted(UrlRequest request, UrlResponseInfo info) throws Exception {
        Log.i(TAG, "onResponseStarted  method called.");
        startTime = System.currentTimeMillis();
        request.read(ByteBuffer.allocateDirect(BYTE_BUFFER_CAPACITY_BYTES));
    }

    @Override
    public void onReadCompleted(UrlRequest request, UrlResponseInfo info, ByteBuffer byteBuffer) throws Exception {
        Log.i(TAG, "onReadCompleted method called.");
        byteBuffer.flip();
        try {
            receiveChannel.write(byteBuffer);
        } catch (IOException e) {
            android.util.Log.i(TAG, "IOException during ByteBuffer read. Details: ", e);
        }
        byteBuffer.clear();
        request.read(byteBuffer);
    }

    @Override
    public void onSucceeded(UrlRequest request, UrlResponseInfo info) {
        Log.i(TAG, "onSucceeded method called.");
        long latencyTime = System.currentTimeMillis() - startTime;
        Log.i(TAG,
                "****** Cronet Request Completed, the latency is " + latencyTime + " nanoseconds" +
                        ". " + getWasCachedMessage(info));

        Log.i(TAG,
                "****** Cronet Negotiated protocol:  " + info.getNegotiatedProtocol());

        Log.i(TAG,
                "****** Cronet Request Completed, status code is " + info.getHttpStatusCode()
                        + ", total received bytes is " + info.getReceivedByteCount());
        byte[] bodyBytes = bytesReceived.toByteArray();
        if (null!=httpCallBack){
            httpCallBack.onSuccess(request,info,bodyBytes,latencyTime);
        }
    }

    private static String getWasCachedMessage(UrlResponseInfo responseInfo) {
        if (responseInfo.wasCached()) {
            return "The request was cached.";
        } else {
            return "";
        }
    }


    @Override
    public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException error) {
        Log.i(TAG, "onFailed method called.");
    }

    @Override
    public void onCanceled(UrlRequest request, UrlResponseInfo info) {
        super.onCanceled(request, info);
        Log.i(TAG, "onCanceled method called.");
    }


}