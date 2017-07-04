package com.pokeahontas.bac.DeRubyfier;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 * Created by Kureeeeeey on 02.03.2017.
 */
public class ErbManager {
    private ANTLRInputStream file;
    public ErbManager(ANTLRInputStream file) {
        this.file = file;
    }

    /*
    Initializes parsers/lexers and walks through ParseTree
     */
    public void initialize() {

        ErbLexer lexer = new ErbLexer(file);
        lexer.removeErrorListeners(); // for removing console error output
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ErbParser parser = new ErbParser(tokens);
        parser.removeErrorListeners(); // for removing console error output
        ErbParser.HtmlDocumentContext docCtx = parser.htmlDocument();

        ParseTreeWalker walker = new ParseTreeWalker();
        ErbListenerImpl rubyListener = new ErbListenerImpl();
        walker.walk(rubyListener,docCtx);
    }
}
