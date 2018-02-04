/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.object.bean;

import java.lang.annotation.*;

/**
 * 用于标识Beans中的属性,从而选择不同的子类进行构造
 */
@Target(value = {ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldSelector {

    String[] value();//参数的值

    Class[] clazz();//子类
}
