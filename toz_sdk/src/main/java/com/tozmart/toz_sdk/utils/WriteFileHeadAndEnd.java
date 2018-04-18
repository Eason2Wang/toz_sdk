package com.tozmart.toz_sdk.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by tracy on 2018/2/4.
 */

public class WriteFileHeadAndEnd {
    public static void writeHead(String filepath, String encodeString){
        File file = new File(filepath);
        if(!file.exists() && !file.isDirectory()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (RandomAccessFile raw = new RandomAccessFile(file,"rw")){
            raw.seek(0l);
            //写文件头时候需要预留占位符
            raw.write(encodeString.getBytes());
            raw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeEnd(String filepath, String encodeString){
        File file = new File(filepath);
        if(!file.exists() && !file.isDirectory()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (RandomAccessFile raw = new RandomAccessFile(file,"rw")){
            raw.seek(raw.length());
            raw.write(('\n' + encodeString).getBytes());
            raw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
