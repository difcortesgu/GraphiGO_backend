import spark.Request;
import spark.Response;
import spark.Route;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        staticFiles.location("/public");


        get("/hello", new Route() {
            public Object handle(Request req, Response res) throws Exception {
                return "Hello World";
            }
        });

        post("/analize", new Analizer());
    }
}
