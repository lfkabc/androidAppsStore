package com.example.luo.androidappsstore;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ListActivity;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonWriter;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends Activity {
    String TAG = MainActivity.class.getSimpleName();
    private static final String JSON_FILE = "https://raw.githubusercontent.com/lfkabc/androidAppsStore/master/raw/apks_info.json";
    private Handler mHandler;
    private static final int MSG_UPDATE_GRIDVEIW = 1;

    private GridAdapter mAdapter;
    private GridView mGridView;

    @Override
    public void onCreate(Bundle icicle)   {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);
        mGridView = (GridView)findViewById(R.id.gridview);
        mHandler = new UpdateUIHandler();

        new Thread(){
            @Override
            public void run() {
                ArrayList<Item> itemList = paseJsonfile(downLoadFile(JSON_FILE));
                mAdapter = new GridAdapter(MainActivity.this, itemList);
                mHandler.sendEmptyMessage(MSG_UPDATE_GRIDVEIW);

            }
        }.start();
    }

    private class UpdateUIHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_UPDATE_GRIDVEIW:
                    mGridView.setAdapter(mAdapter);
            }
        }
    }


    private String downLoadFile(String fileUrl) {
        String fileName = Environment.DIRECTORY_DOWNLOADS + File.separator + fileUrl.split("/")[fileUrl.split("/").length -1];
        final File file = new File(fileName);
        final Request request = new Request.Builder().url(fileUrl).build();
        OkHttpClient client = new OkHttpClient();
        final Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    long total = response.body().contentLength();
                    Log.e(TAG, "total------>" + total);
                    long current = 0;
                    is = response.body().byteStream();
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        current += len;
                        fos.write(buf, 0, len);
                        Log.e(TAG, "total:current------>" +  total + ":" + current);
                    }
                    fos.flush();
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.toString());
                    }
                }
            }
        });

        return  fileName;
    }

    private ArrayList<Item> paseJsonfile(String jsonFileName){
        ArrayList<Item> itemlist = new ArrayList<Item>();
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .create();
        JsonReader reader = null;
        try {
            reader = new JsonReader(new InputStreamReader(new FileInputStream(jsonFileName), "UTF-8"));
            reader.beginArray();
            gson.fromJson(reader, Item.class);
            while (reader.hasNext()) {
                itemlist.add((Item)gson.fromJson(reader, Item.class));
            }

            reader.endArray();
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return itemlist;
    }
}
