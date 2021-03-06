/**
 * Copyright (c) 2017-present, Wonday (@wonday.org)
 * All rights reserved.
 *
 * This source code is licensed under the MIT-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package org.wonday.pdf;

import java.io.File;

import android.content.Context;
import android.view.ViewGroup;
import android.util.Log;
import android.graphics.PointF;
import android.net.Uri;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import static java.lang.String.format;
import java.lang.ClassCastException;


public class RCTPdfManager extends SimpleViewManager<PDFView> implements OnPageChangeListener,OnLoadCompleteListener,OnErrorListener {
    private static final String REACT_CLASS = "RCTPdf";
    private Context context;
    private PDFView pdfView;
    private int page = 1;               // start from 1
    private boolean horizontal = false;
    private float scale = 1;
    private String asset;
    private String path;
    private int spacing = 10;
    private String password = "";
    private boolean enableAntialiasing = true;


    public RCTPdfManager(ReactApplicationContext reactContext){
        this.context = reactContext;
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public PDFView createViewInstance(ThemedReactContext context) {
        pdfView = new PDFView(context, null);
        return pdfView;
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        // pdf lib page start from 0, convert it to our page (start from 1)
        page = page+1;
        showLog(format("%s %s / %s", path, page, pageCount));

        WritableMap event = Arguments.createMap();
        event.putString("message", "pageChanged|"+page+"|"+pageCount);
        ReactContext reactContext = (ReactContext)pdfView.getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
            pdfView.getId(),
            "topChange",
            event
         );
    }

    @Override
    public void loadComplete(int pageCount) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "loadComplete|"+pageCount);
        ReactContext reactContext = (ReactContext)pdfView.getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
            pdfView.getId(),
            "topChange",
            event
         );
    }

    @Override
    public void onError(Throwable t){
        WritableMap event = Arguments.createMap();
        if (t.getMessage().contains("Password required or incorrect password")) {
            event.putString("message", "error|Password required or incorrect password.");
        } else {
            event.putString("message", "error|"+t.getMessage());
        }

        ReactContext reactContext = (ReactContext)pdfView.getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
            pdfView.getId(),
            "topChange",
            event
         );
    }

    private void drawPdf() {
        PointF pivot = new PointF(this.scale, this.scale);

        if (this.path != null){
            File pdfFile = new File(this.path);
            pdfView.fromFile(pdfFile)
                .defaultPage(this.page-1)
                //.showMinimap(false)
                //.enableSwipe(true)
                .swipeHorizontal(this.horizontal)
                .onPageChange(this)
                .onLoad(this)
                .onError(this)
                .spacing(this.spacing)
                .password(this.password)
                .enableAntialiasing(this.enableAntialiasing)
                .load();

            pdfView.zoomCenteredTo(this.scale, pivot);
            showLog(format("drawPdf path:%s %s", this.path, this.page));
        }
    }

    @ReactProp(name = "path")
    public void setPath(PDFView view, String path) {
        this.path = path;
        drawPdf();
    }

    // page start from 1
    @ReactProp(name = "page")
    public void setPage(PDFView view, int page) {
        this.page = page>1?page:1;
        drawPdf();
    }

    @ReactProp(name = "scale")
    public void setScale(PDFView view, float scale) {
        this.scale = scale;
        drawPdf();
    }

    @ReactProp(name = "horizontal")
    public void setHorizontal(PDFView view, boolean horizontal) {
        this.horizontal = horizontal;
        drawPdf();
    }

    @ReactProp(name = "spacing")
    public void setSpacing(PDFView view, int spacing) {
        this.spacing = spacing;
        drawPdf();
    }

    @ReactProp(name = "password")
    public void setSpacing(PDFView view, String password) {
        this.password = password;
        drawPdf();
    }

    @ReactProp(name = "enableAntialiasing")
    public void setEnableAntialiasing(PDFView view, boolean enableAntialiasing) {
        this.enableAntialiasing = enableAntialiasing;
        drawPdf();
    }
    
    private void showLog(final String str) {
        Log.d(REACT_CLASS, str);
    }
}
