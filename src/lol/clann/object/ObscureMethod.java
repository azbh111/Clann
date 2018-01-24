/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.object;

import java.util.Objects;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Administrator
 */
public class ObscureMethod extends ObscureField {

    public String desc;
    
    public ObscureMethod(String Unobscured, String Searge, String Obscured, String desc) {
        super(Unobscured, Searge, Obscured);
        this.desc = desc;
    }

    public ObscureMethod(Node no) {
        super(no);
        Node ob;
        NodeList cn = no.getChildNodes();
        for (int k = 0; k < cn.getLength(); k++) {
            ob = cn.item(k);
            if (ob.getNodeName().equals("Desc")) {
                desc = ob.getTextContent();
                break;
            }
        }
        //System.out.println(desc);
    }
    
    @Override
    public String toString() {
        return super.toString() + " " + desc;
    }
    @Override
    public int hashCode() {
        return Objects.hash(Searge,desc);
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof ObscureMethod){
            ObscureMethod om = (ObscureMethod) o;
            return Searge.equals(om.Searge) && desc.equals(om.desc);
        }else{
            return false;
        }
    }
}
