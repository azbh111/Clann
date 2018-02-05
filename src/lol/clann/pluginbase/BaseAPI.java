/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.pluginbase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;
import lol.clann.Utils.IOUtils;
import lol.clann.object.command.CEException;
import lol.clann.pluginbase.api.Configable;

/**
 *
 * @author zyp
 */
public class BaseAPI {
    /**
     * 遍历集合,遍历过的元素跟着清空
     * @param <T>
     * @param os
     * @param v 
     */
    public static <T> void loopRemoveCollection(Collection<T> os, Consumer<T> v) {
        Iterator<T> it = os.iterator();
        while (it.hasNext()) {
            v.accept(it.next());
            it.remove();
        }
    }
    /**
     * 遍历集合
     * @param <T>
     * @param os
     * @param v 
     */
    public static <T> void loopCollection(Collection<T> os, Consumer<T> v) {
        for (T o : os) {
            v.accept(o);
        }
    }

    /**
     * 输出指定文件
     *
     * @param plugin 插件
     * @param saveFolder 输出的文件夹
     * @param resoueceName 文件名
     * @param replace 如果存在,是否替换
     */
    public static void saveResource(BasePlugin plugin, String resoueceName, File saveFolder, boolean replace) {
        BaseAPI.notEmpty(resoueceName, "ResourcePath cannot be null or empty");
        resoueceName = resoueceName.replace('\\', '/');
        InputStream in = plugin.getResource(resoueceName);
        BaseAPI.notNull(in, "The embedded resource '" + resoueceName + "' cannot be found in " + plugin.getFile());
        File outFile = new File(saveFolder, resoueceName);
        try {
            if (outFile.exists() && !replace) {
                return;
            }
            if (!outFile.exists()) {
                outFile.createNewFile();
            }
            OutputStream out = new FileOutputStream(outFile);
            IOUtils.transform(in, out);
        } catch (IOException ex) {
            throw new CEException(ex);
        }
    }

    private static void error(String pMsg) {
        throw new IllegalArgumentException(pMsg);
    }

    /**
     * 对象不为null,如果null抛出异常
     *
     * @param pObj
     * 对象
     * @param pMsg
     * 异常消息
     * @param pParams
     * 消息参数
     */
    public static void notNull(Object pObj, String pMsg) {
        if (pObj == null) {
            error(pMsg);
        }
    }

    /**
     * 字符串不为空,如果为空抛出异常
     *
     * @param pStr
     * 字符串
     * @param pMsg
     * 异常消息
     * @param pParams
     * 消息参数
     */
    public static void notEmpty(String pStr, String pMsg) {
        if (pStr == null || pStr.isEmpty()) {
            error(pMsg);
        }
    }

    /**
     * 集合不为空,如果为空抛出异常
     *
     * @param pColl
     * 集合
     * @param pMsg
     * 异常消息
     * @param pParams
     * 消息参数
     */
    public static void notEmpty(Collection<?> pColl, String pMsg) {
        if (pColl == null || pColl.isEmpty()) {
            error(pMsg);
        }
    }

    /**
     * 集合不为false,如果为false抛出异常
     *
     * @param pResult
     * 结果
     * @param pMsg
     * 异常消息
     * @param pParams
     * 消息参数
     */
    public static void mustTrue(boolean pResult, String pMsg) {
        if (!pResult) {
            error(pMsg);
        }
    }
}
