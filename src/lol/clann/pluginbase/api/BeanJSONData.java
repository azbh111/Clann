/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.pluginbase.api;

import java.io.File;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import lol.clann.Utils.BeanUtils;
import org.json.JSONObject;

/**
 *
 * @author zyp
 */
public abstract class BeanJSONData extends JSONData {

    public BeanJSONData(File f) {
        this(f, null);
    }

    public BeanJSONData(File f, String encoding) {
        super(f, encoding);
    }

    @Override
    protected JSONObject save0() {
        return BeanUtils.toJson(this);
    }

    @Override
    protected void reloadData0(JSONObject json) {
        try {
            BeanUtils.build(this, json);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
