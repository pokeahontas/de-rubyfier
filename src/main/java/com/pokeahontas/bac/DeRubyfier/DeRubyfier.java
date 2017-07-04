package com.pokeahontas.bac.DeRubyfier;

import org.antlr.v4.runtime.*;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class DeRubyfier {

    static String rootPath = "C:/Users/Kureeeeeey/Uni2/DeRubyfier v2.2 Rework/resources/sample_app_rails_4-master/app";  // src/main/resources/sample_app_rails_4-master/app
    static int count = 0;
    final static boolean fc = true;
    final static Logger logger = Logger.getLogger(FileHelper.class);
    final static String soutSeparator = "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
            "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
            "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
            "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~";
    static String currentErbFile = "";
    static String currentRubyFile = "";

    public static void main(String[] args) {
        //open File chooser to select app directory from Rails project
        if (fc) {
            rootPath = FileHelper.openFileChooserAndGetRootPath();
        }

        FileHelper.retrieveFiles(rootPath);
        ErbManager erbManager;
        RubyManager rubyManager;
        ArrayList<ANTLRInputStream> fileList = new ArrayList<>();
        ANTLRInputStream rbFile = null;
        ANTLRInputStream erbFile = null;
        ANTLRInputStream embRuby;
        ArrayList<String> rubyFileList = FileHelper.getRubyFileList();
        ArrayList<String> erbFileList = FileHelper.getErbFileList();

        /**
         * PHASE 1: Erb file parsing
         * */
        if (!erbFileList.isEmpty()) {
            logger.info("PHASE 1 STARTING: PARSING THE HTML ERB FILES");
            for (String s : erbFileList) {
                try {
                    logger.info("PHASE 1 @ " + FileHelper.calculateProgressInPercent(count, erbFileList.size()) + "%: Checking erb file " + s.substring(s.lastIndexOf("/") + 1));
                    currentErbFile = s; //  s.substring(s.lastIndexOf("/")+1)  for only file name
                    erbFile = new ANTLRInputStream(new FileInputStream(s));
                    erbManager = new ErbManager(erbFile);
                    erbManager.initialize();
                    count++;

                } catch (IOException e) {
                    logger.debug("An error occurred while retrieving erb files");
                    //e.printStackTrace();
                }
            }
        }
        logger.info(erbFileList.size() + " erb files were parsed.");
        logger.info(ErbListenerImpl.getOverallScriptCounter() + " script tags were found in all files");
        FileHelper.writeStringsToFile(ErbListenerImpl.getEmbCodeList().toString(), "embList");
        System.out.println(soutSeparator);
        count = 0;
        /**
         * PHASE 2: Parsing embedded code from erb files
         * */
        if (!erbFileList.isEmpty()) {
            logger.info("PHASE 2 STARTING: PARSING THE EMBEDDED CODE FROM ERB FILES");
            ArrayList<String> embCodeList = ErbListenerImpl.getEmbCodeList();

            for (String s : embCodeList) {
                logger.info("PHASE 2 @ " + FileHelper.calculateProgressInPercent(count, ErbListenerImpl.getEmbCodeList().size()) + "%: Parsing embedded code...");
                count++;
                embRuby = new ANTLRInputStream(s);
                RubyListenerImpl.setIsRealRuby(false);
                RubyManager rubyManagerForErb = new RubyManager(embRuby);
                rubyManagerForErb.initialize();

            }

            logger.info("Embedded Ruby code finished parsing.");
            logger.info(RubyListenerImpl.getErbFunctionCalls().size() + " embedded Ruby calls were made");
            FileHelper.writeStringsToFile(RubyListenerImpl.getErbFunctionCalls().toString() + " \n"
                    + "SIZE: " + RubyListenerImpl.getErbFunctionCalls().size(), "recognizedFunctionCalls");
            System.out.println(soutSeparator);
        }
        count = 0;
        /**
         *  PHASE 3: Parsing ruby files to collect all defined functions
         *
         * */
        logger.info("PHASE 3 STARTING: EXTRACTING FUNCTION NAMES");
        RubyListenerImpl.setIsPhase3(true);
        for (String s : rubyFileList) {
            currentRubyFile = s;
            try {
                RubyListenerImpl.setCurrentPackage(s.substring(s.indexOf("app\\") + 4));
                logger.info("PHASE 3 @ " + FileHelper.calculateProgressInPercent(count, rubyFileList.size()) + "%: " + s.substring(s.lastIndexOf("/") + 1));
                rbFile = new ANTLRInputStream(new FileInputStream(s));
                rubyManager = new RubyManager(rbFile);
                rubyManager.initialize();
                count++;
            } catch (IOException e) {
                logger.debug("An error occurred while retrieving ruby files");
            }

        }
        System.out.println();
        FileHelper.writeStringsToFile(RubyListenerImpl.getDefinedFunctions().toString() + " \n"
                + "SIZE: " + RubyListenerImpl.getDefinedFunctions().size(), "definedFunctions");
        RubyListenerImpl.setIsPhase3(false);
        count = 0;
        /**
         * PHASE 4: Parsing ruby files and creating call graph
         *
         * */
        logger.info("PHASE 4 STARTING: CREATING CALL GRAPH");
        RubyListenerImpl.setIsRealRuby(true);
        for (String s : rubyFileList) {
            currentRubyFile = s;
            try {
                RubyListenerImpl.setCurrentPackage(s.substring(s.indexOf("app\\") + 4));
                logger.info("PHASE 4 @ " + FileHelper.calculateProgressInPercent(count, rubyFileList.size()) + "%: " + s.substring(s.lastIndexOf("/") + 1));
                rbFile = new ANTLRInputStream(new FileInputStream(s));
                rubyManager = new RubyManager(rbFile);
                rubyManager.initialize();
                count++;
            } catch (IOException e) {
                logger.debug("An error occurred while retrieving ruby files");
            }

        }
        logger.info(erbFileList.size() + " ruby files were parsed.");

        FileHelper.writeStringsToFile(RubyListenerImpl.getCallGraphToString(), "output");
        logger.info("Results were written into file out/output.txt");
        logger.info("Program finished.");
    }
}
