/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.object;

import java.util.*;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Administrator
 */
public class ObscureClass {
    
    public String Unobscured;
    public String Obscured;
    public String FullName;
    
    public Set<ObscureField> fields = new HashSet();
    public Set<ObscureMethod> methods = new HashSet();
    
    public ObscureClass(String Unobscured, String Searge, String Obscured) {
        this.Unobscured = Unobscured;
        this.FullName = Searge;
        this.Obscured = Obscured;
    }
    
    public ObscureClass(Node node) {
        Unobscured = node.getAttributes().getNamedItem("Unobscured").getNodeValue();
        Obscured = node.getAttributes().getNamedItem("Obscured").getNodeValue();
        FullName = node.getAttributes().getNamedItem("FullName").getNodeValue().replaceAll("/", ".");
        NodeList cn = node.getChildNodes();
        for (int k = 0; k < cn.getLength(); k++) {
            Node n = cn.item(k);
            if (n.getNodeName().equals("Field")) {
                fields.add(new ObscureField(n));
            } else if (n.getNodeName().equals("Method")) {
                methods.add(new ObscureMethod(n));
            }
        }
    }
    
    public String getMethodSeargeName(String name, String desc) {
        for (ObscureMethod of : methods) {
            if (name.equals(of.Unobscured) && desc.equals(of.desc)) {
                return of.Searge;
            }
        }
        return null;
    }
    
    public String getFieldSeargeName(String name) {
        for (ObscureField of : fields) {
            if (name.equals(of.Unobscured)) {
                return of.Searge;
            }
        }
        return null;
    }
    
    public int getFieldSize() {
        return fields.size();
    }
    
    public int getMethodSize() {
        return methods.size();
    }
    
    public int getSize() {
        return fields.size() + methods.size();
    }
    
    @Override
    public int hashCode() {
        return FullName.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        return FullName.equals(o);
    }
}
