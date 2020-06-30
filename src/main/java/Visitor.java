import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;

public class Visitor extends ChocopyBaseVisitor<Record>{

    ArrayList<HistoryPoint> history;
    Stack<String> callStack;
    Hashtable<String, Hashtable<String, Record>> symbolTables;
    Hashtable<String, Record> symbolTable;
    ArrayList<String> outputs;
    String[] input;
    int current_input_line;

    public Visitor(ArrayList<HistoryPoint> history, String[] input) {
        current_input_line = 0;
        this.input = input;
        this.history = history;
        outputs = new ArrayList<>();
        callStack = new Stack<>();
        symbolTables = new Hashtable<>();
        Hashtable<String, Record> program = new Hashtable<>();
        symbolTables.put("program", program);
        callStack.push("program");
        symbolTable = symbolTables.get(callStack.peek());
        symbolTable.put(".", new Record("program", "program"));
        history.add(new HistoryPoint(callStack, symbolTables, outputs, 0));
    }

    public Record evalOp( Record r1, Record r2, String op, int lineNumber, int columnNumber) throws Exception {

        // Check for operation compatibility
        if(r1.getValue().equals("None") ^ r2.getValue().equals("None") ){
            throw new Exception("Linea: "+lineNumber+":"+columnNumber+" No son validas las operaciones con \"None\"");
        }

        switch (r1.getType()) {
            case "int":
                if (!r2.getType().equals("int"))
                    throw new Exception("Linea: "+lineNumber+":"+columnNumber+" La operacion \""+op+"\" no esta permitida entre los tipos de datos "+r1.getType()+" y "+r2.getType());
                if (op.equals("is"))
                    throw new Exception("Linea: "+lineNumber+":"+columnNumber+"La operacion \""+op+"\" no esta permitida entre los tipos de datos "+r1.getType()+" y "+r2.getType());
                break;
            case "bool":
                if (!r2.getType().equals("bool"))
                    throw new Exception("Linea: "+lineNumber+":"+columnNumber+"La operacion \""+op+"\" no esta permitida entre los tipos de datos "+r1.getType()+" y "+r2.getType());
                if (!op.equals("==") && !op.equals("!="))
                    throw new Exception("Linea: "+lineNumber+":"+columnNumber+"La operacion \""+op+"\" no esta permitida entre los tipos de datos "+r1.getType()+" y "+r2.getType());
                break;
            case "str":
                if (!r2.getType().equals("str"))
                    throw new Exception("Linea: "+lineNumber+":"+columnNumber+"La operacion \""+op+"\" no esta permitida entre los tipos de datos "+r1.getType()+" y "+r2.getType());
                if (!op.equals("==") && !op.equals("!=") && !op.equals("+"))
                    throw new Exception("Linea: "+lineNumber+":"+columnNumber+"La operacion \""+op+"\" no esta permitida entre los tipos de datos "+r1.getType()+" y "+r2.getType());
                break;
            case "list":
                if (!r2.getType().equals("list") && !op.equals("is"))
                    throw new Exception("Linea: "+lineNumber+":"+columnNumber+"La operacion \""+op+"\" no esta permitida entre los tipos de datos "+r1.getType()+" y "+r2.getType());
                if (!op.equals("+") && !op.equals("is"))
                    throw new Exception("Linea: "+lineNumber+":"+columnNumber+"La operacion \""+op+"\" no esta permitida entre los tipos de datos "+r1.getType()+" y "+r2.getType());
                break;
            default:
                if (!op.equals("is"))
                    throw new Exception("Linea: "+lineNumber+":"+columnNumber+"La operacion \""+op+"\" no esta permitida entre los tipos de datos "+r1.getType()+" y "+r2.getType());
                break;
        }

        switch (op){
            case "+":
                if (r1.getType().equals("int")){
                    return new Record("int", (Integer) r1.getValue() + (Integer) r2.getValue());
                }
                else if (r1.getType().equals("str")){
                    return new Record("str", r1.getValue().toString() + r2.getValue().toString());
                }
                else{
                    Object[] l1 = (Object[]) r1.getValue();
                    Object[] l2 = (Object[]) r2.getValue();
                    Object[] new_array = new Object[l1.length + l2.length];
                    System.arraycopy(l1, 0, new_array, 0, l1.length);
                    System.arraycopy(l2, 0, new_array, l1.length, l2.length);
                    return  new Record("list", new_array);
                }
            case "-":
                return new Record("int", (Integer) r1.getValue() - (Integer) r2.getValue());
            case "*":
                return new Record("int", (Integer) r1.getValue() * (Integer) r2.getValue());
            case "//":
                if((int) r2.getValue() == 0){
                    throw new Exception("Linea: "+lineNumber+":"+columnNumber+" No esta permitida la division entre 0");
                }
                return new Record("int", ((Integer) r1.getValue() - ((Integer) r1.getValue() % (Integer) r2.getValue())) / (Integer) r2.getValue());
            case "%":
                if((int) r2.getValue() == 0){
                    throw new Exception("Linea: "+lineNumber+":"+columnNumber+" No esta permitida la division entre 0");
                }
                return new Record("int", (Integer) r1.getValue() % (Integer) r2.getValue());
            case "==":
                // Esto puede fallar
                return switch (r1.getType()) {
                    case "int" -> new Record("bool", ((Integer) r1.getValue()).equals((Integer) r2.getValue()));
                    // case "str" -> new Record("bool", r1.getValue().equals(r2.getValue()));
                    case "bool" -> new Record("bool", (boolean) r1.getValue() == (boolean) r2.getValue());
                    default -> new Record("bool", r1.getValue().equals(r2.getValue()));
                };
            case "!=":
                // Esto puede fallar
                return switch (r1.getType()) {
                    case "int" -> new Record("bool", !((Integer) r1.getValue()).equals((Integer) r2.getValue()));
                    // case "str" -> new Record("bool", !(r1.getValue()).equals(r2.getValue()));
                    case "bool" -> new Record("bool", (boolean) r1.getValue() != (boolean) r2.getValue());
                    default -> new Record("bool", !r1.getValue().equals(r2.getValue()));
                };
            case "<=":
                return  new Record("bool", (Integer) r1.getValue() <= (Integer) r2.getValue());
            case ">=":
                return  new Record("bool", (Integer) r1.getValue() >= (Integer) r2.getValue());
            case "<":
                return  new Record("bool", (Integer) r1.getValue() < (Integer) r2.getValue());
            case ">":
                return  new Record("bool", (Integer) r1.getValue() > (Integer) r2.getValue());
            case "is":
                return  new Record("bool", r1.getValue() == r2.getValue());
        }
        return null;
    }

    public Record func_eval(String funcName, List<ChocopyParser.ExprContext> expr_ctx, int lineNumber, int columnNumber) throws Exception {
        Record r;
        if ( isClass_() ){
            //IMPORTANTE:el ultimo valor del stack debe ser la instancia de la clase
            r = searchClassMember(funcName);
            if (r == null){
                throw new Exception("Linea "+lineNumber+":"+columnNumber+" El metodo " + funcName + " no ha sido declarado");
            }
        }
        else{
            //IMPORTANTE:el ultimo valor del stack debe ser la instancia de la funcion anidada
            r = searchID(funcName);
            if (r == null){
                throw new Exception("Linea "+lineNumber+":"+columnNumber+" La funcion " + funcName + " no ha sido declarada");
            }
        }

        if (!r.getType().equals("func") && !r.getType().equals("class")){
            throw new Exception("Linea "+lineNumber+":"+columnNumber+" La variable " + funcName + " no es una funcion ni una clase");
        }
        if (r.getType().equals("class")){
            //Get the context of the function
            ChocopyParser.Class_defContext ctxClass = (ChocopyParser.Class_defContext) symbolTables.get("program").get(funcName).getValue();
            String id = UUID.randomUUID().toString();
            callStack.push(id);
            symbolTables.put(id, new Hashtable<>());

            String id1 = id;
            String parent;

            do{
                symbolTable = symbolTables.get(callStack.peek());
                parent = ctxClass.ID(1).getText();
                symbolTable.put(".", new Record("class", ctxClass.ID(0).getText()));
                symbolTable.put("self", new Record(ctxClass.ID(0).getText(), id1));
                if(!parent.equals("object")){
                    id1 = UUID.randomUUID().toString();
                    symbolTables.put(id1, new Hashtable<>());
                    symbolTable.put("super", new Record(parent, id1));
                    callStack.push(id1);
                    ctxClass = (ChocopyParser.Class_defContext) symbolTables.get("program").get(parent).getValue();
                }else{
                    symbolTable.put("super", new Record(parent, "object"));
                }
            } while (!parent.equals("object"));
            //stack lleno con las instancias de las clases padres

            do{
                visitClass_body(ctxClass.class_body());
                if (symbolTable.containsKey("__init__")){
                    func_eval("__init__", expr_ctx, ctxClass.start.getLine(), ctxClass.start.getCharPositionInLine());
                }
                callStack.pop();
                symbolTable = symbolTables.get(callStack.peek());
                if (!callStack.peek().equals("program")){
                    ctxClass = (ChocopyParser.Class_defContext) symbolTables.get("program").get(symbolTable.get("self").getType()).getValue();
                }
            }while(!callStack.peek().equals("program"));

            return new Record(funcName, id);
        }else if (r.getType().equals("func")){
            //Get the context of the function
            ChocopyParser.Func_defContext ctxFunc = (ChocopyParser.Func_defContext) r.getValue();

            // Set the scope to be inside of the function
            UUID id = UUID.randomUUID();
            callStack.push(id.toString());
            symbolTables.put(id.toString(), new Hashtable<>());
            symbolTable = symbolTables.get(callStack.peek());
            symbolTable.put(".", new Record("func", funcName));

            // Check if the parameters match
            if (isMethod() && ctxFunc.typed_var().size() < 1) {
                throw new Exception("Linea "+lineNumber+":"+ctxFunc.typed_var(ctxFunc.typed_var().size()-1).start.getCharPositionInLine()+" El parametro self es obligatorio");
            }
            for (int i = 0, j = 0; i < ctxFunc.typed_var().size(); i++){
                // Inside of this function params must be declared in the symbol table
                try {

                    Record param = visitTyped_var(ctxFunc.typed_var(i));
                    if (isMethod() && param.getValue().equals("self")){
                        String aux = callStack.pop();
                        Record self = symbolTables.get(callStack.peek()).get("self");
                        callStack.push(aux);
                        if (!inherits(param, self)){
                            throw new Exception("Linea "+lineNumber+":"+ctxFunc.typed_var(i).start.getCharPositionInLine()+" El parametro "+ param.getValue() +" debe ser de tipo \""+ param.getType() +"\" y se recibio \""+ self.getType() +"\"");
                        }
                        symbolTable.put((String) param.getValue(), self);
                    }
                    else{
                        String aux = callStack.pop();
                        symbolTable = symbolTables.get(callStack.peek());
                        Record expr = visitExpr(expr_ctx.get(j));
                        callStack.push(aux);
                        symbolTable = symbolTables.get(callStack.peek());
                        //REVISAR
                        if (!expr.getType().equals(param.getType()) && !param.getType().equals("object")){
                            throw new Exception("Linea "+lineNumber+":"+ctxFunc.typed_var(i).start.getCharPositionInLine()+" El parametro "+ param.getValue() +" debe ser de tipo \""+ param.getType() +"\" y se recibio \""+ expr.getType() +"\"");
                        }
                        symbolTable.put((String) param.getValue(), expr);
                        j++;
                    }

                }catch (IndexOutOfBoundsException e){
                    throw new Exception("Linea "+lineNumber+":"+ctxFunc.typed_var(ctxFunc.typed_var().size()-1).start.getCharPositionInLine()+" El numero de parametros no coincide");
                }
            }

            Record func_body =  visitFunc_body(ctxFunc.func_body());

            if(func_body == null){
                callStack.pop();
                symbolTable = symbolTables.get(callStack.peek());
                return new Record("None", "None");
            }
            if (ctxFunc.type() != null){
                String type = (String) (visitType(ctxFunc.type())).getValue();
                if (!func_body.getType().equals(type) && !func_body.getType().equals("None")){
                    throw new Exception("Linea "+lineNumber+":"+columnNumber+" La funcion \""+funcName+"\" debe retornar el tipo \""+type+"\" y se encontro el tipo \""+func_body.getType()+"\"");
                }
            }
            // Set the scope to be outside of the function again
            callStack.pop();
            symbolTable = symbolTables.get(callStack.peek());
            return  new Record(func_body);
        }
        return null;
    }

    //Search inside functions until it reaches a class or program
    public Record searchID(String id) {
        LinkedList<String> aux = new LinkedList<>();
        while (isProgram() || isMethod() || isFunction()){
            //update symbol table
            String st_id = callStack.pop();
            aux.add(st_id);
            Hashtable<String, Record> st = symbolTables.get(st_id);

            //search for id
            if (st.containsKey(id)) {
                while(!aux.isEmpty()){
                    callStack.push(aux.removeLast());
                }
                return st.get(id);
            }
        }
        while(!aux.isEmpty()){
            callStack.push(aux.remove());
        }
        return null;
    }

    //Search inside classes and its parents
    public Record searchClassMember(String id){
        String st_id = callStack.peek();
        Hashtable<String, Record> st;
        while (!st_id.equals("object")){
            st = symbolTables.get(st_id);

            //search for id
            if (st.containsKey(id) && !(!st_id.equals(callStack.peek()) && id.equals("__init__"))) {
                return st.get(id);
            }

            //update symbol table
            st_id = (String) st.get("super").getValue();
        }
        return null;
    }

    public boolean inherits(Record r1, Record r2){
        var st = symbolTables.get((String)r2.getValue());
        Record parent;
        Record self;
        do{
            parent = st.get("super");
            self = st.get("self");
            if(r1.getType().equals(self.getType())){
                return true;
            }
            st = symbolTables.get((String)parent.getValue());
        }while(!(parent.getValue()).equals("object"));
        return false;
    }

    //check if you're inside method in a class
    public boolean isMethod(){
        if (callStack.size()<2) return false;
        String aux = callStack.pop();
        Hashtable<String, Record> st = symbolTables.get(callStack.peek());
        if (st.get(".").getType().equals("class")){
            callStack.push(aux);
            return true;
        }
        callStack.push(aux);
        return false;
    }

    //Checks if you've reached the class context
    public boolean isClass_(){
        return !callStack.isEmpty() && symbolTables.get(callStack.peek()).get(".").getType().equals("class");
    }

    //Checks if you've reached the top-level context
    public boolean isProgram(){
        return !callStack.isEmpty() && symbolTables.get(callStack.peek()).get(".").getType().equals("program");
    }

    public boolean isFunction(){
        return !callStack.isEmpty() && symbolTables.get(callStack.peek()).get(".").getType().equals("func");
    }

    @Override
    public Record visitProgram(ChocopyParser.ProgramContext ctx) {
        try {
            if (!ctx.stmt().isEmpty()) {
                for (int i = 0; i < ctx.stmt().size(); i++) {
                    Record r = visitStmt(ctx.stmt(i));
                    if (r != null) {
                        return r;
                    }
                }
                return null;
            }
            if (ctx.class_def() != null) {
                visitClass_def(ctx.class_def());
                return visitProgram(ctx.program());
            }
            if (ctx.var_def() != null) {
                visitVar_def(ctx.var_def());
                return visitProgram(ctx.program());
            }
            if (ctx.func_def() != null) {
                visitFunc_def(ctx.func_def());
                return visitProgram(ctx.program());
            }
        }catch (Exception e){
            outputs.add(e.getMessage());
            history.add(new HistoryPoint(callStack, symbolTables, outputs, ctx.stop.getLine()));
        }
        return null;
    }

    @Override
    public Record visitSimple_stmt(ChocopyParser.Simple_stmtContext ctx) throws Exception {
        if(ctx.PASS() != null){
            //pass
            return null;
        }
        if(ctx.RETURN() != null){
            Record r;
            if(ctx.expr() != null){
                r = visitExpr(ctx.expr());
            }else{
                r = new Record("None", "None");
            }
            history.add(new HistoryPoint(callStack, symbolTables, outputs, ctx.start.getLine()));
            return r;
        }
        if(ctx.PRINT() != null){

            Record r = visitExpr(ctx.expr());
            //puede fallar
            if (r.getType().equals("None") || r.getValue().equals("None")){
                throw new Exception("Linea "+ctx.start.getLine()+":"+ctx.expr().start.getCharPositionInLine()+" No se puede imprimir una variable de tipo \"None\"");
            }
            outputs.add(r.toString());
            history.add(new HistoryPoint(callStack, symbolTables, outputs, ctx.start.getLine()));
            System.out.println(r.toString());
            return null;
        }
        if(ctx.target() !=  null) {
            //(target IGUAL)+ expr
            Record r1 = visitExpr(ctx.expr());
            //itera sobre todos los target y dependiendo del tipo de "operacion" y cambia el valor anidado de la tabla de simbolos
            for (int i = 0; i < ctx.target().size(); i++) {
                Record target = visitTarget(ctx.target(i));
                if(!target.getType().equals(r1.getType()) && !r1.getType().equals("None")){
                    if(!symbolTables.get("program").containsKey(target.getType()) || !symbolTables.get("program").containsKey(r1.getType()) || !inherits(target, r1)){
                        throw new Exception("Linea "+ctx.start.getLine()+":"+ctx.target(i).start.getCharPositionInLine()+" Los tipos de datos \"" + target.getType() + "\" y \"" + r1.getType() + "\" no coinciden");
                    }
                }
                target.setValue(r1.getValue());
            }
            history.add(new HistoryPoint(callStack, symbolTables, outputs, ctx.start.getLine()));
            return null;
        }
        if(ctx.expr() != null){
            //expr
            visitExpr(ctx.expr());
            return null;
        }
        return null;
    }

    @Override
    public Record visitStmt(ChocopyParser.StmtContext ctx) throws Exception {
        if(ctx.simple_stmt() != null){
            //simple_stmt NEWLINE
            return visitSimple_stmt(ctx.simple_stmt());
        }
        if(ctx.IF() != null){
            //IF expr DOS_PUNTOS block (ELIF expr DOS_PUNTOS block )* (ELSE DOS_PUNTOS block)?
            Record r = visitExpr(ctx.expr(0));
            history.add(new HistoryPoint(callStack, symbolTables, outputs, ctx.start.getLine()));
            if (!r.getType().equals("bool")){
                throw new Exception("Linea "+ctx.start.getLine()+":"+ctx.expr(0).start.getCharPositionInLine()+" La comparacion solo es valida entre booleanos, se recibio:\""+r.getType()+"\"");
            }
            if((boolean) r.getValue()){
                return visitBlock(ctx.block(0));
            }

            if(ctx.ELIF() != null){
                for (int i = 1; i < ctx.expr().size(); i++) {
                    r = visitExpr(ctx.expr(i));
                    history.add(new HistoryPoint(callStack, symbolTables, outputs, ctx.start.getLine()));
                    if (!r.getType().equals("bool")){
                        throw new Exception("Linea "+ctx.start.getLine()+":"+ctx.expr(i).start.getCharPositionInLine()+" La comparacion solo es valida entre booleanos, se recibio:\""+r.getType()+"\"");
                    }
                    if((boolean) r.getValue()){
                        return visitBlock(ctx.block(i));
                    }
                }
            }
            if(ctx.ELSE() != null){
                return visitBlock(ctx.block(ctx.expr().size()));
            }
            return null;
        }
        if(ctx.WHILE() != null){
            //WHILE expr DOS_PUNTOS block
            Record r = visitExpr(ctx.expr(0));
            if (!r.getType().equals("bool")){
                throw new Exception("Linea "+ctx.start.getLine()+":"+ctx.expr(0).start.getCharPositionInLine()+" La comparacion solo es valida entre booleanos, se recibio:\""+r.getType()+"\"");
            }
            history.add(new HistoryPoint(callStack, symbolTables, outputs, ctx.start.getLine()));
            while((boolean) r.getValue()){
                Record block_return = visitBlock(ctx.block(0));
                if(block_return != null){
                    return block_return;
                }
                r = visitExpr(ctx.expr(0));
                history.add(new HistoryPoint(callStack, symbolTables, outputs, ctx.start.getLine()));
            }
            return null;
        }
        if(ctx.FOR() != null){
            //FOR ID IN expr DOS_PUNTOS block;
            Record r = visitExpr(ctx.expr(0));
            if (!r.getType().equals("list") && !r.getType().equals("str")){
                throw new Exception("Linea "+ctx.start.getLine()+":"+ctx.expr(0).start.getCharPositionInLine()+" Solo es posible iterar sobre listas o strings, se recibio:\""+r.getType()+"\"");
            }
            if(symbolTable.containsKey(ctx.ID().getText())) {
                throw new Exception("Linea "+ctx.start.getLine()+":"+ctx.start.getCharPositionInLine()+" La variable " + ctx.ID().getText() + " ya fue declarada");
            }
            symbolTable.put(ctx.ID().getText(), new Record("None", "None"));
            if(r.getType().equals("str")) {
                String values = (String) r.getValue();
                for (int i = 0; i < values.length(); i++) {
                    Record id = symbolTable.get(ctx.ID().getText());
                    id.setValue(values.charAt(i));
                    id.setType("str");
                    history.add(new HistoryPoint(callStack, symbolTables, outputs, ctx.start.getLine()));
                    Record block_return = visitBlock(ctx.block(0));
                    if(block_return != null){
                        symbolTable.remove(ctx.ID().getText());
                        return block_return;
                    }
                }
            }
            else {
                for (Object o :(Object[]) r.getValue()) {
                    Record id = symbolTable.get(ctx.ID().getText());
                    id.setType(((Record) o).getType());
                    id.setValue(((Record) o).getValue());
                    history.add(new HistoryPoint(callStack, symbolTables, outputs, ctx.start.getLine()));
                    Record block_return = visitBlock(ctx.block(0));
                    if(block_return != null){
                        symbolTable.remove(ctx.ID().getText());
                        return block_return;
                    }

                }
            }
            symbolTable.remove(ctx.ID().getText());
            return null;
        }
        return null;
    }

    @Override
    public Record visitBlock(ChocopyParser.BlockContext ctx) throws Exception {
        for (int i = 0; i < ctx.stmt().size(); i++) {
            Record r = visitStmt(ctx.stmt(i));
            if (r != null) {
                return r;
            }
        }
        return null;
    }

    @Override
    public Record visitLiteral(ChocopyParser.LiteralContext ctx){

        if (ctx.getText().equals("None"))
            return new Record("None", "None");
        if (ctx.getText().equals("True"))
            return new Record("bool", true);
        if (ctx.getText().equals("False"))
            return new Record("bool", false);
        if (ctx.IDSTRING()!= null){
            Record r = new Record("str", ctx.IDSTRING().getText());
            r.setValue(r.getValue().toString().replace("\"", ""));
            return r;
        }
        if (ctx.INTEGER()!= null)
            return new Record("int", Integer.parseInt(ctx.INTEGER().getText()));
        if (ctx.STRING()!= null){
            Record r = new Record("str", ctx.STRING().getText());
            r.setValue(r.getValue().toString().replace("\"", ""));
            return r;
        }
        return null;
    }

    @Override
    public Record visitTyped_var(ChocopyParser.Typed_varContext ctx){
        if (ctx.ID()!= null){
            Record r = visitType(ctx.type());
            if (!symbolTable.containsKey(ctx.ID().getText())){
                return new Record((String) r.getValue(), ctx.ID().getText());
            }
        }
        return null;
    }

    @Override
    public Record visitType(ChocopyParser.TypeContext ctx){
        if (ctx.ID()!= null){
            return new Record("str", ctx.ID().getText());
        }
        if (ctx.IDSTRING()!= null){
            Record r = new Record("str", ctx.IDSTRING().getText());
            r.setValue(r.getValue().toString().replace("\"", ""));
            return r;
        }
        if (ctx.COR_DER()!= null){
            return new Record("str", "list");
        }
        return null;
    }

    @Override
    public Record visitCmp(ChocopyParser.CmpContext ctx) {
        if (ctx.DOBLE_IGUAL()!=null)
            return new Record("comparison", "==");
        if (ctx.DIFERENTE()!=null)
            return new Record("comparison", "!=");
        if (ctx.MAYOR_IGUAL()!=null)
            return new Record("comparison", ">=");
        if (ctx.MENOR_IGUAL()!=null)
            return new Record("comparison", "<=");
        if (ctx.MENOR()!=null)
            return new Record("comparison", "<");
        if (ctx.MAYOR()!=null)
            return new Record("comparison", ">");
        if (ctx.IS()!=null)
            return new Record("comparison", "is");
        return null;
    }

    @Override
    public Record visitOp_suma(ChocopyParser.Op_sumaContext ctx) {
        if (ctx.MAS() != null)
            return new Record("operation", "+");
        if (ctx.MENOS() != null)
            return new Record("operation", "-");
        return null;
    }

    @Override
    public Record visitOp_mul(ChocopyParser.Op_mulContext ctx) {
        if (ctx.MULTIPLICA()!= null)
            return new Record("operation", "*");
        if (ctx.DIV_ENTERA() != null)
            return new Record("operation", "//");
        if (ctx.MODULO()!=null)
            return new Record("operation", "%");
        return null;
    }

    @Override
    public Record visitExpr(ChocopyParser.ExprContext ctx) throws Exception {
        if (ctx.IF() !=  null){
            // EXPR_OR IF EXPR_OR ELSE EXPR
            Record r1 = visitExpr_or(ctx.expr_or(0));
            Record r2 = visitExpr_or(ctx.expr_or(1));
            Record r3 = visitExpr(ctx.expr());
            if (!r2.getType().equals("bool")){
                throw new Exception("Linea "+ctx.start.getLine()+":"+ctx.expr_or(1).start.getCharPositionInLine()+" La condicion debe ser de tipo booleano, se recibio:\""+r2.getType()+"\"");
            }
            if ((boolean) r2.getValue())
                return r1;
            else
                return r3;
        }
        return visitExpr_or(ctx.expr_or(0));
    }

    @Override
    public Record visitExpr_or(ChocopyParser.Expr_orContext ctx) throws Exception {
        if (ctx.OR() !=  null){
            // expr_and OR simple_expr
            Record r1 = visitExpr_or(ctx.expr_or());
            Record r2 = visitExpr_and(ctx.expr_and());
            if (!r1.getType().equals("bool") || !r2.getType().equals("bool")){
                throw new Exception("Linea "+ctx.start.getLine()+":"+ctx.start.getCharPositionInLine()+" La operacion or solo es valida en booleanos, se recibio:\""+r1.getType()+"\", \""+r2.getType()+"\"");
            }
            return new Record("bool", (boolean) r1.getValue() || (boolean) r2.getValue());
        }
        return visitExpr_and(ctx.expr_and());
    }

    @Override
    public Record visitExpr_and(ChocopyParser.Expr_andContext ctx) throws Exception {
        if (ctx.AND() !=  null){
            // expr_and AND simple_expr
            Record r1 = visitExpr_and(ctx.expr_and());
            Record r2 = visitSimple_expr(ctx.simple_expr());
            if (!r1.getType().equals("bool") || !r2.getType().equals("bool")){
                throw new Exception("Linea "+ctx.start.getLine()+":"+ctx.start.getCharPositionInLine()+" La operacion and solo es valida en booleanos, se recibio:\""+r1.getType()+"\", \""+r2.getType()+"\"");
            }
            return new Record("bool", (boolean) r1.getValue() && (boolean) r2.getValue());
        }
        return visitSimple_expr(ctx.simple_expr());
    }

    @Override
    public Record visitCexpr(ChocopyParser.CexprContext ctx) throws Exception {
        if (ctx.cexpr() != null){
            //cexpr_sum op_suma cexpr_mul
            Record r1 = visitCexpr(ctx.cexpr());
            Record r2 = visitCexpr_sum(ctx.cexpr_sum());
            String op = (String) visitCmp(ctx.cmp()).getValue();

            return evalOp(r1,r2,op, ctx.start.getLine(), ctx.cmp().start.getCharPositionInLine());
        }
        //cexpr_mul
        return visitCexpr_sum(ctx.cexpr_sum());
    }

    @Override
    public Record visitCexpr_sum(ChocopyParser.Cexpr_sumContext ctx) throws Exception {
        if (ctx.cexpr_sum() != null){
            //cexpr_sum op_suma cexpr_mul
            Record r1 = visitCexpr_sum(ctx.cexpr_sum());
            Record r2 = visitCexpr_mul(ctx.cexpr_mul());
            String op = (String) visitOp_suma(ctx.op_suma()).getValue();

            return evalOp(r1,r2,op, ctx.start.getLine(), ctx.op_suma().start.getCharPositionInLine());
        }
        //cexpr_mul
        return visitCexpr_mul(ctx.cexpr_mul());
    }

    @Override
    public Record visitCexpr_mul(ChocopyParser.Cexpr_mulContext ctx) throws Exception {
        if (ctx.cexpr_mul() != null){
            //cexpr_mul op_mul simple_value
            Record r1 = visitCexpr_mul(ctx.cexpr_mul());
            Record r2 = visitSimple_value(ctx.simple_value());
            String op = (String) visitOp_mul(ctx.op_mul()).getValue();
            return evalOp(r1,r2,op, ctx.start.getLine(), ctx.op_mul().start.getCharPositionInLine());
        }
        //simple_value
        return visitSimple_value(ctx.simple_value());
    }

    @Override
    public Record visitTarget(ChocopyParser.TargetContext ctx) throws Exception {
        if(ctx.PUNTO() != null){
            getSimpleValue(ctx.simple_value(), ctx.start.getLine(), ctx.start.getCharPositionInLine());

            // SIMPLE_VALUE . ID

            return getClassMember(ctx.ID(), ctx.start.getLine(), ctx.start.getCharPositionInLine());
        }
        if(ctx.ID() !=  null){
            return getRecord(ctx.ID(), ctx.start.getLine(), ctx.start.getCharPositionInLine());
        }
        if(ctx.COR_IZQ() != null){
            Record r = visitSimple_value(ctx.simple_value());
            Record i = visitExpr(ctx.expr());
            if (!r.getType().equals("list") && !r.getType().equals("str")){
                throw new Exception("Linea "+ctx.start.getLine()+":"+ctx.start.getCharPositionInLine()+" La operacion [] no esta permitida para tipos de dato diferentes a \"str\", \"list\"");
            }
            else if (!i.getType().equals("int")){
                throw new Exception("Linea "+ctx.start.getLine()+":"+ctx.expr().start.getCharPositionInLine()+" El index debe ser de tipo \"int\"");
            }
            else if (r.getValue().equals("None")){
                throw new Exception("Linea "+ctx.start.getLine()+":"+ctx.expr().start.getCharPositionInLine()+" El index no se encuentra en el arreglo");
            }
            int len = 0;
            if (r.getType().equals("list"))
                len = ((Object[]) r.getValue()).length;
            else if (r.getType().equals("str"))
                len = ((String) r.getValue()).length();

            if ((int) i.getValue() >= len){
                throw new Exception("Linea "+ctx.start.getLine()+":"+ctx.expr().start.getCharPositionInLine()+" El index no se encuentra en el arreglo");
            }
            if (r.getType().equals("list")){
                return (Record)((Object[]) r.getValue())[(Integer) i.getValue()];
            }
            if (r.getType().equals("str")){
                return new Record("str", ((String) r.getValue()).charAt((Integer) i.getValue()));
            }
        }
        return null;
    }

    private void getSimpleValue(ChocopyParser.Simple_valueContext simple_valueContext, int line, int charPositionInLine) throws Exception {
        Record r = visitSimple_value(simple_valueContext);

        if(r.getValue().equals("None")){
            throw new Exception("Linea "+ line +":"+ charPositionInLine +" No se puede acceder a una propiedad de una variable con valor \"None\"");
        }

        if (!symbolTables.get("program").containsKey(r.getType())){
            throw new Exception("Linea "+ line +":"+ charPositionInLine +" No se encontro el tipo de dato \"" + r.getType() + "\"");
        }

        if (!symbolTables.get("program").get(r.getType()).getType().equals("class")) {
            throw new Exception("Linea "+ line +":"+ charPositionInLine +" La expresion \"" + simple_valueContext.getText() + "\" no retorna una clase");
        }

        callStack.push((String) r.getValue());
        symbolTable = symbolTables.get((String)r.getValue());
    }

    @Override
    public Record visitSimple_expr(ChocopyParser.Simple_exprContext ctx) throws Exception {
        if (ctx.cexpr() != null){
            //cexpr
            return visitCexpr(ctx.cexpr());
        }
        if (ctx.NOT() !=  null){
            //NOT EXPR
            Record r = visitSimple_expr(ctx.simple_expr());
            if (!r.getType().equals("bool")){
                throw new Exception("Linea "+ctx.start.getLine()+":"+ctx.start.getCharPositionInLine()+" La operacion not solo es valida en booleanos, se recibio:\""+r.getType()+"\"");
            }
            r.setValue(!(boolean)r.getValue());
            return r;
        }
        return null;
    }

    @Override
    public Record visitSimple_value(ChocopyParser.Simple_valueContext ctx) throws Exception {

        if (ctx.ID() !=  null){

            if (ctx.PUNTO() !=  null){

                getSimpleValue(ctx.simple_value(), ctx.start.getLine(), ctx.start.getCharPositionInLine());

                if (ctx.PAR_IZQ() !=  null){
                    // SIMPLE_VALUE . ID ( EXPR ... )
                    Record result = (Record) func_eval(ctx.ID().getText(), ctx.expr(), ctx.start.getLine(), ctx.start.getCharPositionInLine());
                    callStack.pop();
                    symbolTable = symbolTables.get(callStack.peek());
                    history.add(new HistoryPoint(callStack, symbolTables, outputs, ctx.start.getLine()));
                    return result;
                }

                // SIMPLE_VALUE . ID
                return getClassMember(ctx.ID(), ctx.start.getLine(), ctx.start.getCharPositionInLine());
            }

            if (ctx.PAR_IZQ() !=  null){
                // ID ( EXPR ... )
                history.add(new HistoryPoint(callStack, symbolTables, outputs, ctx.start.getLine()));
                return func_eval(ctx.ID().getText(), ctx.expr(), ctx.start.getLine(), ctx.start.getCharPositionInLine());
            }

            // ID
            return getRecord(ctx.ID(), ctx.start.getLine(), ctx.start.getCharPositionInLine());
        }

        if (ctx.COR_IZQ() != null){

            if (ctx.simple_value() !=  null){
                // SIMPLE_VALUE [ EXPR ]
                Record cexpr = visitSimple_value(ctx.simple_value());
                if (cexpr.getValue().equals("None")){
                    throw new Exception("Linea "+ctx.start.getLine()+":"+ctx.start.getCharPositionInLine()+" La operacion [] no esta permitida para tipos de dato \"None\"");
                }
                if (!cexpr.getType().equals("list") && !cexpr.getType().equals("str")){
                    throw new Exception("Linea "+ctx.start.getLine()+":"+ctx.start.getCharPositionInLine()+" La operacion [] no esta permitida para tipos de dato diferentes a \"str\", \"list\"");
                }
                Record expr = visitExpr(ctx.expr(0));
                if (!expr.getType().equals("int")){
                    throw new Exception("Linea "+ctx.start.getLine()+":"+ctx.expr(0).start.getCharPositionInLine()+" El index debe ser de tipo \"int\"");
                }
                Integer len = 0;
                if (cexpr.getType().equals("list"))
                    len = ((Object[]) cexpr.getValue()).length;
                else if (cexpr.getType().equals("str"))
                    len = ((String) cexpr.getValue()).length();
                if ((int) expr.getValue() < 0 || (Integer) expr.getValue() >= len){
                    if (cexpr.getValue().equals("None")){
                        throw new Exception("Linea "+ctx.start.getLine()+":"+ctx.start.getCharPositionInLine()+" El index no se encuentra en el arreglo");
                    }
                    throw new Exception("Linea "+ctx.start.getLine()+":"+ctx.start.getCharPositionInLine()+" El index no se encuentra en el arreglo");
                }
                if (cexpr.getType().equals("list")){
                    return (Record)((Object[]) cexpr.getValue())[(Integer) expr.getValue()];
                }
                if (cexpr.getType().equals("str")){
                    return new Record("str", ((String) cexpr.getValue()).charAt((Integer) expr.getValue()));
                }
            }
            // [ EXPR ...]
            Object[] l = new Object[ctx.expr().size()];
            for(int i=0; i<ctx.expr().size(); i++){
                l[i] = visitExpr(ctx.expr(i));
            }
            return new Record("list", l);
        }

        if (ctx.literal() != null){
            // LITERAL
            return  visitLiteral(ctx.literal());
        }

        if (ctx.LEN() !=  null){
            // LEN ( EXPR )
            Record r = visitExpr(ctx.expr(0));
            if (!r.getType().equals("str") && !r.getType().equals("list")){
                throw new Exception("Linea "+ctx.start.getLine()+":"+ctx.expr(0).start.getCharPositionInLine()+" La expresion debe ser de tipo \"list\" o \"str\", se recibio:\""+r.getType()+"\"");
            }
            if (r.getValue().equals("None")){
                throw new Exception("Linea "+ctx.start.getLine()+":"+ctx.expr(0).start.getCharPositionInLine()+" La expresion debe ser de tipo \"list\" o \"str\", se recibio:\""+r.getValue()+"\"");
            }
            if (r.getType().equals("str")){
                return new Record("int", ((String)r.getValue()).length());
            }else{
                return new Record("int", ((Object[])r.getValue()).length);
            }
        }

        if (ctx.INPUT() !=  null){
            // INPUT ( )

            if(current_input_line >= input.length){
                throw new Exception("Linea "+ctx.start.getLine()+":"+ctx.start.getCharPositionInLine()+" El programa requiere un input y no se recibio ninguno");
            }
            String input_text = input[current_input_line++];

            history.add(new HistoryPoint(callStack, symbolTables, outputs, ctx.start.getLine()));
            return new Record("str", input_text);
        }

        if (ctx.MENOS() != null){
            // - CEXPR
            Record cexpr = visitSimple_value(ctx.simple_value());
            if (!cexpr.getType().equals("int")){
                throw new Exception("Linea "+ctx.start.getLine()+":"+ctx.expr(0).start.getCharPositionInLine()+" La operacion - no esta permitida para tipos de dato diferentes a \"int\"");
            }
            cexpr.setValue(- (Integer)cexpr.getValue());
            return cexpr;
        }

        if (ctx.PAR_IZQ() != null){
            // ( EXPR )
            return visitExpr(ctx.expr(0));
        }

        return null;
    }

    private Record getRecord(TerminalNode id, int line, int charPositionInLine) throws Exception {
        Record r;
        String varName = id.getText();
        if ( isClass_() ){
            r = searchClassMember(varName);
            if (r == null){
                throw new Exception("Linea "+ line +":"+ charPositionInLine +" El atributo " + varName + " no ha sido declarado");
            }
        }
        else{
            r = searchID(varName);
            if (r == null){
                throw new Exception("Linea "+ line +":"+ charPositionInLine +" La variable " + varName + " no ha sido declarada");
            }
        }
        if (r.getType().equals("func") || r.getType().equals("class")){
            throw new Exception("Linea "+ line +":"+ charPositionInLine +" "+varName +" no una variable");
        }
        return r;
    }

    private Record getClassMember(TerminalNode id, int line, int charPositionInLine) throws Exception {
        Record r2 = searchClassMember(id.getText());
        if (r2 == null){
            throw new Exception("Linea "+ line +":"+ charPositionInLine +" El atributo " + id.getText() + " no ha sido declarado");
        }
        callStack.pop();
        symbolTable = symbolTables.get(callStack.peek());
        return r2;
    }

    @Override
    public Record visitFunc_body(ChocopyParser.Func_bodyContext ctx) throws Exception {
        if (ctx.global_decl() != null){
            visitGlobal_decl(ctx.global_decl());
            return visitFunc_body(ctx.func_body());
        }
        if (ctx.nonlocal_decl() != null){
            visitNonlocal_decl(ctx.nonlocal_decl());
            return visitFunc_body(ctx.func_body());
        }
        if (ctx.var_def() != null){
            visitVar_def(ctx.var_def());
            return visitFunc_body(ctx.func_body());
        }
        if (ctx.func_def() != null){
            visitFunc_def(ctx.func_def());
            return visitFunc_body(ctx.func_body());
        }
        if (!ctx.stmt().isEmpty()){
            for (int i = 0; i < ctx.stmt().size(); i++) {
                Record r = visitStmt(ctx.stmt(i));
                if (r != null) {
                    return r;
                }
            }
            return null;
        }
        return null;
    }

    @Override
    public Record visitFunc_def(ChocopyParser.Func_defContext ctx) throws Exception {
        String funcName = ctx.ID().getText();

        if ( isClass_() ){
            if (searchClassMember(funcName) != null  && !funcName.equals("__init__")){
                throw new Exception("Linea "+ctx.start.getLine()+":"+ctx.start.getCharPositionInLine()+" El metodo " + funcName + " ya fue declarado");
            }
        }
        else{
            if (searchID(funcName) != null){
                throw new Exception("Linea "+ctx.start.getLine()+":"+ctx.start.getCharPositionInLine()+" La funcion " + funcName + " ya fue declarada");
            }
        }

        Record func = new Record("func", ctx);
        symbolTable.put(funcName, func);
        history.add(new HistoryPoint(callStack, symbolTables, outputs, ctx.start.getLine()));
        return  null;
    }

    @Override
    public Record visitVar_def(ChocopyParser.Var_defContext ctx) throws Exception {
        String varName = ctx.typed_var().ID().getText();
        if ( isClass_() ){
            if (searchClassMember(varName) != null){
                throw new Exception("Linea "+ctx.start.getLine()+":"+ctx.start.getCharPositionInLine()+" El atributo " + varName + " ya fue declarado");
            }
        }
        else{
            if (searchID(varName) != null){
                throw new Exception("Linea "+ctx.start.getLine()+":"+ctx.start.getCharPositionInLine()+" La variable " + varName + " ya fue declarada");
            }
        }

        Record literal = (Record)visitLiteral(ctx.literal());
        Record type = visitType(ctx.typed_var().type());

        if (!literal.getType().equals(type.getValue()) && !literal.getValue().equals("None")){
            throw new Exception("Linea "+ctx.start.getLine()+":"+ctx.start.getCharPositionInLine()+" No se puede asignar un " + type.getValue() + " a una variable " + literal.getType());
        }

        Record var = new Record((String) type.getValue(), literal.getValue());
        symbolTable.put(varName, var);
        history.add(new HistoryPoint(callStack, symbolTables, outputs, ctx.start.getLine()));
        return  null;
    }

    @Override
    public Record visitClass_body(ChocopyParser.Class_bodyContext ctx) throws Exception {
        if (ctx.PASS() != null){
            return null;
        }
        if (ctx.class_body_def() != null){
            return visitClass_body_def(ctx.class_body_def());
        }
        return null;
    }

    @Override
    public Record visitClass_body_def(ChocopyParser.Class_body_defContext ctx) throws Exception {
        if (ctx.var_def() != null){
            if (ctx.class_body_def() != null){
                visitVar_def(ctx.var_def());
                return visitClass_body_def(ctx.class_body_def());
            }
            return visitVar_def(ctx.var_def());
        }
        if (ctx.func_def() != null){
            if (ctx.class_body_def() != null){
                visitFunc_def(ctx.func_def());
                return visitClass_body_def(ctx.class_body_def());
            }
            return visitFunc_def(ctx.func_def());
        }
        return null;
    }

    @Override
    public Record visitClass_def(ChocopyParser.Class_defContext ctx) throws Exception {
        String className = ctx.ID(0).getText();
        String parentName = ctx.ID(1).getText();

        if (symbolTable.containsKey(className)){

            throw new Exception("Linea "+ctx.start.getLine()+":"+ctx.start.getCharPositionInLine()+" La clase " + className + " ya fue definida");
        }
        if (!parentName.equals("object") && !symbolTable.containsKey(parentName)){
            throw new Exception("Linea "+ctx.start.getLine()+":"+ctx.start.getCharPositionInLine()+" La clase " + parentName + " no ha sido definida");
        }
        Record new_class = new Record("class", ctx);
        symbolTable.put(className, new_class);
        history.add(new HistoryPoint(callStack, symbolTables, outputs, ctx.start.getLine()));
        return  null;
    }

    @Override
    public Record visitGlobal_decl(ChocopyParser.Global_declContext ctx) throws Exception {
        Hashtable<String, Record> st = symbolTables.get("program");

        //search for id
        if (!st.containsKey(ctx.ID().getText())) {
            throw new Exception("Linea "+ctx.start.getLine()+":"+ctx.start.getCharPositionInLine()+" La variable " + ctx.ID().getText() + " no ha sido declarada");
        }
        symbolTable.put(ctx.ID().getText(),st.get(ctx.ID().getText()));
        return st.get(ctx.ID().getText());
    }

    @Override
    public Record visitNonlocal_decl(ChocopyParser.Nonlocal_declContext ctx) throws Exception {
        String aux = callStack.pop();
        Hashtable<String, Record> st = symbolTables.get(callStack.peek());

        //search for id
        if (!st.containsKey(ctx.ID().getText())) {
            throw new Exception("Linea "+ctx.start.getLine()+":"+ctx.start.getCharPositionInLine()+" La variable " + ctx.ID().getText() + " no ha sido declarada");
        }
        symbolTable.put(ctx.ID().getText(),st.get(ctx.ID().getText()));
        callStack.push(aux);
        return st.get(ctx.ID().getText());
    }
}

