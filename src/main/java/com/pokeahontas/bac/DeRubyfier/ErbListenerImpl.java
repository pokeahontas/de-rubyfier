package com.pokeahontas.bac.DeRubyfier;

import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 * Created by Kureeeeeey on pokeahtont02.04.2017.
 */
public class ErbListenerImpl extends ErbParserBaseListener{
    final Logger logger = Logger.getLogger(ErbListenerImpl.class);
    private static String embCode = "";
    private static ArrayList<String> embCodeList = new ArrayList<>();
    int scriptCounter = 0;
    static int overallScriptCounter = 0;

    @Override
    public void enterHtmlDocument(ErbParser.HtmlDocumentContext ctx) {
        /**
        Write Filename as Ruby comment
         */
        embCode+="# ~~~~~~~~~~~~~~~~~~~~~~~~~~~ File " + DeRubyfier.currentErbFile + "\n";
        embCodeList.add("# ~~~~~~~~~~~~~~~~~~~~~~~~~~~ File " + DeRubyfier.currentErbFile + "\n");
    }

    @Override
    public void exitHtmlDocument(ErbParser.HtmlDocumentContext ctx) {
       overallScriptCounter+=scriptCounter;
    }

    /**
     *
     * REMOVING TAGS FOR SIMPLIFIED PARSING OF EMBEDDED RUBY CODE
     */
    @Override
    public void enterOutput_scriptlet(ErbParser.Output_scriptletContext ctx) {
        scriptCounter++;
        String temp1 = ctx.OUTPUT_SCRIPTLET().getText();
        temp1 = temp1.replaceAll("\\W<%=\\W|^<%=\\W|\\W<%=$", "");
        temp1 = temp1.replaceAll("\\W<%=h\\W|^<%=h\\W|\\W<%=h$", "");
        temp1 = temp1.replaceAll("\\W%>\\W|^%>\\W|\\W%>$", "");

        embCodeList.set(embCodeList.size()-1,embCodeList.get(embCodeList.size()-1) +  temp1 + "\n");
        embCode += temp1 + "\n";
    }

    /**
     *
     * REMOVING TAGS FOR SIMPLIFIED PARSING OF EMBEDDED RUBY CODE
     */
    @Override
    public void enterScriptlet(ErbParser.ScriptletContext ctx) {
        scriptCounter++;
        String temp2 = ctx.SCRIPTLET().getText();
        temp2 = temp2.replaceAll("\\W<%\\W|^<%\\W|\\W<%$", "");
        temp2 = temp2.replaceAll("\\W%>\\W|^%>\\W|\\W%>$", "");
        temp2 = temp2.replace("?", "");

        embCodeList.set(embCodeList.size()-1,embCodeList.get(embCodeList.size()-1) +  temp2 + "\n");
        embCode += temp2 + "\n";
    }

    public static String getEmbCode() {
        return embCode;
    }

    public static ArrayList<String> getEmbCodeList() {return embCodeList;}

    public static int getOverallScriptCounter() {
        return overallScriptCounter;
    }
}
