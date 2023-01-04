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
public abstract class CronetUrlRequestCallback extends UrlRequest.Callback {
    private final static String TAG = "Cronet";
    private static final int BYTE_BUFFER_CAPACITY_BYTES = 64 * 1024;
    private ByteArrayOutputStream bytesReceived = new ByteArrayOutputStream();
    private WritableByteChannel receiveChannel = Channels.newChannel(bytesReceived);
    private long startTime;

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
            Log.i(TAG, "IOException during ByteBuffer read. Details: ", e);
        }
        byteBuffer.clear();
        request.read(byteBuffer);
    }

    @Override
    public void onSucceeded(UrlRequest request, UrlResponseInfo info) {
        Log.i(TAG, "onSucceeded method called.");
        long latencyTime = System.currentTimeMillis() - startTime;
        Log.i(TAG, "****** Cronet Request Completed, the latency is " + latencyTime + " nanoseconds" +
                ". " + (info.wasCached() ? "The request was cached." : ""));

        Log.i(TAG, "****** Cronet Negotiated protocol:  " + info.getNegotiatedProtocol());

        Log.i(TAG, "****** Cronet Request Completed, status code is " + info.getHttpStatusCode()
                + ", total received bytes is " + info.getReceivedByteCount());
        onSuccess(request, info, bytesReceived.toByteArray(), latencyTime);
    }


    @Override
    public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException error) {
        Log.i(TAG, "onFailed method called.");
        long latencyTime = System.currentTimeMillis() - startTime;
        onFailed(request, info, error, latencyTime);
    }

    @Override
    public void onCanceled(UrlRequest request, UrlResponseInfo info) {
        super.onCanceled(request, info);
        Log.i(TAG, "onCanceled method called.");
        long latencyTime = System.currentTimeMillis() - startTime;
        onCanceled(request, info, latencyTime);
    }


    /**
     * 请求成功
     *
     * @param request
     * @param info
     * @param bodyBytes
     * @param latencyTime
     */
    abstract void onSuccess(UrlRequest request, UrlResponseInfo info, byte[] bodyBytes, long latencyTime);

    /**
     * 请求失败
     *
     * @param request
     * @param info
     * @param error
     * @param latencyTime
     */
    abstract void onFailed(UrlRequest request, UrlResponseInfo info, Exception error, long latencyTime);

    /**
     * 请求取消
     *
     * @param request
     * @param info
     * @param latencyTime
     */
    abstract void onCanceled(UrlRequest request, UrlResponseInfo info, long latencyTime);
}