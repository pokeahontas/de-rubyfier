package com.pokeahontas.bac.DeRubyfier;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
/**
 * Created by Kureeeeeey on 19.01.2017.
 */
public class ParseManager {
    ANTLRInputStream files;
    public ParseManager(ANTLRInputStream files) {
        this.files=files;
    }
    public void initialize() {
        RubyLexer lexer = new RubyLexer(files);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        RubyParser parser = new RubyParser(tokens);
        RubyParser.ProgramContext progCtx = parser.program();

        ParseTreeWalker walker = new ParseTreeWalker();
        ClassListener classListener = new ClassListener();
        walker.walk(classListener,progCtx);


/*ParseTree tree = parser.program();
        JFrame frame = new JFrame("Antlr AST");
        JPanel panel = new JPanel();
        TreeViewer viewr = new TreeViewer(Arrays.asList(
                parser.getRuleNames()),tree);
        viewr.setScale(1.5);//scale a little
        panel.add(viewr);
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(200,200);
        frame.setVisible(true);*/

    }
}
