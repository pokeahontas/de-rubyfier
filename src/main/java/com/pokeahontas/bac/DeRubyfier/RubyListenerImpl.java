package com.pokeahontas.bac.DeRubyfier;

import org.apache.log4j.Logger;

import javax.swing.text.MutableAttributeSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Kureeeeeey on 13.01.2017.
 */
public class RubyListenerImpl extends RubyBaseListener {

    final Logger logger = Logger.getLogger(RubyListenerImpl.class);

    private static String classFuncDefinitions = "";
    private static ArrayList<String> callGraph = new ArrayList<>();
    private static HashSet erbFunctionCalls = new HashSet();
    private static HashMap<String, String> definedFunctions = new HashMap<>();
    private static boolean isPhase3 = false;
    private static boolean isRealRuby = false;
    private static boolean isModel = false;
    private static String currentClass = "";
    private static String currentPackage = "";
    private static String currentFunction = "";

    private boolean usedInErb = false;
    private String nl;
    private String functionCalls = "";
    private String functionCallsFromClass = "";
    private static String failedRecognitions = "";
    private static int graphCounter = 0;

    private static String currentFCCpre = "";
    private static int fcHelpCounter = 0;
    private static boolean inFCC = false;

    static int testCounter = 0;

    @Override
    public void enterProgram(RubyParser.ProgramContext ctx) {
        nl = System.lineSeparator(); // set Systems CTRLF
    }

    @Override
    public void enterClass_definition(RubyParser.Class_definitionContext ctx) {
        if (!isPhase3) {
            if (ctx.class_name() != null) {
                if (ctx.class_name().class_name_include_list() != null) {
                    List<RubyParser.Id_classContext> includeList = ctx.class_name().class_name_include_list().id_class();
                    currentClass = includeList.get(includeList.size() - 1).getText();
                } else {
                    currentClass=ctx.class_name().id_class().getText();
                }
            }
        }
    }

    @Override
    public void enterModule_definition(RubyParser.Module_definitionContext ctx) {
        if (!isPhase3) {
            if (ctx.class_name() != null) {
                if (ctx.class_name().class_name_include_list() != null) {
                    List<RubyParser.Id_classContext> includeList = ctx.class_name().class_name_include_list().id_class();
                    currentClass = includeList.get(includeList.size() - 1).getText();
                } else {
                    currentClass=ctx.class_name().id_class().getText();
                }
            }
        }
    }

    @Override
    public void enterFunction_definition(RubyParser.Function_definitionContext ctx) {
        if (ctx.function_definition_header().function_name() != null) {
            currentFunction = ctx.function_definition_header().function_name().getText();
            if (isPhase3) {  // PHASE 3
                definedFunctions.put(currentFunction, currentPackage);
            } else {
                if (isRealRuby) { // PHASE 4
                    if (ctx.function_definition_header().function_definition_params() != null) {
                        classFuncDefinitions += "\t" + ctx.function_definition_header().function_name().getText()
                                + " with parameters: " + ctx.function_definition_header().function_definition_params().getText() + nl;
                    } else if (ctx.function_definition_header().function_name() != null) {
                        classFuncDefinitions += "\t" + ctx.function_definition_header().function_name().getText() + nl;
                    } else {
                        //logger.debug("function_definition_header().FUNCTION_NAME/PARAMS == NULL AT: " + ctx.getText());
                    }
                }
                if (erbFunctionCalls.contains(currentFunction)) {
                    usedInErb = true;
                } else {
                    // SET TRUE FOR CREATING CALL GRAPH FOR EVERY METHOD DECLARED IN RUBY
                    usedInErb = false;
                }
            }
        }
    }

    @Override
    public void exitFunction_definition(RubyParser.Function_definitionContext ctx) {
        if (!isPhase3) {
            classFuncDefinitions += nl;
            if (usedInErb) {
                graphCounter = callGraph.size();
            }
            //usedInErb=false;
            //currentFunction = "NOPE";
        }
    }

    @Override
    public void enterFunction_call_chain(RubyParser.Function_call_chainContext ctx) {
        inFCC=true;
    }

    @Override
    public void exitFunction_call_chain(RubyParser.Function_call_chainContext ctx) {
        currentFCCpre = "";
        inFCC=false;
        fcHelpCounter=0;
    }

    @Override
    public void enterFunction_call_chain_pre(RubyParser.Function_call_chain_preContext ctx) {
        if (ctx.id_class()!=null || ctx.class_name_include_list()!=null) {
            currentFCCpre = ctx.getText().substring(0, ctx.getText().length() - 1);
        }
    }

    @Override
    public void enterFunction_call(RubyParser.Function_callContext ctx) {
        if (!isPhase3) {
            String foundInDefinedFunctions;
            if (isRealRuby) {
                if (!currentFunction.equals("NOPE")) {      // if currentFunction equals NOPE then ctx is a function call outside of a function
                    if (usedInErb) {
                        if(inFCC) {     //increments if function call is in  fc_chain
                            fcHelpCounter++;
                        }
                        if (ctx.function_name() != null && (ctx.function_name().getText().length() > 1 && !ctx.function_name().getText().equals("l")) && !ctx.function_name().getText().equals("id") && !ctx.function_name().getText().equals("==")&& !ctx.function_name().getText().equals("<<")&& !ctx.function_name().getText().equals("<=>") && !Character.isUpperCase(ctx.function_name().getText().charAt(0))) {
                            String params;

                            foundInDefinedFunctions = definedFunctions.get(ctx.function_name().getText());
                            if (foundInDefinedFunctions != null) {  // CASE 1: Method is defined in Ruby
                                if (ctx.function_call_param_list() != null
                                        && !ctx.function_call_param_list().getText().substring(0, Math.min(ctx.function_call_param_list().getText().length(), 2)).equals("==")
                                        && !ctx.function_call_param_list().getText().substring(0, Math.min(ctx.function_call_param_list().getText().length(), 2)).equals("<<")
                                        && !ctx.function_call_param_list().getText().substring(0, Math.min(ctx.function_call_param_list().getText().length(), 3)).equals("<=>")
                                        && !String.valueOf(ctx.function_call_param_list().getText().charAt(0)).equals("+")) { //to exclude wrong params when there's recognition error in cond_expression
                                    params = ctx.function_call_param_list().getText();
                                    params = params.replace("\n", "");
                                    callGraph.add(currentPackage + "\t" + currentClass + "." + currentFunction + "\t " + foundInDefinedFunctions + "\t " + ctx.function_name().getText() + "\t" + "(" + params + ")");
                                } else {
                                    callGraph.add(currentPackage + "\t" + currentClass + "." + currentFunction + "\t " + foundInDefinedFunctions + "\t " + ctx.function_name().getText() + "\t" + "()");
                                }
                            } else if (!currentFCCpre.isEmpty() && fcHelpCounter==1) { // CASE 2: Method not defined in Ruby, but called from class
                                if (ctx.function_call_param_list() != null
                                        && !ctx.function_call_param_list().getText().substring(0, Math.min(ctx.function_call_param_list().getText().length(), 2)).equals("==")
                                        && !ctx.function_call_param_list().getText().substring(0, Math.min(ctx.function_call_param_list().getText().length(), 2)).equals("<<")
                                        && !ctx.function_call_param_list().getText().substring(0, Math.min(ctx.function_call_param_list().getText().length(), 3)).equals("<=>")
                                        && !String.valueOf(ctx.function_call_param_list().getText().charAt(0)).equals("+")) { //to exclude wrong params when there's recognition error in cond_expression
                                    params = ctx.function_call_param_list().getText();
                                    params = params.replace("\n", "");
                                    callGraph.add(currentPackage + "\t" + currentClass + "." + currentFunction + "\t " + currentFCCpre + "\t" + ctx.function_name().getText() + "\t" + "(" + params + ")");
                                } else {
                                    callGraph.add(currentPackage + "\t" + currentClass + "." + currentFunction + "\t " + currentFCCpre + "\t" + ctx.function_name().getText() + "\t" + "()");
                                }
                            } else { // CASE 3/4: Method is not defined in Ruby and not called from class

                                if (ctx.function_call_param_list() != null
                                        && !ctx.function_call_param_list().getText().substring(0, Math.min(ctx.function_call_param_list().getText().length(), 2)).equals("==")
                                        && !ctx.function_call_param_list().getText().substring(0, Math.min(ctx.function_call_param_list().getText().length(), 2)).equals("<<")
                                        && !ctx.function_call_param_list().getText().substring(0, Math.min(ctx.function_call_param_list().getText().length(), 3)).equals("<=>")
                                        && !String.valueOf(ctx.function_call_param_list().getText().charAt(0)).equals("+")) { //to exclude wrong params when there's recognition error in cond_expression
                                    params = ctx.function_call_param_list().getText();
                                    params = params.replace("\n", "");
                                    if (ctx.function_name().getText().equals("raise")) {  // CASE 4
                                        callGraph.add(currentPackage + "\t" + currentClass + "." + currentFunction + "\t " + "EXCEPTION THROWN" + "\t" + ctx.function_name().getText() + "\t" + "(" + params + ")");
                                    } else {
                                        callGraph.add(currentPackage + "\t" + currentClass + "." + currentFunction + "\t " + "VARIABLE/EXTRANEOUS METHOD" + "\t" + ctx.function_name().getText() + "\t" + "(" + params + ")");
                                    }
                                } else {
                                    if (ctx.function_name().getText().equals("raise")) {  // CASE 4

                                        callGraph.add(currentPackage + "\t" + currentClass + "." + currentFunction + "\t " + "EXCEPTION THROWN" + "\t" + ctx.function_name().getText() + "\t" + "()");
                                    } else {
                                        callGraph.add(currentPackage + "\t" + currentClass + "." + currentFunction + "\t " + "VARIABLE/EXTRANEOUS METHOD" + "\t" + ctx.function_name().getText() + "\t" + "()");
                                    }
                                }
                            }
                        } else {
                            failedRecognitions += currentPackage + "\t" + currentClass + "." + currentFunction + "." + ctx.getText() + ", \n";
                        }
                    }
                }
            } else {
                if (ctx.function_name() == null) {
                    //logger.debug("FUNCTION_NAME == NULL AT: " + ctx.getText());
                } else {
                    erbFunctionCalls.add(ctx.function_name().getText()); // recognized method is added to look up HashSet
                }
            }
        }
    }

    /**
     * Get call graph as formatted String
     * @return
     */
    public static String getCallGraphToString() {
        String graph = "CALL GRAPH:" + System.lineSeparator();
        for (String s : callGraph) {
            graph += s + System.lineSeparator();
        }
        //graph += "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" + failedRecognitions; uncomment to add unclear/failed recognitions
        return graph;
    }

    public static boolean isIsRealRuby() {
        return isRealRuby;
    }

    public static void setCurrentPackage(String currentPackage) {
        RubyListenerImpl.currentPackage = currentPackage;
    }

    public static String getCurrentPackage() {
        return currentPackage;
    }

    public static void setIsRealRuby(boolean flag) {
        isRealRuby = flag;
    }

    public static void setModel(boolean model) {
        isModel = model;
    }

    public static void setIsPhase3(boolean flag) {
        isPhase3 = flag;
    }

    public static HashMap<String, String> getDefinedFunctions() {
        return definedFunctions;
    }

    public static String getClassFuncDefinitions() {
        return classFuncDefinitions;
    }

    public static HashSet getErbFunctionCalls() {
        return erbFunctionCalls;
    }



}


