package com.tozmart.toz_sdk.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import com.tozmart.toz_sdk.TozSDK;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import static com.tozmart.toz_sdk.contants.HttpsAuthString.HTTPS_SERVER_PASSWORD;
import static com.tozmart.toz_sdk.contants.HttpsAuthString.HTTPS_SERVER_USER_NAME;

public class FileUtil {
    private final String TAG = "FileUtil";

    /**
     * 判断SDCard是否存在 [当没有外挂SD卡时，内置ROM也被识别为存在sd卡]
     *
     * @return
     */
    public static boolean isSdCardExist() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    //使用BufferedWriter写入文件
    public static void writeTxt_bw(String filePath, String info) {

        try {
            File file = new File(filePath);
            //第二个参数意义是说是否以append方式添加内容
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            bw.write(info);
            bw.flush();
            bw.close();
            System.out.println("写入成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> readText(String path) {
        try {
            FileReader fr = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(fr);
            String str;
            ArrayList<String> newStr = new ArrayList<String>();
            while ((str = bufferedReader.readLine()) != null) {
                newStr.add(str);
            }
            fr.close();
            bufferedReader.close();
            return newStr;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty so now it can be smoked
        return dir.delete();
    }

    /**
     * 调用系统的扫描器来添加你的照片到媒体扫描器(Media Provider)的数据库中，
     * 使得这些照片可以被系统的相册应用或者其它APP可访问
     *
     * @param context
     * @param photoPath
     */
    public static void galleryAddPic(Context context, String photoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(photoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    /**
     * 读取法向量的txt文件
     *
     * @param path
     * @return
     */
    public static ArrayList<Float> readNormalText(String path) {
        try {
            FileReader fr = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(fr);
            String str;
            ArrayList<Float> values = new ArrayList<>();
            StringTokenizer newStr;
            while ((str = bufferedReader.readLine()) != null) {
                newStr = new StringTokenizer(str);
                while (newStr.hasMoreTokens()) {
                    values.add(Float.parseFloat(newStr.nextToken()));
                }
            }
            bufferedReader.close();
            return values;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    //使用FileOutputStream写入文件
//	public void writeTxt_fos(String info){
//		try {
//			File file = new File("");
//	        FileOutputStream fos = new FileOutputStream(file);
//            fos.write(info.getBytes());
//            fos.close();
//			System.out.println("写入成功：");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

    public static byte[] readFileToByteArray(String path) {
        File file = new File(path);
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * 初始化保存路径
     *
     * @return
     */
    private void initPath(String path) {

        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();
        }
    }

    /**
     * 保存Bitmap到sdcard
     * @param bitmap
     * @param dir
     * @param name
     * @param isShowPhotos 把文件插入到系统图库
     * @param type 0:jpg;1:png
     * @return
     */
    public static boolean saveBitmapToSD(Bitmap bitmap, String dir, String name, boolean isShowPhotos, int type) {
        File path = new File(dir);
        if (!path.exists()) {
            path.mkdirs();
        }
        File file = new File(path + "/" + name);
        if (file.exists()) {
            file.delete();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return true;
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            if (type == 1) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100,
                        fileOutputStream);
            } else {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                        fileOutputStream);
            }
            fileOutputStream.flush();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 其次把文件插入到系统图库
        if (isShowPhotos) {
            try {
                MediaStore.Images.Media.insertImage(TozSDK.getContext().getContentResolver(),
                        file.getAbsolutePath(), name, null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // 最后通知图库更新
            TozSDK.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file)));
        }

        return true;
    }

    //从Assets中读取图片
    public Bitmap getImageFromAssetsFile(String fileName, Context context) {
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

    private static InputStream OpenHttpConnection(String urlString)
            throws IOException {
        InputStream in = null;
        int response = -1;

        URL url = new URL(urlString);
        HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();

        if (!(httpConn instanceof HttpURLConnection))
            throw new IOException("Not an HTTP connection");

        try {
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.setRequestProperty( "Accept-Encoding", "" );
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

    public static String downloadTxt(String URL) {
        InputStream in = null;
        StringBuilder txt = new StringBuilder();
        try {
            in = OpenHttpConnection(URL);
            if (in == null) {
                return null;
            }
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = r.readLine()) != null) {
                txt.append(line);
            }
            in.close();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return txt.toString();
    }

    public String DownloadNormalsTxt(String URL) {
        InputStream in = null;
        StringBuilder txt = new StringBuilder();
        StringTokenizer newStr;
        try {
            in = OpenHttpConnection(URL);
            if (in == null) {
                return null;
            }
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = r.readLine()) != null) {
                newStr = new StringTokenizer(line);
                while (newStr.hasMoreTokens()){
                    txt.append(newStr.nextToken());
                    txt.append(", ");
                }
            }
            in.close();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return txt.toString().substring(0, txt.length() - 3);
    }

    /**
     * 通过后缀去查找文件
     * @param extension
     * @return
     */
    public static File findFileByExtension(File findFile, String extension){
        File file = null;
        File files[] = findFile.listFiles();
        if(files != null){
            for (File f : files){
                if(f.isDirectory()){
                    continue;
                }else{
                    if (f.getAbsolutePath().contains(extension)){
                        file = f;
                        break;
                    }
                }
            }
        }
        return file;
    }

    /**
     * 通过后缀去查找文件
     * @param extension
     * @return
     */
    public static List<File> findFilesByExtension(File findFile, String extension){
        List<File> results = new ArrayList<>();
        File files[] = findFile.listFiles();
        if(files != null){
            for (File f : files){
                if(f.isDirectory()){
                    continue;
                }else{
                    if (f.getAbsolutePath().contains(extension)){
                        results.add(f);
                    }
                }
            }
        }
        return results;
    }

    /**
     * 下载zip
     * @param url
     * @param rootFolder
     * @param filePath
     * @return
     */
    public static boolean downloadZip(String url, String rootFolder, String filePath, boolean deleteZip){
        HttpURLConnection connection = null;
        int bytesCopied = 0;
        try {
            URL mUrl = new URL(url);
            connection = (HttpURLConnection) mUrl.openConnection();
            connection.setRequestProperty("Authorization", "Basic " +
                    Base64.encodeToString((HTTPS_SERVER_USER_NAME + ":" + HTTPS_SERVER_PASSWORD).getBytes(), Base64.NO_WRAP));
            connection.setUseCaches(false);
            connection.connect();
            File mFile = new File(filePath);
            if(!mFile.getParentFile().exists()){
                mFile.getParentFile().mkdirs();
            }
            int length = connection.getContentLength();
            if(mFile.exists()&&length == mFile.length()){
                Log.d("downloadZip", "file "+mFile.getName()+" already exits!!");
            }
            FileOutputStream mOutputStream = new FileOutputStream(mFile);
            bytesCopied =copy(connection.getInputStream(),mOutputStream);
            if(bytesCopied!=length&&length!=-1){
                Log.e("downloadZip", "Download incomplete bytesCopied="+bytesCopied+", length"+length);
            }
            mOutputStream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (Exception e){
            e.printStackTrace();
        }
        return unzip(filePath, rootFolder, deleteZip, null);
    }

    /**
     * 下载zip
     * @param srcUrl
     * @param filePath
     * @return
     */
    public static boolean downloadZip(String srcUrl, String filePath){
        HttpURLConnection connection = null;
        int bytesCopied = 0;
        try {
            URL mUrl = new URL(srcUrl);
            connection = (HttpURLConnection) mUrl.openConnection();
            connection.setRequestProperty("Authorization", "Basic " +
                    Base64.encodeToString((HTTPS_SERVER_USER_NAME + ":" + HTTPS_SERVER_PASSWORD).getBytes(), Base64.NO_WRAP));
            connection.setUseCaches(false);
            connection.connect();
            File mFile = new File(filePath);
            if(!mFile.getParentFile().exists()){
                mFile.getParentFile().mkdirs();
            }
            int length = connection.getContentLength();
            if(mFile.exists()&&length == mFile.length()){
                Log.d("downloadZip", "file "+mFile.getName()+" already exits!!");
            }
            FileOutputStream mOutputStream = new FileOutputStream(mFile);
            bytesCopied =copy(connection.getInputStream(),mOutputStream);
            if(bytesCopied!=length&&length!=-1){
                Log.e("downloadZip", "Download incomplete bytesCopied="+bytesCopied+", length"+length);
            }
            mOutputStream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (Exception e){
            e.printStackTrace();
        }
        return unzip(filePath, "", true, null);
    }

    public static int copy(InputStream input, OutputStream output){
        byte[] buffer = new byte[1024*8];
        BufferedInputStream in = new BufferedInputStream(input, 1024*8);
        BufferedOutputStream out  = new BufferedOutputStream(output, 1024*8);
        int count =0,n=0;
        try {
            while((n=in.read(buffer, 0, 1024*8))!=-1){
                out.write(buffer, 0, n);
                count+=n;
            }
            out.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            try {
                out.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                in.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return count;
    }

    /**
     * 解压缩文件
     * @param in
     * @param out
     * @param deleteOriZip 是否删除原zip文件
     * @return
     */
    public static boolean unzip(String in, String out, boolean deleteOriZip, ProgressDialog progressDialog){
        File mInput = new File(in);
        File mOutput;
        if (out.equals("")) {
            mOutput = new File(in.substring(0, in.indexOf(".zip")));
        }else{
            mOutput = new File(out);
        }
        long extractedSize = 0L;
        Enumeration<ZipEntry> entries;
        ZipFile zip = null;
        try {
            zip = new ZipFile(mInput);
            long uncompressedSize = getOriginalSize(zip);

            entries = (Enumeration<ZipEntry>) zip.entries();
            while(entries.hasMoreElements()){
                ZipEntry entry = entries.nextElement();
                if(entry.isDirectory()){
                    continue;
                }
                File destination = new File(mOutput, entry.getName());
                if(!destination.getParentFile().exists()){
                    destination.getParentFile().mkdirs();
                }

                FileOutputStream outStream = new FileOutputStream(destination);
                extractedSize += FileUtil.copy(zip.getInputStream(entry),outStream);
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.setProgress((int)(extractedSize / uncompressedSize));
                }
                outStream.close();
            }
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.setProgress(100);
            }
            if(deleteOriZip) {
                if (mInput.exists()){
                    mInput.delete();
                }
            }
        } catch (ZipException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }finally{
            try {
                zip.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return true;
    }

    private static long getOriginalSize(ZipFile file){
        Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) file.entries();
        long originalSize = 0l;
        while(entries.hasMoreElements()){
            ZipEntry entry = entries.nextElement();
            if(entry.getSize()>=0){
                originalSize+=entry.getSize();
            }
        }
        return originalSize;
    }

    /**
     * 清空cache
     * @param context
     */
    public static void deleteCache(Context context) {
        try {
            File dir = context.getExternalCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过后缀去cache dir查找文件并删除
     * @param context
     * @param extensions
     */
    public static void deleteCacheFileByExtensions(Context context, String... extensions){
        try {
            File dir = context.getExternalCacheDir();
            File files[] = dir.listFiles();
            if(files != null){
                for (File f : files){
                    if(f.isDirectory()){
                        continue;
                    }else{
                        for (String extension : extensions) {
                            if (f.getAbsolutePath().contains(extension)) {
                                f.delete();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据Uri获取文件的绝对路径，解决Android4.4以上版本Uri转换
     *
     * @param activity
     * @param fileUri
     */
    @TargetApi(19)
    public static String getFileAbsolutePathFromUri(Activity activity, Uri fileUri) {
        if (activity == null || fileUri == null)
            return null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(activity, fileUri)) {
            if (isExternalStorageDocument(fileUri)) {
                String docId = DocumentsContract.getDocumentId(fileUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(fileUri)) {
                String id = DocumentsContract.getDocumentId(fileUri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(activity, contentUri, null, null);
            } else if (isMediaDocument(fileUri)) {
                String docId = DocumentsContract.getDocumentId(fileUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[] { split[1] };
                return getDataColumn(activity, contentUri, selection, selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(fileUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(fileUri))
                return fileUri.getLastPathSegment();
            return getDataColumn(activity, fileUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(fileUri.getScheme())) {
            return fileUri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String[] projection = { MediaStore.Images.Media.DATA };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}



















