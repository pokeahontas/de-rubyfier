package com.pokeahontas.bac.DeRubyfier;

import org.apache.log4j.Logger;

/**
 * Created by Kureeeeeey on 13.01.2017.
 */
public class ClassListener extends RubyBaseListener {
    final Logger logger = Logger.getLogger(ClassListener.class);
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
    public void enterClass_define(RubyParser.Class_defineContext ctx) {
        System.out.println("CLASS: "+ctx.class_name().getText());
    }
    @Override
    public void enterFunction_define(RubyParser.Function_defineContext ctx) {
        System.out.println("FUNCTION: "+ctx.function_name().getText() + " with parameters: " + ctx.function_params().getText());
    }
}
