/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.object.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 用于标注Map,Set,List及他们的子类,指明存储元素的类型
 * 其中Map指的是value
 * @author zyp
 */
@Target(value = {ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ComponentType {
    Class clazz();
}
