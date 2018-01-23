/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.api;

import lol.clann.exception.UnexpectedException;

/**
 *
 * @author Administrator
 */
public class ExceptionApi {

    public static void NotFalse(boolean b, String message) {
        if (!b) {
            throw new UnexpectedException(message);
        }
    }

    public static void NotFalse(boolean b) {
        if (!b) {
            throw new UnexpectedException();
        }
    }
}
