/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.object.command;

/**
 *
 * @author zyp
 */
public class CEException extends RuntimeException {

    public CEException(String message) {
        super(message);
    }

    public CEException(Throwable e) {
        super(e);
    }

    public CEException(String message, Throwable e) {
        super(message, e);
    }
}
