package com.pokeahontas.bac.DeRubyfier;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.log4j.Logger;

/**
 * Created by Kureeeeeey on 19.01.2017.
 */
public class RubyManager {
    ANTLRInputStream files;
    final static Logger logger = Logger.getLogger(FileHelper.class);

    public RubyManager(ANTLRInputStream files) {
        this.files = files;
    }
    /*
    Initializes parsers/lexers and walks through ParseTree
     */
    public void initialize() {

        RubyLexer lexer = new RubyLexer(files);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        RubyParser parser = new RubyParser(tokens);
        //parser.setTrace(true); // UNCOMMENT TO SEE DETAILED PARSING PROCESS
        parser.removeErrorListeners(); // for removing console error output
        lexer.removeErrorListeners();  // for removing console error output
        RubyParser.ProgramContext progCtx = parser.program();

        ParseTreeWalker walker = new ParseTreeWalker();
        RubyListenerImpl rubyListener = new RubyListenerImpl();
        walker.walk(rubyListener, progCtx);
    }
}
