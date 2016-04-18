package com.master.henrik.shared;

import android.content.Context;
import android.util.Log;

import com.master.henrik.statics.FilePaths;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Henri on 20.10.2015.
 */
public class StorageHandler {

    //TODO
    private String outputFileName = FilePaths.tempStorageFileName;


    private final String TAG = "StorageHandler";
    File outputPath;
    File outputFile;
    FileOutputStream outputStream;

    Context _ctx;

    public StorageHandler(Context ctx){
        outputPath = ctx.getFilesDir();
        outputFile = new File(outputPath, outputFileName);

        _ctx = ctx;
    }

    public void writeToFile(byte[] data){
        try {
            if(outputStream == null){
                outputStream = new FileOutputStream(outputFile, false);
            }
            outputStream.write(data);
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.toString());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }

    public boolean deleteFile(String fileName){
        if(outputStream != null){
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File file = new File(outputPath, fileName);
        if(file.exists()){
            file.delete();
        }
        return !file.exists();
    }

    public void closeOutputStream(){
        if(outputStream != null){
            try {
                outputStream.close();
                outputStream = null;
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    public String readFromFileAppDir(String name){
        String content = "";
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(outputPath, name));
            byte[] input = new byte[fileInputStream.available()];
            while (fileInputStream.read(input) != -1) {
                Log.d(TAG, "Reading");
            }
            content += new String(Converter.ByteArrayToHexString(input));
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.toString());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        return content;
    }


    public String readFromFileInAssets(String name){
        String content = "";
        try {
            InputStream inputStream = _ctx.getAssets().open(name);

            byte[] input = new byte[inputStream.available()];
            while (inputStream.read(input) != -1) {}
            content += new String(input);
            inputStream.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.toString());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        return content;
    }


}
