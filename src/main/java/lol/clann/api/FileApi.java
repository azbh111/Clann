/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.api;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.bukkit.plugin.java.JavaPlugin;

public class FileApi {

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

    public static File getFile(JavaPlugin plugin,String path) throws IOException{
        return createFile(new File(plugin.getDataFolder(),path));
    }
    
    /**
     * 返回文件夹下所有文件的路径,不含空文件夹
     *
     * @param file
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

    public static Boolean logToFile(String s, File file) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file, true);
            fw.write(s);
            fw.flush();
            fw.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
