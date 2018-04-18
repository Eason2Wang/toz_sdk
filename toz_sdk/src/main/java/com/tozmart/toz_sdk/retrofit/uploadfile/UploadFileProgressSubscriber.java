package com.tozmart.toz_sdk.retrofit.uploadfile;

import android.app.ProgressDialog;

/**
 * Created by wys on 17/5/13.
 */

public abstract class UploadFileProgressSubscriber extends UploadFileSubscriber {
    private ProgressDialog dialog;

    protected UploadFileProgressSubscriber(ProgressDialog dialog) {
        this.dialog = dialog;
    }

    @Override public void onError(Throwable e) {
        super.onError(e);
        if (dialog != null) dialog.dismiss();
    }

    @Override public void onComplete() {
        super.onComplete();
        if (dialog != null) dialog.dismiss();
    }
}
