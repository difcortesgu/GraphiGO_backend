import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;

public class Analizer implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {
        //read file from request
        request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("C:/tmp"));
        Part filePart = request.raw().getPart("myfile");

        //set up the lexer and parser
        GoLexer lexer = new GoLexer(CharStreams.fromStream(filePart.getInputStream()));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        GoParser parser = new GoParser(tokens);
        ParseTree tree = parser.sourceFile();

        //visit first node of the parse tree and return the result of the analysis
        Visitor loader = new Visitor();
        return loader.visit(tree);
    }
}
