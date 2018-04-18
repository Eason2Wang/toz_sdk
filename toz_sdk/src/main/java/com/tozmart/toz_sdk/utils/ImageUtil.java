package com.tozmart.toz_sdk.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Base64;
import android.util.Log;

import com.tozmart.toz_sdk.TozSDK;
import com.tozmart.toz_sdk.contants.Constants;
import com.tozmart.toz_sdk.retrofit.callback.LoadDataCallBack;
import com.tozmart.toz_sdk.retrofit.uploadfile.UploadFile;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static com.tozmart.toz_sdk.contants.HttpsAuthString.ENCODE_BITMAP_STRING;
import static com.tozmart.toz_sdk.contants.HttpsAuthString.HTTPS_SERVER_PASSWORD;
import static com.tozmart.toz_sdk.contants.HttpsAuthString.HTTPS_SERVER_USER_NAME;

public class ImageUtil {

    /**
     * @param options   参数
     * @param reqWidth  目标的宽度
     * @param reqHeight 目标的高度
     * @return
     * @description 计算图片的压缩比率
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * @param src
     * @param dstWidth
     * @param dstHeight
     * @return
     * @description 通过传入的bitmap，进行压缩，得到符合标准的bitmap
     */
    public static Bitmap createScaleBitmap(Bitmap src, int dstWidth, int dstHeight, int inSampleSize) {
        // 如果是放大图片，filter决定是否平滑，如果是缩小图片，filter无影响，我们这里是缩小图片，所以直接设置为false
        Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
        if (src != dst) { // 如果没有缩放，那么不回收
            src.recycle(); // 释放Bitmap的native像素数组
        }
        return dst;
    }

    /**
     * @param pathName
     * @param reqWidth
     * @param reqHeight
     * @return
     * @description 从SD卡上加载图片
     */
    public static Bitmap decodeSampledBitmapFromFile(String pathName, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        if (reqWidth == 0 && reqHeight == 0) {
            return BitmapFactory.decodeFile(pathName);
        } else {
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            Bitmap src = BitmapFactory.decodeFile(pathName, options);
            return createScaleBitmap(src, reqWidth, reqHeight, options.inSampleSize);
        }
    }

    /**
     * @param pathName
     * @param reqHeight
     * @return
     * @description 从SD卡上加载图片
     */
    public static Bitmap decodeSampledBitmapFromFile(String pathName, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        float ratio = (float) options.outWidth / options.outHeight;
        if (reqHeight == 0) {
            return BitmapFactory.decodeFile(pathName);
        } else {
            options.inSampleSize = calculateInSampleSize(options, (int) (reqHeight * ratio), reqHeight);
            options.inJustDecodeBounds = false;
            Bitmap src = BitmapFactory.decodeFile(pathName, options);
            return createScaleBitmap(src, (int) (reqHeight * ratio), reqHeight, options.inSampleSize);
        }
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        if (bitmap != null) {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                    .getHeight(), Bitmap.Config.ARGB_4444);
            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(rect);
            float roundPx = pixels;

            if (roundPx > bitmap.getWidth() / 2) {
                roundPx = bitmap.getWidth() / 2;
            }

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

            return output;
        }
        return null;
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    public static Bitmap resizedBitmapToSquare(Bitmap bm, int newValue) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scale = width < height ? ((float) newValue) / height : ((float) newValue) / width;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scale, scale);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    //图片压缩
    public static Bitmap compressImageFromFile(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;//只读边,不读内容
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 800f;//
        float ww = 480f;//
        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置采样率

        newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888;//该模式是默认的,可不设
        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收

        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
//		return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩
        //其实是无效的,大家尽管尝试
        return bitmap;
    }

    //从Assets中读取图片
    public static Bitmap getImageFromAssetsFile(String fileName, Context context) {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;

    }

    public static byte[] bitmap2Byte(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    private static InputStream OpenHttpConnection(String urlString)
            throws IOException {
        InputStream in = null;
        int response = -1;

        URL url = new URL(urlString);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

        if (!(httpConn instanceof HttpURLConnection))
            throw new IOException("Not an HTTP connection");

        try {
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.setRequestProperty("Accept-Encoding", "");
            httpConn.setRequestProperty("Connection", "close");
            httpConn.setRequestProperty("Authorization", "Basic " +
                    Base64.encodeToString((HTTPS_SERVER_USER_NAME + ":"
                            + HTTPS_SERVER_PASSWORD).getBytes(), Base64.NO_WRAP));
            httpConn.connect();

            response = httpConn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new IOException("Error connecting");
        }
        return in;
    }

    public static Bitmap downloadBitmap(String URL) {
        Bitmap bitmap = null;
        InputStream in = null;
        try {
            in = OpenHttpConnection(URL);
            if (in == null) {
                return null;
            }
            bitmap = BitmapFactory.decodeStream(in);
            in.close();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 图片关于Y轴对称
     *
     * @param b
     * @return
     */
    public Bitmap mirrorY(Bitmap b) {
        if (b != null) {
            Matrix m = new Matrix();
            Matrix temp = new Matrix();
            float[] mirrorY = {-1, 0, 0, 0, 1, 0, 0, 0, 1};
            temp.setValues(mirrorY);
            m.postConcat(temp);
            try {
                Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
                if (b != b2) {
                    b.recycle();
                    b = b2;
                }
            } catch (OutOfMemoryError ex) {
                ex.printStackTrace();
                System.out.println("内存溢出异常");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("出错了");
            }
        }
        return b;
    }

    /**
     * 图片关于X轴对称
     *
     * @param b
     * @return
     */
    public static Bitmap mirrorX(Bitmap b) {
        if (b != null) {
            Matrix m = new Matrix();
            Matrix temp = new Matrix();
            float[] mirrorY = {1, 0, 0, 0, -1, 0, 0, 0, 1};
            temp.setValues(mirrorY);
            m.postConcat(temp);
            try {
                Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
                if (b != b2) {
                    b.recycle();
                    b = b2;
                }
            } catch (OutOfMemoryError ex) {
                ex.printStackTrace();
                System.out.println("内存溢出异常");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("出错了");
            }
        }
        return b;
    }

    /**
     * 旋转Bitmap
     *
     * @param b
     * @param rotateDegree
     * @return
     */
    public static Bitmap getRotateBitmap(Bitmap b, float rotateDegree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotateDegree);
        b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, false);
        return b;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转图片
     *
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public Bitmap rotaingImage(int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    public void convertToBitmap(String imagePath) {
        Bitmap bImage = BitmapFactory.decodeFile(imagePath);
        writePhoto(bImage, bImage.getWidth(), bImage.getHeight(), imagePath);
    }

    public void writePhoto(Bitmap bmp, int width, int height, String path) {
        File file = new File(path);
        try {
            Bitmap bm = Bitmap.createBitmap(bmp, 0, 0, width, height);
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(file));
            if (bm.compress(Bitmap.CompressFormat.JPEG, 100, bos)) {
                bos.flush();
                bos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // get image RGB values
    public void getRGB(Bitmap mBitmap) {

        int mBitmapWidth = mBitmap.getWidth();
        int mBitmapHeight = mBitmap.getHeight();

        int mArrayColorLengh = mBitmapWidth * mBitmapHeight;
        int mArrayColor[] = new int[mArrayColorLengh];
        int count = 0;
        for (int i = 0; i < mBitmapHeight; i++) {
            for (int j = 0; j < mBitmapWidth; j++) {
                //获得Bitmap 图片中每一个点的color颜色值
                int color = mBitmap.getPixel(j, i);
                //将颜色值存在一个数组中 方便后面修改
                mArrayColor[count] = color;
                //如果你想做的更细致的话 可以把颜色值的R G B 拿到做响应的处理 笔者在这里就不做更多解释
                int r = Color.red(color);
                int g = Color.green(color);
                int b = Color.blue(color);

                count++;
            }
        }
    }

    // get image RGB values
    public static int[] getRGB(Bitmap mBitmap, int x, int y) {
        int color = mBitmap.getPixel(x, y);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return new int[]{r, g, b};
    }

    //convert to grayscale image
    public Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    public Bitmap createScaleBitmap_opencv(Bitmap src, int dstWidth, int dstHeight) {
        Mat src_mat = new Mat();
        Utils.bitmapToMat(src, src_mat);
        Mat dst_mat = new Mat();
        Bitmap dst = Bitmap.createBitmap(dstWidth, dstHeight, Bitmap.Config.ARGB_8888);
        Imgproc.resize(src_mat, dst_mat, new Size(dstWidth, dstHeight));
        Utils.matToBitmap(dst_mat, dst);
        if (src != dst) { // 如果没有缩放，那么不回收
            src.recycle(); // 释放Bitmap的native像素数组
        }
        return dst;
    }

    /**
     * @param res
     * @param resId
     * @param reqWidth
     * @param reqHeight
     * @return
     * @description 从Resources中加载图片
     */
    public Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 设置成了true,不占用内存，只获取bitmap宽高
        BitmapFactory.decodeResource(res, resId, options); // 读取图片长宽，目的是得到图片的宽高
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight); // 调用上面定义的方法计算inSampleSize值
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeResource(res, resId, options); // 载入一个稍大的缩略图
        return createScaleBitmap(src, reqWidth, reqHeight, options.inSampleSize); // 通过得到的bitmap，进一步得到目标大小的缩略图
    }

    public Bitmap decodeSampledBitmapFromFile_opencv(String pathName, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        if (reqWidth == 0 && reqHeight == 0) {
            return BitmapFactory.decodeFile(pathName);
        } else {
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            Bitmap src = BitmapFactory.decodeFile(pathName, options);
            return createScaleBitmap_opencv(src, reqWidth, reqHeight);
        }
    }

    /**
     * 重叠两张图片
     * @param bmp
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap overlayBitmap(Bitmap bmp, int reqWidth, int reqHeight) {
        return overlayBitmap(bmp, 0, 0, reqWidth, reqHeight);
    }

    public static Bitmap overlayBitmap(Bitmap bmp, int x, int y, int reqWidth, int reqHeight) {
        Bitmap bmOverlay = Bitmap.createBitmap(reqWidth, reqHeight, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp, x, y, null);
        return bmOverlay;
    }

    public static Bitmap getUpperBodyImage(Bitmap image) {
        return Bitmap.createBitmap(
                image, 0, 0, image.getWidth(), image.getWidth());
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 动态渐变色
     *
     * @param startValue
     * @param endValue
     * @param fraction   差值0～1
     * @return
     */
    public static int evaluateColor(int startValue, int endValue, float fraction) {
        if (fraction <= 0) {
            return startValue;
        }
        if (fraction >= 1) {
            return endValue;
        }
        int startInt = startValue;
        int startA = (startInt >> 24) & 0xff;
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;

        int endInt = endValue;
        int endA = (endInt >> 24) & 0xff;
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;

        return ((startA + (int) (fraction * (endA - startA))) << 24)
                | ((startR + (int) (fraction * (endR - startR))) << 16)
                | ((startG + (int) (fraction * (endG - startG))) << 8)
                | ((startB + (int) (fraction * (endB - startB))));
    }

    /**
     * 用自身加密算法设置图片信息，使得非本app拍摄的照片无法识别
     * @param filepath
     * @param copyRight
     */
    public static void setExifCopyRight(String filepath, String copyRight) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);     //根据图片的路径获取图片的Exif
        } catch (IOException ex) {
            Log.e("Mine", "cannot read exif", ex);
        }
        exif.setAttribute(ExifInterface.TAG_COPYRIGHT, copyRight);
        try {
            exif.saveAttributes();         //最后保存起来
        } catch (IOException e) {
            Log.e("Mine", "cannot save exif", e);
        }
    }

    //获取exif
    public static String getExifCopyRight(String filepath){
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);    //想要获取相应的值：exif.getAttribute("对应的key")；比如获取时间：exif.getAttribute(ExifInterface.TAG_DATETIME);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exif.getAttribute(ExifInterface.TAG_COPYRIGHT);
    }

    /**
     * 转vector drawable为bitmap
     * @param context
     * @param drawableId
     * @return
     */
    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    /**
     * 获取图片加密信息
     * @param bitmap
     * @return
     */
    public static String getBitmapEncodeString(Bitmap bitmap){
        StringBuilder encodeStringBuilder = new StringBuilder();
        encodeStringBuilder.append(ENCODE_BITMAP_STRING);
        int[] rgbs = ImageUtil.getRGB(bitmap, 0, 0);
        for (int rgb : rgbs) {
            encodeStringBuilder.append(String.valueOf(rgb));
        }
        rgbs = ImageUtil.getRGB(bitmap, bitmap.getWidth() - 1, 0);
        for (int rgb : rgbs) {
            encodeStringBuilder.append(String.valueOf(rgb));
        }
        rgbs = ImageUtil.getRGB(bitmap, bitmap.getWidth() - 1, bitmap.getHeight() - 1);
        for (int rgb : rgbs) {
            encodeStringBuilder.append(String.valueOf(rgb));
        }
        rgbs = ImageUtil.getRGB(bitmap, 0, bitmap.getHeight() - 1);
        for (int rgb : rgbs) {
            encodeStringBuilder.append(String.valueOf(rgb));
        }
        return encodeStringBuilder.toString();
    }

    /**
     * 有的手机相机拍摄之后的图片人头朝下，需要翻转
     */
    public static void flipFrontImage(){
        Bitmap frontBitmap = ImageUtil.decodeSampledBitmapFromFile(
                Constants.FRONT_CACHE_URL, 0, 0);
        Bitmap frontBitmapOri = ImageUtil.decodeSampledBitmapFromFile(
                Constants.FRONTORI_CACHE_URL, 0, 0);
        frontBitmap = mirrorX(frontBitmap);
        frontBitmapOri = mirrorX(frontBitmapOri);

        FileUtil.saveBitmapToSD(frontBitmap,
                TozSDK.getContext().getExternalCacheDir().getAbsolutePath(),
                Constants.FRONT_CACHE_NAME,
                false,
                1);
        FileUtil.saveBitmapToSD(frontBitmapOri,
                TozSDK.getContext().getExternalCacheDir().getAbsolutePath(),
                Constants.FRONTORI_CACHE_NAME,
                false,
                0);
        frontBitmap.recycle();
        frontBitmapOri.recycle();
    }

    /**
     * 有的手机相机拍摄之后的图片人头朝下，需要翻转
     */
    public static void flipSideImage(){
        Bitmap sideBitmap = ImageUtil.decodeSampledBitmapFromFile(
                Constants.SIDE_CACHE_URL, 0, 0);
        Bitmap sideBitmapOri = ImageUtil.decodeSampledBitmapFromFile(
                Constants.SIDEORI_CACHE_URL, 0, 0);
        sideBitmap = mirrorX(sideBitmap);
        sideBitmapOri = mirrorX(sideBitmapOri);

        FileUtil.saveBitmapToSD(sideBitmap,
                TozSDK.getContext().getExternalCacheDir().getAbsolutePath(),
                Constants.SIDE_CACHE_NAME,
                false,
                1);
        FileUtil.saveBitmapToSD(sideBitmapOri,
                TozSDK.getContext().getExternalCacheDir().getAbsolutePath(),
                Constants.SIDEORI_CACHE_NAME,
                false,
                0);
        sideBitmap.recycle();
        sideBitmapOri.recycle();
    }

    /**
     * 上传图片至叶波服务器，但不保留
     */
    public static void uploadImage2YeBoServer(){
        try {
            UploadFile.upload(TozSDK.getContext(),
                    Constants.FRONTORI_CACHE_URL,
                    false,
                    new LoadDataCallBack() {
                        @Override
                        public void onDataListLoaded(List infoList) {
                        }
                        @Override
                        public void onDataLoaded(Object info) {
                            UploadFile.upload(TozSDK.getContext(),
                                    Constants.SIDEORI_CACHE_URL,
                                    false,
                                    new LoadDataCallBack() {
                                        @Override
                                        public void onDataListLoaded(List infoList) {
                                        }

                                        @Override
                                        public void onDataLoaded(Object info) {
                                        }

                                        @Override
                                        public void onDataLoadFailed() {
                                        }

                                        @Override
                                        public void onDataEmpty() {
                                        }
                                    });
                        }
                        @Override
                        public void onDataLoadFailed() {
                        }
                        @Override
                        public void onDataEmpty() {
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



































