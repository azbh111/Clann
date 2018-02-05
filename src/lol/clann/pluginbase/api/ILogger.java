/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.pluginbase.api;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zyp
 */
public interface ILogger {

    public Logger getLogger();

    public default void log(String message) {
        getLogger().log(Level.INFO, message);
    }

    public default void logWarning(String message) {
        getLogger().log(Level.WARNING, message);
    }

    public default void logError(String message) {
        getLogger().log(Level.SEVERE, message);
    }
}
