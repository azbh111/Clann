/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import lol.clann.api.FileApi;
import lol.clann.object.command.CEException;

/**
 *
 * @author zyp
 */
public class FileUtils {

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("C:\\Users\\zyp\\Desktop\\新建文本文档.txt"))));
        String s = "";
        String ss;
        while ((ss = br.readLine()) != null) {
            s += ss;
        }
        System.out.println(s);
    }

    /**
     * 根据uuid返回路径,层数与长度之积应不大于32
     *
     * @param uuid
     * @param count 目录层数
     * @param perLength 每层目录名字长度
     *
     * @return
     */
    public static String getPathByUUID(UUID uuid, int count, int perLength) {
        String uid = uuid.toString().replace("-", "");
        if (perLength * count > uid.length()) {
            throw new RuntimeException("单位长度与个数之积应小于uuid长度");
        }
        String s = "";
        for (int i = 0; i < count; i++) {
            s += uid.substring(perLength * i, perLength * i + 2) + File.separator;
        }
        s += uuid.toString();
        return s;
    }

    /**
     * 从uuid指定的位置读取数据
     *
     * @param folder
     * @param uuid
     *
     * @return
     */
    public static File getFileByUUID(File folder, UUID uuid) {
        String s = getPathByUUID(uuid, 4, 2);
        File f = new File(folder, s);
        return f;
    }

    /**
     * 在uuid指定的位置存储数据
     *
     * @param folder
     * @param uuid uuid
     * @param data
     *
     * @return
     */
    public static File saveByUUID(File folder, byte[] data, UUID uuid) {
        String s = getPathByUUID(uuid, 4, 2);
        File f = getFile(folder.getPath() + File.separator + s, true);
        writeData(f, false, data);
        return f;
    }

    /**
     * 字符串写入文件
     *
     * @param f
     * @param append 是否追加到文件末尾
     * @param content 字符串
     * @param encoding 编码
     */
    public static void writeContent(File f, boolean append, String content, Charset encoding) {
        writeData(f, append, content.getBytes(encoding));
    }

    /**
     * 数据写入文件,文件必须存在
     *
     * @param f
     * @param append 是否追加到文件末尾
     * @param data
     */
    public static void writeData(File f, boolean append, byte[] data) {
        f = getFile(f.getPath(), true);//创建文件
        try (OutputStream os = new FileOutputStream(f, append)) {
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            IOUtils.transform(in, os);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static byte[] readData(File f) {
        try {
            InputStream is = new FileInputStream(f);
            return IOUtils.readData(is);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 以指定编码读取文件内的字符串
     *
     * @param f
     * @param encoding
     *
     * @return
     */
    public static String readContent(File f, Charset encoding) {
        try(InputStream is = new FileInputStream(f);) {
            return IOUtils.readContent(is, encoding);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 读取文件所有文本行
     *
     * @param f
     * @param encoding
     *
     * @return
     */
    public static ArrayList<String> readLines(File f, Charset encoding) {
        ArrayList<String> lines = new ArrayList();
        String s;
        try (BufferedReader br = getReader(f, encoding)) {
            while ((s = br.readLine()) != null) {
                lines.add(s);
            }
        } catch (IOException ex) {
            throw new CEException(ex);
        }
        return lines;
    }

    /**
     * 将文本行写入文件
     *
     * @param f
     * @param encoding
     * @param append 是否追加
     * @param lines
     */
    public static void writeLines(File f, Charset encoding, boolean append, Collection<String> lines) {
        Iterator<String> it = lines.iterator();
        f = getFile(f.getPath(), true);
        try (BufferedWriter bw = getWriter(f, encoding, append)) {
            while (it.hasNext()) {
                bw.write(it.next());
                bw.newLine();
            }
        } catch (IOException ex) {
            throw new CEException(ex);
        }
    }

    /**
     * 获取目录,默认不创建
     *
     * @param path
     *
     * @return
     */
    public static File getFolder(String path) {
        return getFolder(path, false);
    }

    public static File getFolder(String path, boolean create) {
        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();
        }
        return f;
    }

    /**
     * 获取文件,默认不创建
     *
     * @param path
     *
     * @return
     */
    public static File getFile(String path) {
        return getFile(path, false);
    }

    public static File getFile(String path, boolean create) {
        File f = new File(path);
        if (!f.exists()) {
            getFolder(f.getParent(), true);
            try {
                f.createNewFile();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        return f;
    }

    public static File createFile(File f) throws IOException {
        if (!f.exists()) {
            if (!f.getParentFile().exists()) {
                f.getParentFile().mkdirs();
            }
            f.createNewFile();
        }
        return f;
    }

    public static File createFile(String path) throws IOException {
        return createFile(new File(path));
    }

    public static List<String> FileList(File file, String subfix) {
        List<String> filelist = new ArrayList<String>();
        if (file.isFile()) {
            if (file.getPath().endsWith(subfix)) {
                filelist.add(file.getPath());
            }
            return filelist;
        } else {
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.isFile()) {
                    if (f.getPath().endsWith(subfix)) {
                        filelist.add(f.getPath());
                    }
                } else {
                    filelist.addAll(FileList(f));
                }
            }
        }
        return filelist;
    }

    /**
     * 返回文件夹下所有文件的路径,不含空文件夹
     *
     * @param file
     *
     * @return
     */
    public static List<String> FileList(File file) {
        List<String> filelist = new ArrayList<String>();
        if (file.isFile()) {
            filelist.add(file.getPath());
            return filelist;
        } else {
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.isFile()) {
                    filelist.add(f.getPath());
                } else {
                    filelist.addAll(FileList(f));
                }
            }
        }
        return filelist;
    }

    /**
     * 打开BufferedWriter,默认UTF-8编码,非追加
     *
     * @param f
     *
     * @return
     *
     * @throws FileNotFoundException
     */
    public static BufferedWriter getWriter(File f) throws FileNotFoundException {
        return getWriter(f, Charset.forName("UTF-8"), false);
    }

    /**
     * 打开BufferedWriter,默认UTF-8编码
     *
     * @param f
     * @param append 是否追加
     *
     * @return
     *
     * @throws FileNotFoundException
     */
    public static BufferedWriter getWriter(File f, boolean append) throws FileNotFoundException {
        return getWriter(f, Charset.forName("UTF-8"), append);
    }

    /**
     * 打开BufferedWriter,追加模式
     *
     * @param f
     * @param encoding
     *
     * @return
     *
     * @throws FileNotFoundException
     */
    public static BufferedWriter getWriter(File f, Charset encoding) throws FileNotFoundException {
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f, false), encoding));
    }

    /**
     * 打开BufferedWriter
     *
     * @param f
     * @param encoding 编码
     * @param append 追加
     *
     * @return
     *
     * @throws FileNotFoundException
     */
    public static BufferedWriter getWriter(File f, Charset encoding, boolean append) throws FileNotFoundException {
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f, append), encoding));
    }

    public static BufferedReader getReader(File f) throws FileNotFoundException {
        return getReader(f, Charset.forName("UTF-8"));
    }

    public static BufferedReader getReader(File f, Charset encoding) throws FileNotFoundException {
        return new BufferedReader(new InputStreamReader(new FileInputStream(f), encoding));
    }

    /**
     * 一次性压缩多个文件，文件存放至一个文件夹中
     */
    //ZipMultiFile("f:/uu", "f:/zippath.zip");
    public static void ZipMultiFile(String filepath, String zippath, int level) {
        List<String> filelist = new ArrayList<>();
        ZipOutputStream zipOut = null;
        try {
            File file = new File(filepath);// 要被压缩的文件夹
            File zipFile = new File(zippath);
            InputStream input = null;
            File temp;
            String entryS;
            ZipEntry entry;

            zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
            zipOut.setLevel(level);
            filelist = FileList(file);
            for (String path : filelist) {
                temp = new File(path);
                if (!temp.isDirectory()) {
                    entryS = temp.getPath().substring(filepath.length() + 1);  //条目为文件
                } else {
                    entryS = temp.getPath().substring(filepath.length() + 1) + "/";  //条目为目录，结尾加上"/"
                }
                entry = new ZipEntry(entryS);
                entry.setTime(temp.lastModified());
                zipOut.putNextEntry(entry);
                if (!temp.isDirectory()) {
                    input = new FileInputStream(temp);
                    byte[] bs = new byte[8192];
                    int surplus = 0;
                    while ((surplus = input.read(bs)) != -1) {
                        zipOut.write(bs, 0, surplus);
                    }
                    input.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                zipOut.close();
            } catch (NullPointerException e) {
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    //解压zip文件内指定条目filename到outfilepath
    //ZipContraFile("f:/zippath.zip", "f:/Clann.dat", "default/Clann.dat");
    public static void ZipContraFile(String zippath, String outfilepath, String filename) {
        try {
            File file = new File(zippath);//压缩文件路径和文件名
            File outFile = new File(outfilepath);//解压后路径和文件名
            ZipFile zipFile;
            zipFile = new ZipFile(file);
            ZipEntry entry = zipFile.getEntry(filename);//所解压的文件名
            InputStream input = zipFile.getInputStream(entry);
            OutputStream output = new FileOutputStream(outFile);
            int temp = 0;
            byte[] bs = new byte[8192];
            while ((temp = input.read(bs)) != -1) {
                output.write(bs, 0, temp);
            }
            input.close();
            output.close();
            outFile.setLastModified(entry.getTime());
        } catch (IOException ex) {
            Logger.getLogger(FileApi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //删除文件或递归删除目录和其下所有目录和文件
    public static void delDirectory(File file) {
        if (file.isDirectory()) {
            for (File delFile : file.listFiles()) {
                delDirectory(delFile);
            }
        }
        file.delete();
    }
}
