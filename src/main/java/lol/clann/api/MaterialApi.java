/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.api;

import java.util.Map;
import org.bukkit.Material;

/**
 *
 * @author zyp
 */
public class MaterialApi {

    //服务器所有ID与name对应关系
    public static Material[] byId = ReflectApi.Material_byId.of(null).get();
    /**
     * 由于某些MOD作者的恶习 同一个Material可能对应多个String，建议使用byId
     */
    public static final Map<String, Material> BY_NAME = ReflectApi.Material_BY_NAME.of(null).get();

}
