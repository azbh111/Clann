
package lol.clann.api;

//自定义类加载器,可动态加载类
public class newClassLoader extends ClassLoader {
    public Class getClassByByteArray(byte[]  bytes) throws Exception {
        return super.defineClass(null, bytes, 0, bytes.length);
    }
}
