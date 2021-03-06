import com.google.gson.Gson;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.io.IOUtils;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class Analizer implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {
        // Create a stream to hold the output
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setErr(ps);
        Gson gson = new Gson();
        //read file from request
        request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("C:/tmp"));
        Part filePart = request.raw().getPart("myfile");


        Part inputPart = request.raw().getPart("input");
        String[] input;
        if(inputPart != null){
            Object[] aux = IOUtils.readLines(inputPart.getInputStream(), StandardCharsets.UTF_8).toArray();
            input = Arrays.copyOf(aux, aux.length, String[].class);
        }else{
            input = new String[0];
        }
        //set up the lexer and parser
        ChocopyLexer lexer = new ChocopyLexer(CharStreams.fromStream(filePart.getInputStream()));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ChocopyParser parser = new ChocopyParser(tokens);
        ParseTree tree = parser.program();

        //visit first node of the parse tree and return the result of the analysis
        ArrayList<HistoryPoint> history = new ArrayList<>();
        Visitor loader = new Visitor(history, input);
        if(baos.size() > 0){
            for (int i = 0; i < baos.toString().split("\n").length; i++) {
                history.get(0).outputs.add(baos.toString().split("\n")[i]);
            }
            return gson.toJson(history);
        }
        loader.visit(tree);
        return gson.toJson(history);
    }
}
