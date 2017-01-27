package com.pokeahontas.bac.DeRubyfier;

import org.antlr.v4.runtime.*;
import java.io.IOException;
import java.util.ArrayList;

public class DeRubyfier {
    public static void main( String[] args )
    {
        /*
        TODO 1: Read all necessary files
        TODO 2: Deliver them to  ParserManager class (as String, list of InputStreams etc.)
         */
        ArrayList<ANTLRInputStream> fileList = new ArrayList<>();
        ANTLRInputStream files = null;
        try {
            files = new ANTLRInputStream(DeRubyfier.class.getResourceAsStream("/test.rb"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ParseManager parseManager = new ParseManager(files);
        parseManager.initialize();
    }
}
