package lol.clann.object.nbt;

import java.util.regex.Pattern;
import static lol.clann.object.nbt.utils.NBTUtils.nbtUtils;

/**
 * 15.01.13 4:39
 *
 * @author DPOH-VAR
 */
public abstract class NBTTagDatable<T> extends NBTBase {

    public static String regexByte = "[-+]?[0-9]+[b|B]";
    public static String regexByteArray = "\\[[\\d-,]*\\][b|B]";
    public static String regexShort = "[-+]?[0-9]+[s|S]";
    public static String regexInt = "[-+]?[0-9]+";
    public static String regexIntArray = "\\[[\\d-,]*\\]";
    public static String regexLong = "[-+]?[0-9]+[l|L]";
    public static String regexFloat = "[-+]?[0-9]*\\\\.?[0-9]+[f|F]";
    public static String regexDouble = "[-+]?[0-9]*\\\\.?[0-9]+";
    public static String regexString = "\".*\"";

    public static NBTBase wrap(String value) {
        value = value.trim();
        if (value.matches(regexString)) {//String
            return NBTBase.wrap(value.substring(1, value.length() - 1).replace("\\\"", "\""));
        } else if (value.matches(regexByte)) {//byte
            return NBTBase.wrap(Byte.valueOf(value.substring(0, value.length() - 1)));
        } else if (value.matches(regexByteArray)) {//byte[]
            String[] ss = value.substring(1, value.length() - 2).split(",");
            byte[] bs = new byte[ss.length];
            for (int i = 0; i < ss.length; i++) {
                bs[i] = Byte.valueOf(ss[1]);
            }
            return NBTBase.wrap(bs);
        } else if (value.matches(regexShort)) {//short
            return NBTBase.wrap(Short.valueOf(value.substring(0, value.length() - 1)));
        } else if (value.matches(regexInt)) {//int
            return NBTBase.wrap(Integer.valueOf(value.substring(0, value.length() - 1)));
        } else if (value.matches(regexIntArray)) {  //int[]
            String[] ss = value.substring(1, value.length() - 1).split(",");
            int[] bs = new int[ss.length];
            for (int i = 0; i < ss.length; i++) {
                bs[i] = Integer.valueOf(ss[1]);
            }
            return NBTBase.wrap(bs);
        } else if (value.matches(regexLong)) {//long
            return NBTBase.wrap(Long.valueOf(value.substring(0, value.length() - 1)));
        } else if (value.matches(regexFloat)) {//float
            return NBTBase.wrap(Float.valueOf(value.substring(0, value.length() - 1)));
        } else if (value.matches(regexDouble)) {//double
            return NBTBase.wrap(Double.valueOf(value.substring(0, value.length() - 1)));
        }
        return null;
    }
    
    NBTTagDatable(Object handle) {
        super(handle);
    }

    @SuppressWarnings("unchecked")
    public T get() {
        return (T) nbtUtils.getValue(handle);
    }

    public void set(T value) {
        nbtUtils.setValue(handle, value);
    }
}
