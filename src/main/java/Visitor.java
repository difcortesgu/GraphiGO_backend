public class Visitor extends GoParserBaseVisitor<Object> {
    @Override
    public Object visitSourceFile(GoParser.SourceFileContext ctx) {
        super.visitSourceFile(ctx);
        return "hola";
    }
}
