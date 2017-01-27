package com.pokeahontas.bac.DeRubyfier;

import org.apache.log4j.Logger;

/**
 * Created by Kureeeeeey on 13.01.2017.
 */
public class ClassListener extends RubyBaseListener {
    final Logger logger = Logger.getLogger(ClassListener.class);
    private String classDefinitions = "";
    private String functionDefinitions = "";
    private String functionCalls = "";
    private String functionCallsFromClass="";
    @Override
    public void enterProgram(RubyParser.ProgramContext ctx) {
        logger.info("Program started.");
        System.out.println("\n");
    }
    @Override
    public void exitProgram(RubyParser.ProgramContext ctx) {
        System.out.println("\n");
        logger.info("Program finished.");
    }
   @Override
    public void enterClass_definition(RubyParser.Class_definitionContext ctx) {
        if(ctx.class_name().id_superclass() != null) {
            System.out.println("DEFINED CLASS: " + ctx.class_name().getText() + " (inherits from: " + ctx.class_name().id_superclass() + ")");
        } else {
            System.out.println("DEFINED CLASS: " + ctx.class_name().getText());
        }
    }
    @Override
    public void enterFunction_definition(RubyParser.Function_definitionContext ctx) {
        System.out.println("DEFINED FUNCTION: "+ctx.function_definition_header().function_name().getText() + " with parameters: " + ctx.function_definition_header().function_definition_params().getText());
    }
    @Override
    public void enterFunction_call(RubyParser.Function_callContext ctx) {
        if(ctx.function_call_param_list() != null) {
            functionCalls+="FUNCTION CALL: " + ctx.function_name().getText() + " with parameters " + ctx.function_call_param_list().getText();
        }
        else {
            functionCalls+="FUNCTION CALL: " + ctx.function_name().getText() + " with no parameters";
        }
        System.out.println(functionCalls);
        functionCalls="";
    }
    @Override
    public void enterFunction_call_from_class(RubyParser.Function_call_from_classContext ctx) {
        if(ctx.function_call_param_list() != null) {
            functionCallsFromClass += "FUNCTION CALL: " + ctx.function_name().getText() + " called from class " + ctx.id_class_fcall().getText() + " with parameters: " + ctx.function_call_param_list().getText();
        }
        else {
            functionCallsFromClass+="FUNCTION CALL: " + ctx.function_name().getText()+ " called from class " + ctx.id_class_fcall().getText() + " with no parameters";
        }
        System.out.println(functionCallsFromClass);
        functionCallsFromClass="";
    }

    @Override
    public void enterString_assignment(RubyParser.String_assignmentContext ctx) {
        if(ctx.var_id.getText().contains("@")) {
            System.out.println("    Declaration of instance variable: " + ctx.var_id.getText() + " with value " + ctx.string_result().getText());
        }else if(ctx.var_id.getText().contains("@@")) {
            System.out.println("    Declaration of class variable: " + ctx.var_id.getText());
        }
    }

    @Override
    public void enterFloat_assignment(RubyParser.Float_assignmentContext ctx) {
        if(ctx.var_id.getText().contains("@")) {
            System.out.println("    Declaration of instance variable: " + ctx.var_id.getText());
        }else if(ctx.var_id.getText().contains("@@")) {
            System.out.println("    Declaration of class variable: " + ctx.var_id.getText());
        }
    }

    @Override
    public void enterInt_assignment(RubyParser.Int_assignmentContext ctx) {
        if(ctx.var_id.getText().contains("@")) {
            System.out.println("    Declaration of instance variable: " + ctx.var_id.getText());
        } else if(ctx.var_id.getText().contains("@@")) {
            System.out.println("    Declaration of class variable: " + ctx.var_id.getText());
        }
    }

    @Override
    public void enterDynamic_assignment(RubyParser.Dynamic_assignmentContext ctx) {
        if(ctx.var_id.getText().contains("@")) {
            System.out.println("    Declaration of instance variable: " + ctx.var_id.getText());
        }else if(ctx.var_id.getText().contains("@@")) {
            System.out.println("    Declaration of class variable: " + ctx.var_id.getText());
        }
    }

    @Override
    public void enterAssignment(RubyParser.AssignmentContext ctx) {
        if(ctx.var_id.getText().contains("@")) {
            System.out.println("    Declaration of instance variable: " + ctx.var_id.getText());
        }else if(ctx.var_id.getText().contains("@@")) {
            System.out.println("    Declaration of class variable: " + ctx.var_id.getText());
        }
    }
}


