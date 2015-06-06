package com.gaoyuan4122.download.util;

import android.content.Context;
import android.net.Proxy;
import android.os.Build;

import com.gaoyuan4122.download.app.GlobalConfig;
import com.gaoyuan4122.download.app.MyApplication;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by GAOYUAN on 2015/5/26.
 */
public class HttpUtil {
    private Context mCtx;

    public HttpUtil() {
        this.mCtx = MyApplication.getContext();
    }

    public void downFile(String url, HashMap<String, String> headers, DownloadProcessor downloadProcessor, boolean isGet)
            throws HttpException {
        if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE == GlobalConfig.NETWORK_STATE_IDLE) {
            throw new HttpException("Http NoAvailable");
        }
        HttpClient httpClient = null;
        try {
            HttpParams params = new BasicHttpParams();
            if (GlobalConfig.CURRENT_NETWORK_STATE_TYPE == GlobalConfig.NETWORK_STATE_CMWAP
                    || GlobalConfig.CURRENT_NETWORK_STATE_TYPE == GlobalConfig.NETWORK_STATE_CTWAP) {
                String proxyHost = null;
                int proxyPort = 80;
                if (Build.VERSION.SDK_INT >= 13) {
                    proxyHost = System.getProperties().getProperty("http.proxyHost");
                    proxyPort = Integer.parseInt(System.getProperties().getProperty("http.proxyPort"));
                } else {
                    proxyHost = Proxy.getHost(mCtx);
                    proxyPort = Proxy.getPort(mCtx);
                }
                if (proxyHost != null) {
                    HttpHost proxy = new HttpHost(proxyHost, proxyPort);
                    params.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
                }
            }
            HttpConnectionParams.setConnectionTimeout(params, GlobalConfig.HTTP_CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(params, GlobalConfig.HTTP_SO_TIMEOUT);
            HttpConnectionParams.setSocketBufferSize(params, GlobalConfig.HTTP_SOCKET_BUFFER_SIZE);
            HttpClientParams.setRedirecting(params, false);
            httpClient = new DefaultHttpClient(params);
            HttpResponse response = null;
            HttpContext httpContext = new BasicHttpContext();
            if (isGet) {
                HttpGet hg = new HttpGet(url);
                if (headers != null && headers.size() > 0) {
                    Iterator<String> iters = headers.keySet().iterator();
                    while (iters.hasNext()) {
                        String key = iters.next();
                        String value = headers.get(key);
                        hg.addHeader(key, value);
                    }
                }
                response = httpClient.execute(hg, httpContext);
            } else {
                HttpPost hp = new HttpPost(url);
                if (headers != null && headers.size() > 0) {
                    Iterator<String> iters = headers.keySet().iterator();
                    while (iters.hasNext()) {
                        String key = iters.next();
                        String value = headers.get(key);
                        hp.addHeader(key, value);
                    }
                }
                response = httpClient.execute(hp, httpContext);
            }

            if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 206) {
                HttpEntity entity = response.getEntity();
                downloadProcessor.processStream(entity.getContent(), entity.getContentLength(), entity.getContentEncoding(), null);
            } else if (response.getStatusLine().getStatusCode() == 301
                    || response.getStatusLine().getStatusCode() == 302) {
                String redirectURL = response.getHeaders("location")[0].getValue();
                downloadProcessor.processStream(null, -1, null, redirectURL);
                downFile(redirectURL, headers, downloadProcessor, isGet);
            }
        } catch (SocketTimeoutException e) {
            throw new HttpException(e.getMessage());
        } catch (IOException e) {
            throw new HttpException(e.getMessage());
        } catch (Exception e) {
            throw new HttpException(e.getMessage());
        } finally {
            if (httpClient != null) {
                httpClient.getConnectionManager().shutdown();
            }
        }
    }

    public interface DownloadProcessor {
        public void processStream(InputStream stream, long totalSize, Header encoding, String newURL);
    }
}
