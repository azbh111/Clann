/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.exception;

/**
 *
 * @author Administrator
 */
public class IllegalParmException extends NullPointerException {

    /**
     * Constructs a {@code NullPointerException} with no detail message.
     */
    public IllegalParmException() {
        super();
    }

    /**
     * Constructs a {@code NullPointerException} with the specified detail
     * message.
     *
     * @param s the detail message.
     */
    public IllegalParmException(String s) {
        super(s);
    }
}
