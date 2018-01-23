/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.object;

/**
 *
 * @author zyp
 */
public class ID {

    public int id;
    public short data;

    public ID(int id, short data) {
        this.id = id;
        this.data = data;
    }

    @Override
    public String toString() {
        return id + ":" + data;
    }

    @Override
    public int hashCode() {
        return id * 31 + data;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ID other = (ID) obj;
        if (this.id != other.id) {
            return false;
        }
        if (this.data != other.data) {
            return false;
        }
        return true;
    }
}
