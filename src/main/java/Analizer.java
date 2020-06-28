import com.google.gson.Gson;
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
        ChocopyLexer lexer = new ChocopyLexer(CharStreams.fromStream(filePart.getInputStream()));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ChocopyParser parser = new ChocopyParser(tokens);
        ParseTree tree = parser.program();

        //visit first node of the parse tree and return the result of the analysis
        Visitor loader = new Visitor();
        System.out.println("entró");
        loader.visit(tree);
        System.out.println("terminó");
        Gson gson = new Gson();
        return gson.toJson(loader.history);
    }
}
