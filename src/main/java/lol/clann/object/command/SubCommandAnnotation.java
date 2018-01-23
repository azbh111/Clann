/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.object.command;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SubCommandAnnotation {

    /**
     * 参数(变量)  [可选变量]
     * @return 
     */
    String args() default ""; //命令参数

    /**
     * 介绍
     * @return 
     */
    String[] des();//介绍

    /**
     * 是否需要OP权限
     * @return 
     */
    boolean needOp() default true;  //false:给非OP玩家执行

    /**
     * 所需权限
     * @return 
     */
    String permission() default ""; //所需权限

    /**
     * 是否必须玩家才能执行
     * @return 
     */
    boolean mustPlayer() default false;//true:必须玩家才能执行
}
