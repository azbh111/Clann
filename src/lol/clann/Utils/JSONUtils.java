/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.Utils;

import java.io.File;
import java.nio.charset.Charset;
import org.json.JSONObject;

/**
 *
 * @author zyp
 */
public class JSONUtils {

    /**
     * 用指定编码从文件读取json
     *
     * @param f
     * @param encoding
     *
     * @return
     */
    public static JSONObject readJsonFromFile(File f, String encoding) {
        String s = FileUtils.readContent(f, Charset.forName(encoding));
        if (s != null && s.length() >= 2) {
            return new JSONObject(s);
        } else {
            return null;
        }
    }

    /**
     * 用UTF-编码从文件读取json
     *
     * @param f
     *
     * @return
     */
    public static JSONObject readJsonFromFile(File f) {
        return readJsonFromFile(f, "UTF-8");
    }

    /**
     * 用UTF-8编码写入文件
     *
     * @param json
     * @param f
     */
    public static void writeJsonToFile(JSONObject json, File f) {
        writeJsonToFile(json, f, "UTF-8");
    }

    /**
     * 指定编码写入文件
     *
     * @param json
     * @param f
     */
    public static void writeJsonToFile(JSONObject json, File f, String encoding) {
        FileUtils.writeContent(f, false, json.toString(), Charset.forName(encoding));
    }
}
