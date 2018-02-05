/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zyp
 */
public class ClannAPI {
    
    private static final Logger logger = Logger.getLogger("Clann");
    public static void log(String s) {
        logger.log(Level.INFO, s);
    }

    public static void logError(String s) {
        logger.log(Level.SEVERE, s);
    }

    public static void logWarning(String s) {
        logger.log(Level.WARNING, s);
    }
}
