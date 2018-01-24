/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.object;

import org.w3c.dom.Node;

/**
 *
 * @author Administrator
 */
public class ObscureField {

    public String Unobscured;
    public String Searge;
    public String Obscured;
    
    public ObscureField(String Unobscured, String Searge, String Obscured) {
        this.Unobscured = Unobscured;
        this.Searge = Searge;
        this.Obscured = Obscured;
    }
    
    public ObscureField(Node ob) {
        Unobscured = ob.getAttributes().getNamedItem("Unobscured").getNodeValue();
        Searge = ob.getAttributes().getNamedItem("Searge").getNodeValue();
        Obscured = ob.getAttributes().getNamedItem("Obscured").getNodeValue();
    }
    
    
    @Override
    public String toString() {
        return Unobscured + " " + Searge + " " + Obscured;
    }
    
    @Override
    public int hashCode() {
        return Searge.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        return Searge.equals(o);
    }
}
