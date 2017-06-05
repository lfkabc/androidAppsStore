package com.example.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MyTools {

    private Map<String, String[]> apkMap = new HashMap<>();
    private List<String> apkList = new ArrayList<>();

    public void extractIconsFromFile(String apkFilesDir, String outputDir, String aaptFilePath) {
        File apksDir = new File(apkFilesDir);
        File iconDir = new File(outputDir);
        checkOrGenerateDir(apksDir);
        checkOrGenerateDir(iconDir);
        if (!checkExeFile(aaptFilePath)) return;

        for (String apk : apksDir.list()) {
            apkList.add(apk);
            String apkFilePath = apkFilesDir + File.separator + apk;
            String iconFilePath = outputDir + File.separator + (apk.substring(0, apk.indexOf(".apk")) + ".png");
            doExtractIconFromAPKFile(apk, apkFilePath, iconFilePath, aaptFilePath);

        }

    }

    private void doExtractIconFromAPKFile(String apkName, String apkPath, String outPath, String aaptFile) {
        FileInputStream fis = null;
        FileOutputStream fos;
        ZipInputStream zis = null;
        File apkFile = new File(apkPath);
        String iconName = getIconName(apkName, apkPath, aaptFile);
        try {
            fis = new FileInputStream(apkFile);
            zis = new ZipInputStream(fis);
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                String name = zipEntry.getName().toLowerCase();
                if ((name.endsWith("/" + iconName) && name.contains("drawable") && name.contains("res")) ||
                        (name.endsWith("/" + iconName) && name.contains("raw") && name.contains("res"))) {
                    fos = new FileOutputStream(new File(outPath));
                    byte[] buffer = new byte[1024];
                    int n = 0;
                    while ((n = zis.read(buffer, 0, buffer.length)) != -1) {
                        fos.write(buffer, 0, n);
                    }
                    break;
                }
            }
            zis.closeEntry();
            System.out.println("generate icon success: " + outPath);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (zis != null) {
                    zis.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getIconName(String apkName, String apkPath, String aaptPath) {
        String iconName = "";
        String displayName = "";
        try {
            Runtime rt = Runtime.getRuntime();
            String cmd = aaptPath + " d badging " + apkPath;
            Process proc = rt.exec(cmd);
            InputStream inputStream = proc.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line = null;
            String md5 = getMD5Sum(apkPath);
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("application:")) {//application: label='UC浏览器' icon='res/drawable-hdpi/icon.png'
                    iconName = line.substring(line.lastIndexOf("/") + 1, line.lastIndexOf("'")).trim().toLowerCase();
                    displayName = line.substring(line.indexOf("label=") + 1, line.indexOf("icon=") - 2);
                    apkMap.put(apkName, new String[]{displayName, md5});
                    System.out.println("display name : " + displayName);
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return iconName;
    }

    private String getMD5Sum(String apkFilePath) {
        String value = null;
        FileInputStream in = null;
        File file = new File(apkFilePath);
        try {
            in = new FileInputStream(file);
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }

    private void checkOrGenerateDir(File dir) {
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private boolean checkExeFile(String file) {
        File tempFile = new File(file);
        return tempFile.exists() && tempFile.canExecute();
    }

    private void checkOrGenerateFile(String file) {
        File apkFiledirsFile = new File(file);
        if (!apkFiledirsFile.exists()) {
            if(!apkFiledirsFile.getParentFile().exists()){
                apkFiledirsFile.getParentFile().mkdirs();
            }
            try {
                apkFiledirsFile.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }


    public void generateJsonFile(String jsonOutputFile) {
        checkOrGenerateFile(jsonOutputFile);

        String urlPre = "https://raw.githubusercontent.com/lfkabc/androidAppsStore/master/raw/";
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .create();
        JsonWriter writer = null;
        try {
            writer = new JsonWriter(new OutputStreamWriter(new FileOutputStream(jsonOutputFile), "UTF-8"));
            writer.beginArray();

            for (String apk : apkList) {
                String iconUrl = urlPre + "icon/" + (apk.substring(0, apk.indexOf(".apk")) + ".png");
                String url = urlPre + "apks/" + apk;
                Item item = new Item(apkMap.get(apk)[0], iconUrl, url, apkMap.get(apk)[1]);
                gson.toJson(item, Item.class, writer);
            }

            writer.endArray();
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) {
        /*String apkFileDir = "D:\\androidAppsStore\\raw\\apks";
        String iconOutputDir = "D:\\androidAppsStore\\raw\\icons";
        String aaptFile = "D:\\androidAppsStore\\raw\\aapt.exe";
        String outputJsonfile = "D:\\androidAppsStore\\raw\\apks_info.json";*/


        String apkFileDir = "../raw/apks";//"D:\\androidAppsStore\\raw\\apks";
        String iconOutputDir = "../raw/icons";//"D:\\androidAppsStore\\raw\\icons";
        String aaptFile = "../raw/aapt.exe";//"D:\\androidAppsStore\\raw\\aapt.exe";
        String outputJsonfile = "../raw/apks_info.json";//"D:\\androidAppsStore\\raw\\apks_info.json";

        File file = new File("kkkkkkkk.txt");
        if(!file.exists()) try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MyTools tools = new MyTools();
        tools.extractIconsFromFile(apkFileDir, iconOutputDir, aaptFile);
        tools.generateJsonFile(outputJsonfile);
    }
}
