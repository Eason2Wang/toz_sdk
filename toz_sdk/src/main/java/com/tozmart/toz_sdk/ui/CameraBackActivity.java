
package com.tozmart.toz_sdk.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;
import com.tozmart.toz_sdk.R;
import com.tozmart.toz_sdk.beans.CustomerInfo;
import com.tozmart.toz_sdk.contants.Constants;
import com.tozmart.toz_sdk.contants.ImageConstParams;
import com.tozmart.toz_sdk.utils.BitmapHolder;
import com.tozmart.toz_sdk.utils.ClickUtils;
import com.tozmart.toz_sdk.utils.DisplayUtil;
import com.tozmart.toz_sdk.utils.FileUtil;
import com.tozmart.toz_sdk.utils.GetFileEnd;
import com.tozmart.toz_sdk.utils.ImageUtil;
import com.tozmart.toz_sdk.utils.PickPhotoFromGallery;
import com.tozmart.toz_sdk.utils.ShowToast;
import com.tozmart.toz_sdk.utils.StatusBarUtils;
import com.tozmart.toz_sdk.utils.WriteFileHeadAndEnd;
import com.tozmart.toz_sdk.widge.LineImageView;
import com.tozmart.toz_sdk.widge.SensorTipView;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class CameraBackActivity extends BaseAppCompatActivity {
    public static final String TAG = CameraBackActivity.class.getSimpleName();
    private final int BODYSHOW_ACTIVITY_REQUEST_CODE = 1011;

    public static final int BACK_CAMERA = 0;
    private final CompositeDisposable disposables = new CompositeDisposable();

    //角度传感器的范围
    private final int MIN_BOUND = -8;
    private final int MAX_BOUND = 8;
    /**
     * sensor
     */
    private SensorManager mSensorManager;
    private float X, Y;
    //需要两个Sensor
    private Sensor aSensor;
    private Sensor mSensor;
    private float[] accelerometerValues = new float[3];
    private float[] magneticFieldValues = new float[3];
    //以下是实现SensorEventListener接口必须实现的方法
    final SensorEventListener myListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent sensorEvent) {
            synchronized (this) {
                //注意在赋值的时候一定要调用一下 values 数组的 clone() 方法，不
                //然 accelerometerValues 和 magneticValues 将会指向同一个引用。
                if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                    magneticFieldValues = sensorEvent.values.clone();
                if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                    accelerometerValues = sensorEvent.values.clone();
                calculateOrientation();
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private CoordinatorLayout parent_layout;
    private ImageView camera_mask;
    private CameraView camera;
    private FrameLayout cameraArea;
    private ProgressBar circularProgressBar;
    private ImageView shutterBtn;
    private TextView top_text;
    private RelativeLayout bottomLayout;
    private TextView userInfo;
    private LineImageView lineImageView;
    private ImageView tipsView;
    private ImageView pickPhotoBtn;
    private ImageView backwardView;
    private CheckBox cameraPoseCheckbox;
    private LinearLayout errorPositionToast;
    private SensorTipView sensorView;
    private float sensorViewWidth;

    /**
     * 是否显示模特
     */
    private boolean isShowModel = true;

    private int bottomLayout_height;

    /**
     * 拍照获得的图片
     */
    Bitmap bitmap;

    /**
     * 指示拍正侧面
     */
    boolean isFront = true;

    /**
     * 屏幕真实高度
     */
    private int screenRealHeight;

    /**
     * 记录navigationbar高度
     */
    private int mNavigationBarHeight;

    /**
     * 保存图片时间
     */
    long saveBitmapTime;

    /**
     * 当前需要拍照量体的用户的信息
     */
    private CustomerInfo customerInfo;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_back_camera;
    }

    @Override
    protected void initActionBar() {
        super.initActionBar();
    }

    @Override
    protected void initView() {
        super.initView();
        saveBitmapTime = System.currentTimeMillis();

        //打开屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        parent_layout = (CoordinatorLayout) findViewById(R.id.back_container_layout);
        camera_mask = (ImageView) findViewById(R.id.camera_mask);
        camera = (CameraView) findViewById(R.id.camera);
        cameraArea = (FrameLayout) findViewById(R.id.camera_area);
        circularProgressBar = (ProgressBar) findViewById(R.id.pb);
        shutterBtn = (ImageView) findViewById(R.id.capturePhoto);
        top_text = (TextView) findViewById(R.id.top_text);
        bottomLayout = (RelativeLayout) findViewById(R.id.bottom_l);
        userInfo = (TextView) findViewById(R.id.camera_user_info);
        lineImageView = (LineImageView) findViewById(R.id.lineImageView);
        tipsView = (ImageView) findViewById(R.id.camera_tips_view);
        pickPhotoBtn = (ImageView) findViewById(R.id.pickPhoto);
        backwardView = (ImageView) findViewById(R.id.camera_back_view);
        cameraPoseCheckbox = (CheckBox) findViewById(R.id.camera_pose_checkbox);
        errorPositionToast = (LinearLayout) findViewById(R.id.camera_position_error_l);
        sensorView = (SensorTipView) findViewById(R.id.sensorView);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        screenRealHeight = DisplayUtil.getRealScreenMetrics(this).y;

        //有的设备底部虚拟按键会自动隐藏和显示，所以需要重置camera view的位置
        parent_layout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (!StatusBarUtils.isNavigationBarShow(CameraBackActivity.this)){
                    camera.setTranslationY(-bottomLayout_height
                            - DisplayUtil.getStatusBarHeight(CameraBackActivity.this));
                } else {
                    // status bar 和 navigationBarHeight（如有）的高度之和
                    int barHeight = StatusBarUtils.getNavigationBarOffsetPx(CameraBackActivity.this)
                                + DisplayUtil.getStatusBarHeight(CameraBackActivity.this);
                    camera.setTranslationY(-bottomLayout_height - barHeight);
                }
            }
        });
        shutterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ClickUtils.isDoubleClick()) {
                    shutterBtn.setEnabled(false);
                    pickPhotoBtn.setEnabled(false);
                    tipsView.setEnabled(false);
                    try {
                        camera.captureImage();
                    } catch (Exception e) {
                        ShowToast.showToast("相机错误，无法完成拍照。");
                        e.printStackTrace();
                    }
                }
            }
        });
        pickPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickPhotoFromGallery.pickImage(CameraBackActivity.this);
            }
        });
        tipsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(this, CameraHelpActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putParcelable("userInfo", customerInfo);
//                intent.putExtras(bundle);
//                startActivity(intent);
            }
        });
        backwardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        /**
         * gif view
         */
        ViewGroup.LayoutParams params = bottomLayout.getLayoutParams();
        params.height = screenRealHeight / 4;
        bottomLayout_height = params.height;
        bottomLayout.setLayoutParams(params);
        /**
         * 将拍照界面调整到gifView上面
         */
        ViewGroup.LayoutParams cameraLayoutParams = camera.getLayoutParams();
        cameraLayoutParams.height = screenRealHeight;
        camera.setLayoutParams(cameraLayoutParams);

        cameraPoseCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isShowModel = isChecked;
                lineImageView.postInvalidate(isFront, isShowModel);
            }
        });

        setToFront();
        initSensorView();
        initUserInfoView();
    }

    public void setCustomerInfo(CustomerInfo customerInfo){
        this.customerInfo = customerInfo;
    }

    private void initSensorView(){
        sensorViewWidth = getResources().getDimension(R.dimen.camera_toast_width) / 2.f;
        ViewGroup.LayoutParams params = sensorView.getLayoutParams();
        params.width = (int)sensorViewWidth;
        params.height = (int)sensorViewWidth;
        sensorView.setLayoutParams(params);
        sensorView.setCircleWidth(sensorViewWidth * 0.02f);
        sensorView.setRadius(sensorViewWidth * 0.5f);
    }

    private void initUserInfoView(){
        /**
         * set info
         */
        if (customerInfo.getUserGender().equals(Constants.MALE)){
            userInfo.setText(customerInfo.getUserName() + " (" + getString(R.string.male) + ") "
                    + customerInfo.getUserHeight() + "cm");
        }else{
            userInfo.setText(customerInfo.getUserName() + " (" + getString(R.string.female) + ") "
                    + customerInfo.getUserHeight() + "cm");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        circularProgressBar.setVisibility(View.GONE);
        camera_mask.setVisibility(View.GONE);
        shutterBtn.setEnabled(true);
        pickPhotoBtn.setEnabled(true);
        tipsView.setEnabled(true);

        camera.start();
        camera.setCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] jpeg) {
                super.onPictureTaken(jpeg);
                bitmap = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);
                if (bitmap.getWidth() > bitmap.getHeight()){
                    bitmap = ImageUtil.getRotateBitmap(bitmap, 90);
                }
                /**
                 * 屏幕上肉眼可见的相机的高度
                 */
                float cameraHeight = DisplayUtil.getScreenMetrics(CameraBackActivity.this).y
                        - bottomLayout_height
                        - DisplayUtil.getStatusBarHeight(CameraBackActivity.this);
                float cameraView2ScreenRatio = cameraHeight / screenRealHeight;
                float ratio = (float) bitmap.getHeight() / bitmap.getWidth();
                bitmap = ImageUtil.getResizedBitmap(bitmap,
                        ImageConstParams.SCALE_IMAGE_W_HIGH,
                        (int)(ImageConstParams.SCALE_IMAGE_W_HIGH * ratio));
                bitmap = Bitmap.createBitmap(
                        bitmap,
                        0,
                        (int)(bitmap.getHeight() * (1 - cameraView2ScreenRatio)),
                        bitmap.getWidth(),
                        (int)(bitmap.getHeight() * cameraView2ScreenRatio));

                customerInfo.setSelectFromGallery(false);
                if (isFront) {
                    detectFaceTask(false);
                } else{
                    BitmapHolder.setSideBitmap(bitmap);
                    /**
                     * 保存图片
                     */
                    new Thread(new saveBitmapThread("s")).start();
                    startProcessImage();
                }
            }
        });

        //为系统的方向传感器注册监听器
        aSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(myListener, aSensor, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(myListener, mSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        camera.stop();

        //取消注册sensor
        if (mSensorManager != null)
            mSensorManager.unregisterListener(myListener);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        disposables.clear(); // do not send event after activity has been destroyed
        BitmapHolder.recycle();
        lineImageView.recycleBitmap();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == PickPhotoFromGallery.REQUEST_PICK && resultCode == RESULT_OK) {
            circularProgressBar.setVisibility(View.VISIBLE);
            camera_mask.setVisibility(View.VISIBLE);
            shutterBtn.setEnabled(false);
            pickPhotoBtn.setEnabled(false);
            tipsView.setEnabled(false);

            if (result.getData() != null) {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), result.getData());
                    if (bitmap.getWidth() > bitmap.getHeight()){
                        bitmap = ImageUtil.getRotateBitmap(bitmap, 90);
                    }

                    String path = FileUtil.getFileAbsolutePathFromUri(
                            CameraBackActivity.this, result.getData());
                    String decodeString = new String(Base64.decode(
                            GetFileEnd.readLastLine(path, null), Base64.NO_WRAP));
                    if (decodeString.equals(ImageUtil.getBitmapEncodeString(bitmap))){
                        float ratio = (float) bitmap.getHeight() / bitmap.getWidth();
                        bitmap = ImageUtil.getResizedBitmap(bitmap,
                                ImageConstParams.SCALE_IMAGE_W_HIGH,
                                (int)(ImageConstParams.SCALE_IMAGE_W_HIGH * ratio));

                        customerInfo.setSelectFromGallery(true);
                        if (isFront) {
                            detectFaceTask(true);
                        } else{
                            BitmapHolder.setSideBitmap(bitmap);
                            startProcessImage();
                        }
                    } else {
                        showIllegalImageError();
                        if (bitmap != null && !bitmap.isRecycled()) bitmap.recycle();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showIllegalImageError();
                    if (bitmap != null && !bitmap.isRecycled()) bitmap.recycle();
                }
            }
        } else if (requestCode == BODYSHOW_ACTIVITY_REQUEST_CODE && resultCode == RESULT_FIRST_USER){
            finish();
        }
    }

    private void detectFaceTask(final boolean isSelectFromGallery){
        Observable<RectF> observable = Observable.defer(new Callable<ObservableSource<? extends RectF>>() {
            @Override
            public ObservableSource<? extends RectF> call() throws Exception {
                // Do some long running operation
                if (!isSelectFromGallery) {
//                    DetectMethod detectF = new DetectMethod();
//                    RectF f = detectF.DetectFace(bitmap, CameraBackActivity.this);
                    return Observable.just(new RectF());
                } else{
                    return Observable.just(new RectF());
                }
            }
        });

        disposables.add(observable
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        circularProgressBar.setVisibility(View.VISIBLE);
                        camera_mask.setVisibility(View.VISIBLE);
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread()) // 指定主线程
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<RectF>() {
                    @Override
                    public void onNext(RectF face) {
                        circularProgressBar.setVisibility(View.GONE);
                        camera_mask.setVisibility(View.GONE);
                        shutterBtn.setEnabled(true);
                        pickPhotoBtn.setEnabled(true);
                        tipsView.setEnabled(true);

                        BitmapHolder.setFrontBitmap(bitmap);
                        setToSide();

                        if (!isSelectFromGallery) {
                            /**
                             * 保存图片
                             */
                            new Thread(new saveBitmapThread("f")).start();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, e.toString());
                        circularProgressBar.setVisibility(View.GONE);
                        camera_mask.setVisibility(View.GONE);
                        shutterBtn.setEnabled(true);
                        pickPhotoBtn.setEnabled(true);
                        tipsView.setEnabled(true);

                        new MaterialDialog.Builder(CameraBackActivity.this)
                                .iconRes(R.drawable.ic_error_red_24dp)
                                .limitIconToDefaultSize() // limits the displayed icon size to 48dp
                                .title(R.string.sorry)
                                .content(R.string.detect_face)
                                .positiveText(android.R.string.ok)
                                .canceledOnTouchOutside(false)
                                .show();
                    }

                    @Override
                    public void onComplete() {
                    }
                })
        );
    }

    /**
     * 保存拍照后的图片到本地图库中
     */
    private class saveBitmapThread implements Runnable {
        String mPose;
        public saveBitmapThread(String pose){
            mPose = pose;
        }

        @Override
        public void run() {
            try {
                FileUtil.saveBitmapToSD(bitmap,
                        Constants.PHOTO_SAVE_PATH,
                        String.valueOf(saveBitmapTime) + "_" + customerInfo.getUserName()
                                + "_" + customerInfo.getUserHeight()
                                + "_" + customerInfo.getUserWeight() + "_" + mPose + ".png",
                        true,
                        1);
                WriteFileHeadAndEnd.writeEnd(Constants.PHOTO_SAVE_PATH + "/"
                                + String.valueOf(saveBitmapTime) + "_" + customerInfo.getUserName()
                                + "_" + customerInfo.getUserHeight()
                                + "_" + customerInfo.getUserWeight() + "_" + mPose + ".png",
                        Base64.encodeToString(ImageUtil.getBitmapEncodeString(bitmap).getBytes(), Base64.NO_WRAP));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void calculateOrientation() {
        float[] values = new float[3];
        float[] temp = new float[9];
        float[] Rr = new float[9];
        SensorManager.getRotationMatrix(temp, null, accelerometerValues, magneticFieldValues);
        //Remap to camera's point-of-view
        SensorManager.remapCoordinateSystem(temp,
                SensorManager.AXIS_X,
                SensorManager.AXIS_Z, Rr);
        SensorManager.getOrientation(Rr, values);

        // 要经过一次数据格式的转换，转换为度
        values[0] = (float) Math.toDegrees(values[0]);
        values[1] = (float) Math.toDegrees(values[1]);
        values[2] = (float) Math.toDegrees(values[2]);

        //传感器数据去噪方法，一阶差分
        final float alpha = 0.8f;
        float[] valuesTemp = new float[3];
        valuesTemp[1] = alpha * valuesTemp[1] + (1 - alpha) * values[1];
        valuesTemp[2] = alpha * valuesTemp[2] + (1 - alpha) * values[2];
        X = values[1] - valuesTemp[1];
        Y = values[2] - valuesTemp[2];

        if (Y + MAX_BOUND > 4 * MAX_BOUND) {
            Y = 3 * MAX_BOUND;
        } else if (Y + MIN_BOUND < 4 * MIN_BOUND) {
            Y = 3 * MIN_BOUND;
        }
        if (X + MAX_BOUND > 4 * MAX_BOUND) {
            X = 3 * MAX_BOUND;
        } else if (X + MIN_BOUND < 4 * MIN_BOUND) {
            X = 3 * MIN_BOUND;
        }
        sensorView.setBallPosition((Y - 4 * MIN_BOUND) / (4 * (MAX_BOUND - MIN_BOUND)),
                (X - 4 * MIN_BOUND) / (4 * (MAX_BOUND - MIN_BOUND)));

        double dis = Math.sqrt(Math.pow(X, 2) + Math.pow(Y, 2));
        if (dis < MAX_BOUND) {
            shutterBtn.setImageResource(R.drawable.camera_tick);
            shutterBtn.setEnabled(true);
            errorPositionToast.setVisibility(View.INVISIBLE);
        } else {
            shutterBtn.setImageResource(R.drawable.camera_cross);
            shutterBtn.setEnabled(false);
            errorPositionToast.setVisibility(View.VISIBLE);
        }
    }

    /**
     * UI恢复并跳转activity
     */
    private void startProcessImage(){
        circularProgressBar.setVisibility(View.GONE);
        camera_mask.setVisibility(View.GONE);
        shutterBtn.setEnabled(true);
        pickPhotoBtn.setEnabled(true);
        tipsView.setEnabled(true);
        setToFront();

//        Intent intent = new Intent(this, BodyShowActivity.class);
//        /*new一个Bundle对象，并将要传递的数据传入*/
//        Bundle bundle = new Bundle();
//        bundle.putInt("cameraId", BACK_CAMERA);
//        bundle.putParcelable("userInfo", customerInfo);
//        /*将Bundle对象assign给Intent*/
//        intent.putExtras(bundle);
//        startActivityForResult(intent, BODYSHOW_ACTIVITY_REQUEST_CODE);
    }

    /**
     * 进入侧面拍照设置相关
     */
    private void setToSide(){
        isFront = false;
        top_text.setText(R.string.side);
        lineImageView.postInvalidate(isFront, isShowModel);
    }

    /**
     * 进入正面拍照设置相关
     */
    private void setToFront(){
        isFront = true;
        top_text.setText(R.string.front);
        lineImageView.postInvalidate(isFront, isShowModel);
    }

    private void showIllegalImageError(){
        new MaterialDialog.Builder(CameraBackActivity.this)
                .iconRes(R.drawable.ic_error_red_24dp)
                .limitIconToDefaultSize() // limits the displayed icon size to 48dp
                .title(R.string.error_dialog_title)
                .content(R.string.select_app_image)
                .positiveText(android.R.string.ok)
                .canceledOnTouchOutside(true)
                .show();
    }
}