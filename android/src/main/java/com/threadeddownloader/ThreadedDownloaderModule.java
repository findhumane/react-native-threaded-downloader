package com.threadeddownloader;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.bridge.ReactContext;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import android.util.Log;

@ReactModule(name = ThreadedDownloaderModule.NAME)
public class ThreadedDownloaderModule extends ReactContextBaseJavaModule {
  public static final String NAME = "ThreadedDownloader";
  private static final int DEFAULT_MAX_THREADS = 10;
  private static final String NEWLINE = System.lineSeparator();

  public ThreadedDownloaderModule(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }

  @ReactMethod
  public void performThreadedDownload(final String url, final double timeoutSeconds, Promise promise) {
    Log.d(NAME, "performThreadedDownload start; url: " + url);

    // https://developer.android.com/develop/background-work/background-tasks/asynchronous/java-threads
    synchronized (this) {
      if (executorService == null) {
        executorService = Executors.newFixedThreadPool(DEFAULT_MAX_THREADS, new ThreadFactory() {
          private AtomicInteger threadCount = new AtomicInteger(1);

          @Override
          public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            thread.setName(NAME + "-" + threadCount.getAndIncrement());
            return thread;
          }
        });
      }
    }

    DownloaderThread downloader = new DownloaderThread(url, promise);
    executorService.execute(downloader);
  }

  class DownloaderThread implements Runnable {
    final String url;
    final double timeoutSeconds;
    final Promise promise;

    DownloaderThread(final String url, final double timeoutSeconds, final Promise promise) {
      this.url = url;
      this.timeoutSeconds = timeoutSeconds;
      this.promise = promise;
    }

    @Override
    public void run() {
      try {
        Log.d(NAME, "performThreadedDownload DownloaderThread start; url: " + url);

        URL urlObject = new URL(url);
        HttpURLConnection httpConnection = (HttpURLConnection)urlObject.openConnection();

        int timeoutMilliseconds = ((int)timeoutSeconds) * 1000;
        httpConnection.setConnectTimeout(timeoutMilliseconds);
        httpConnection.setReadTimeout(timeoutMilliseconds);

        Log.d(NAME, "performThreadedDownload DownloaderThread connection opened");

        httpConnection.setRequestMethod("GET");
        int responseCode = httpConnection.getResponseCode();

        Log.d(NAME, "performThreadedDownload DownloaderThread response code: " + responseCode);

        boolean isSuccessful = responseCode >= 100 && responseCode < 400;
        String response = null;
        try (InputStreamReader isr = new InputStreamReader(isSuccessful ? httpConnection.getInputStream() : httpConnection.getErrorStream())) {
          try (BufferedReader br = new BufferedReader(isr)) {
            StringBuffer responseBuffer = new StringBuffer();
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
              responseBuffer.append(inputLine);
              responseBuffer.append(NEWLINE);
            }
            response = responseBuffer.toString();
          }
        }

        Log.d(NAME, "performThreadedDownload DownloaderThread finished reading response body");

        if (isSuccessful) {
          promise.resolve(response);
        } else {
          promise.reject(NAME + " Error", "Error " + responseCode + " -- " + response, new Error("Error " + responseCode));
        }
      } catch (Throwable t) {
        Log.e(NAME, "performThreadedDownload DownloaderThread failed", t);
        promise.reject(NAME + " Error", "Failed to download " + url + " -- " + t.getLocalizedMessage(), t);
      }
    }
  }
}
