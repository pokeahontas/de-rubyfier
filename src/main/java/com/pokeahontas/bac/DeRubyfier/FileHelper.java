package com.pokeahontas.bac.DeRubyfier;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by alexander.kuroll on 3/20/2017.
 *
 * Class for Helper methods(mostly concerning files)
 */
public class FileHelper {
    private static ArrayList<String> rubyFileList = new ArrayList<>();
    private static ArrayList<String> erbFileList = new ArrayList<>();

    final static Logger logger = Logger.getLogger(FileHelper.class);

    public static void retrieveFiles(String rootPath) {
        listf(rootPath,new ArrayList<File>());
        logger.info(rubyFileList.size() + " .rb files were collected");
        logger.info(erbFileList.size() + " .html.erb files were collected");
    }

    /**
     *
     * @param directoryName Starting directory
     * @param files fileList
     *
     *  This is a method for recursively walking through directory hierarchy and collecting .rb and .erb files
     */
    private static void listf(String directoryName, ArrayList<File> files) {
        File directory = new File(directoryName);
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                if (!(file.getName().contains("js.erb"))) {
                    files.add(file);
                if (checkFileExtension(file, "rb")) {
                    rubyFileList.add(directoryName + "/" + file.getName());
                }
                if (checkFileExtension(file, "erb")) {
                    erbFileList.add(directoryName + "/" + file.getName());
                }
            }
            } else if (file.isDirectory()) {
                listf(file.getAbsolutePath(), files);
            }
        }
    }

    /**
     *
     * @param file
     * @param ext
     * @return if the files extension equals ext
     */
    private static boolean checkFileExtension(File file, String ext) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            if(fileName.substring(fileName.lastIndexOf(".") + 1).equals(ext)) {
                return true;
            } else {
                return false;
            }
        } else return false;
    }

    /**
     * Simple File Writer
     * @param infos
     * @param name
     */
    public static void writeStringsToFile(String infos, String name) {
        PrintWriter writer = null;
//+ new java.util.Date() +
        File file = new File("src/main/out/"+name+".txt");
        try {
            writer = new PrintWriter(file, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (writer != null) {
            writer.println(infos);
            writer.close();
        }


    }

    /**
     * Open FileChooser; only folder named "app" can be chosen
     * @return
     */
    public static String openFileChooserAndGetRootPath() {
        String rootPath = "";

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        JFileChooser chooser = new JFileChooser(System.getProperty("user.home") +"\\Uni2\\DeRubyfier v3\\resources\\rtissTrim");
        chooser.setDialogTitle("Please choose app folder from a Rails project!");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        boolean loop=true;
        while (loop) {
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                String app = chooser.getSelectedFile().getAbsolutePath();
                app = app.substring(app.lastIndexOf("\\"));
                if (app.equals("\\app")) {
                    loop=false;
                    logger.info("Selected directory: " + chooser.getSelectedFile());
                    rootPath = chooser.getSelectedFile().getAbsolutePath();
                } else {
                    JOptionPane.showMessageDialog(null, "You can only choose the \"app\" folder from a Rails Project!");
                }
                //System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());

            } else {
                logger.debug("No Selection ");
            }
        }
        return rootPath;
    }

    public static ArrayList<String> getRubyFileList() {
        return rubyFileList;
    }

    public static ArrayList<String> getErbFileList() {
        return erbFileList;
    }

    /**
     * Method for calculating progress percentage
     * @param count number of files went through
     * @param size number of files
     * @return
     */
    public static int calculateProgressInPercent(int count,int size) {
        return (int)((count * 100.0f) / size);
    }
}

