/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.pluginbase.api;

import java.io.*;
import java.nio.charset.Charset;
import lol.clann.pluginbase.BaseAPI;
import lol.clann.Utils.FileUtils;
import lol.clann.Utils.JSONUtils;
import org.json.JSONObject;

/**
 *
 * @author zyp
 */
public abstract class JSONData {

    private File file;
    private Charset encoding = Charset.forName("UTF-8");

    public JSONData(File f, String encoding) {
        file = f;
        this.encoding = Charset.forName(encoding);
        reloadData();
    }

    public JSONData(File f) {
        file = f;
    }

    /**
     * 保存模块数据
     */
    public final void save() {
        JSONObject json = save0();
        JSONUtils.writeJsonToFile(json, file, encoding.name());
    }

    /**
     * 将数据转换为json,子类使用数据的话,应覆盖此方法
     * 子类需要保存数据时,应重写
     *
     * @return
     */
    protected abstract JSONObject save0();

    /**
     * 需要重载数据的模块覆盖此方法
     */
    public final void reloadData() {
        JSONObject json = getData();
        reloadData0(json);
    }

    /**
     * 子类需要数据存储时,应重写
     *
     * @param json
     */
    protected abstract void reloadData0(JSONObject json);

    private JSONObject getData() {
        BaseAPI.notNull(file, "数据文件不存在");
        String content = FileUtils.readContent(file, encoding);
        JSONObject json = new JSONObject(content);
        return json;
    }
}
