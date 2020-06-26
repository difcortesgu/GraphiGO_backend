import java.util.*;

public class Visitor extends ChocopyBaseVisitor<Record>{

    Stack<String> callStack;
    Hashtable<String, Hashtable<String, Record>> symbolTables;
    Hashtable<String, Record> symbolTable;

    public Visitor() {
        callStack = new Stack<>();
        symbolTables = new Hashtable<>();
        Hashtable<String, Record> program = new Hashtable<>();
        symbolTables.put("program", program);
        callStack.push("program");
        symbolTable = symbolTables.get(callStack.peek());
        symbolTable.put(".", new Record("program", null));
    }

    public Record evalOp( Record r1, Record r2, String op ){

        // Check for operation compatibility
        try {
            switch (r1.getType().getFirst()) {
                case "int":
                    if (!r2.getType().getFirst().equals("int"))
                        throw new Exception();
                    if (op.equals("is"))
                        throw new Exception();
                    break;
                case "bool":
                    if (!r2.getType().getFirst().equals("bool"))
                        throw new Exception();
                    if (!op.equals("==") && !op.equals("!="))
                        throw new Exception();
                    break;
                case "str":
                    if (!r2.getType().getFirst().equals("str"))
                        throw new Exception();
                    if (!op.equals("==") && !op.equals("!=") && !op.equals("+"))
                        throw new Exception();
                    break;
                case "list":
                    if (!r2.getType().getFirst().equals("list") && !op.equals("is"))
                        throw new Exception();
                    if (!op.equals("+") && !op.equals("is"))
                        throw new Exception();
                    break;
                default:
                    if (!op.equals("is"))
                        throw new Exception();
                    break;
            }
        }catch (Exception e){
            System.err.println("La operacion \""+op+"\" no esta permitida entre los tipos de datos "+r1.getType().getFirst()+" y "+r2.getType().getFirst());
            System.exit(1);
        }
//        if (r1.getValue().equals("None") || r2.getValue().equals("None")){
//            System.err.println("La operacion \""+op+"\" no esta permitida entre los tipos de datos \"None\"");
//            System.exit(1);
//        }

        switch (op){
            case "+":
                if (r1.getType().getFirst().equals("int")){
                    return new Record("int", (Integer) r1.getValue() + (Integer) r2.getValue());
                }
                else if (r1.getType().getFirst().equals("str")){
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
                    System.err.println("No esta permitida la division entre 0");
                    System.exit(1);
                }
                return new Record("int", ((Integer) r1.getValue() - ((Integer) r1.getValue() % (Integer) r2.getValue())) / (Integer) r2.getValue());
            case "%":
                if((int) r2.getValue() == 0){
                    System.err.println("No esta permitida la division entre 0");
                    System.exit(1);
                }
                return new Record("int", (Integer) r1.getValue() % (Integer) r2.getValue());
            case "==":
                // Esto puede fallar
                return switch (r1.getType().getFirst()) {
                    case "int" -> new Record("bool", ((Integer) r1.getValue()).equals((Integer) r2.getValue()));
                    // case "str" -> new Record("bool", r1.getValue().equals(r2.getValue()));
                    case "bool" -> new Record("bool", (boolean) r1.getValue() == (boolean) r2.getValue());
                    default -> new Record("bool", r1.getValue().equals(r2.getValue()));
                };
            case "!=":
                // Esto puede fallar
                return switch (r1.getType().getFirst()) {
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

    public Record func_eval(String funcName, List<ChocopyParser.ExprContext> expr_ctx){
        Record r;
        if ( isClass_() ){
            //IMPORTANTE: el ultimo valor del stack debe ser la instancia de la clase
            r = searchClassMember(funcName);
            if (r == null){
                System.err.println("El metodo " + funcName + " no ha sido declarado");
                System.exit(1);
            }
        }
        else{
            //IMPORTANTE: el ultimo valor del stack debe ser la instancia de la funcion anidada
            r = searchID(funcName);
            if (r == null){
                System.err.println("La funcion " + funcName + " no ha sido declarada");
                System.exit(1);
            }
        }

        if (!r.getType().getFirst().equals("func") && !r.getType().getFirst().equals("class")){
            System.err.println("La variable " + funcName + " no es una funcion ni una clase");
            System.exit(1);
        }
        if (r.getType().getFirst().equals("class")){
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
                symbolTable.put(".", new Record("class", null));
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
                    func_eval("__init__", expr_ctx);
                }
                callStack.pop();
                symbolTable = symbolTables.get(callStack.peek());
                if (!callStack.peek().equals("program")){
                    ctxClass = (ChocopyParser.Class_defContext) symbolTables.get("program").get(symbolTable.get("self").getType().getFirst()).getValue();
                }
            }while(!callStack.peek().equals("program"));

            return new Record(funcName, id);
        }else if (r.getType().getFirst().equals("func")){
            //Get the context of the function
            ChocopyParser.Func_defContext ctxFunc = (ChocopyParser.Func_defContext) r.getValue();

            // Set the scope to be inside of the function
            UUID id = UUID.randomUUID();
            callStack.push(id.toString());
            symbolTables.put(id.toString(), new Hashtable<>());
            symbolTable = symbolTables.get(callStack.peek());
            symbolTable.put(".", new Record("func", null));

            // Check if the parameters match
            if (isMethod() && ctxFunc.typed_var().size() < 1) {
                System.err.println("El parametro self es obligatorio");
                System.exit(1);
            }
            for (int i = 0, j = 0; i < ctxFunc.typed_var().size(); i++){
                // Inside of this function params must be declared in the symbol table
                try {

                    Record param = (Record) visitTyped_var(ctxFunc.typed_var(i));
                    if (isMethod() && param.getValue().equals("self")){
                        String aux = callStack.pop();
                        Record self = symbolTables.get(callStack.peek()).get("self");
                        callStack.push(aux);
                        if (! self.getType().getFirst().equals(param.getType().getFirst())){
                            System.err.println("El parametro "+ param.getValue() +" debe ser de tipo \""+ param.getType().getFirst() +"\" y se recibio \""+ self.getType().getFirst() +"\"");
                            System.exit(1);
                        }
                        symbolTable.put((String) param.getValue(), self);
                    }
                    else{
                        String aux = callStack.pop();
                        symbolTable = symbolTables.get(callStack.peek());
                        Record expr = (Record) visitExpr(expr_ctx.get(j));
                        callStack.push(aux);
                        symbolTable = symbolTables.get(callStack.peek());
                        //REVISAR
                        if (! expr.getType().getFirst().equals(param.getType().getFirst())){
                            System.err.println("El parametro "+ param.getValue() +" debe ser de tipo \""+ param.getType().getFirst() +"\" y se recibio \""+ expr.getType().getFirst() +"\"");
                            System.exit(1);
                        }
                        symbolTable.put((String) param.getValue(), expr);
                        j++;
                    }

                }catch (IndexOutOfBoundsException e){
                    System.err.println("El numero de parametros no coincide");
                    System.exit(1);
                }
            }

            Record func_body =  (Record) visitFunc_body(ctxFunc.func_body());

            if(func_body == null){
                callStack.pop();
                symbolTable = symbolTables.get(callStack.peek());
                return new Record("None", "None");
            }
            if (ctxFunc.type() != null){
                String type = (String) ((Record) visitType(ctxFunc.type())).getValue();
                if (!func_body.getType().getFirst().equals(type) && !func_body.getType().getFirst().equals("None")){
                    System.err.println("La funcion \""+funcName+"\" debe retornar el tipo \""+type+"\" y se encontro el tipo \""+func_body.getType().getFirst()+"\"");
                    System.exit(1);
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
        var st = symbolTables.get(r2.getValue());
        Record parent;
        Record self;
        do{
            parent = st.get("super");
            self = st.get("self");
            if(r1.getType().getFirst().equals(self.getType().getFirst())){
                return true;
            }
            st = symbolTables.get(parent.getValue());
        }while(!(parent.getValue()).equals("object"));
        return false;
    }

    //check if you're inside method in a class
    public boolean isMethod(){
        if (callStack.size()<2) return false;
        String aux = callStack.pop();
        Hashtable<String, Record> st = symbolTables.get(callStack.peek());
        if (st.get(".").getType().getFirst().equals("class")){
            callStack.push(aux);
            return true;
        }
        callStack.push(aux);
        return false;
    }

    //Checks if you've reached the class context
    public boolean isClass_(){
        return !callStack.isEmpty() && symbolTables.get(callStack.peek()).get(".").getType().getFirst().equals("class");
    }

    //Checks if you've reached the top-level context
    public boolean isProgram(){
        return !callStack.isEmpty() && symbolTables.get(callStack.peek()).get(".").getType().getFirst().equals("program");
    }

    public boolean isFunction(){
        return !callStack.isEmpty() && symbolTables.get(callStack.peek()).get(".").getType().getFirst().equals("func");
    }

    @Override
    public Record visitProgram(ChocopyParser.ProgramContext ctx) {

        if (!ctx.stmt().isEmpty()){
            for (int i = 0; i < ctx.stmt().size(); i++) {
                visitStmt(ctx.stmt(i));
            }
            return null;
        }
        if (ctx.class_def() != null){
            visitClass_def(ctx.class_def());
            return visitProgram(ctx.program());
        }
        if (ctx.var_def() != null){
            visitVar_def(ctx.var_def());
            return visitProgram(ctx.program());
        }
        if (ctx.func_def() != null){
            visitFunc_def(ctx.func_def());
            return visitProgram(ctx.program());
        }
        return null;
    }

    @Override
    public Record visitSimple_stmt(ChocopyParser.Simple_stmtContext ctx) {
        if(ctx.PASS() != null){
            //pass
            return null;
        }
        if(ctx.RETURN() != null){
            if(ctx.expr() != null){
                return visitExpr(ctx.expr());
            }
            return new Record("None", "None");
        }
        if(ctx.PRINT() != null){

            Record r = (Record) visitExpr(ctx.expr());
            //puede fallar
            if (r.getType().getFirst().equals("None")){
                System.err.println("No se puede imprimir una variable de tipo \"None\"");
                System.exit(1);
            }
            System.out.println(r.getValue());
            return null;
        }
        if(ctx.target() !=  null) {
            //(target IGUAL)+ expr
            Record r1 = (Record) visitExpr(ctx.expr());
            //itera sobre todos los target y dependiendo del tipo de "operacion" y cambia el valor anidado de la tabla de simbolos
            for (int i = 0; i < ctx.target().size(); i++) {
                Record temp = null;
                Record target = (Record) visitTarget(ctx.target(i));
                if(target.getTrace().isEmpty()) {
                    System.err.println("No se puede asignar un valor a un resultado anonimo");
                    System.exit(1);
                }
                boolean change = true;
                for (int j = 0; j < target.getTrace().size(); j++) {
                    //revisar esto
                    Tupla t = (Tupla) target.getTrace().get(j);
                    switch (t.x) {
                        case "id" -> temp = symbolTable.get(t.y);
                        case "index" -> {
                            assert temp != null;
                            if (temp.getType().getFirst().equals("str")) {
                                change = false;
                                temp.setValue(((String) temp.getValue()).replace(((String) temp.getValue()).charAt((Integer) t.y), ((String) r1.getValue()).charAt(0)));
                            }else{
                                temp = (Record) ((Object[]) temp.getValue())[(Integer) t.y];
                            }
                        }
                        case "member" -> {
                            assert temp != null;
                            temp = symbolTables.get(temp.getValue()).get(t.y);
                        }
                    }
                }
                assert temp != null;
                if(!temp.getType().getFirst().equals(r1.getType().getFirst()) && !r1.getType().getFirst().equals("None")){
                    if(!symbolTables.get("program").containsKey(temp.getType().getFirst()) || !symbolTables.get("program").containsKey(r1.getType().getFirst()) || !inherits(temp, r1)){
                        System.err.println("Los tipos de datos \"" + temp.getType().getFirst() + "\" y \"" + r1.getType().getFirst() + "\" no coinciden");
                        System.exit(1);
                    }
                }
                if(change) temp.setValue(r1.getValue());
            }
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
    public Record visitStmt(ChocopyParser.StmtContext ctx) {
        if(ctx.simple_stmt() != null){
            //simple_stmt NEWLINE
            return visitSimple_stmt(ctx.simple_stmt());
        }
        if(ctx.IF() != null){
            //IF expr DOS_PUNTOS block (ELIF expr DOS_PUNTOS block )* (ELSE DOS_PUNTOS block)?
            Record r = (Record) visitExpr(ctx.expr(0));
            if (!r.getType().getFirst().equals("bool")){
                System.err.println("La comparacion solo es valida entre booleanos, se recibio: \""+r.getType().getFirst()+"\"");
                System.exit(1);
            }
            if((boolean) r.getValue()){
                visitBlock(ctx.block(0));
                return null;
            }

            if(ctx.ELIF() != null){
                for (int i = 1; i < ctx.expr().size(); i++) {
                    r = (Record) visitExpr(ctx.expr(i));
                    if (!r.getType().getFirst().equals("bool")){
                        System.err.println("La comparacion solo es valida entre booleanos, se recibio: \""+r.getType().getFirst()+"\"");
                        System.exit(1);
                    }
                    if((boolean) r.getValue()){
                        visitBlock(ctx.block(i));
                        return null;
                    }
                }
            }
            if(ctx.ELSE() != null){
                visitBlock(ctx.block(ctx.expr().size()));
                return null;
            }
            return null;
        }
        if(ctx.WHILE() != null){
            //WHILE expr DOS_PUNTOS block
            Record r = (Record) visitExpr(ctx.expr(0));
            if (!r.getType().getFirst().equals("bool")){
                System.err.println("La comparacion solo es valida entre booleanos, se recibio: \""+r.getType().getFirst()+"\"");
                System.exit(1);
            }
            while((boolean) r.getValue()){
                visitBlock(ctx.block(0));
                r = (Record) visitExpr(ctx.expr(0));
            }
            return null;
        }
        if(ctx.FOR() != null){
            //FOR ID IN expr DOS_PUNTOS block;
            Record r = (Record) visitExpr(ctx.expr(0));
            if (!r.getType().getFirst().equals("list") && !r.getType().getFirst().equals("str")){
                System.err.println("Solo es posible iterar sobre listas o strings, se recibio: \""+r.getType().getFirst()+"\"");
                System.exit(1);
            }
            if(!symbolTable.containsKey(ctx.ID().getText())) {
                System.err.println("La variable " + ctx.ID().getText() + " ya fue declarada");
                System.exit(1);
            }
            if(r.getType().getFirst().equals("str")) {
                String values = (String) r.getValue();
                for (int i = 0; i < values.length(); i++) {
                    Record id = symbolTable.get(ctx.ID().getText());
                    char value =  values.charAt(i);
                    id.setValue(value);
                    visitBlock(ctx.block(0));
                }
            }
            else {
                Object[] values = (Object[]) r.getValue();
                for (Object o : values) {
                    Record id = symbolTable.get(ctx.ID().getText());
                    Record value = (Record) o;
                    id.setValue(value.getValue());
                    visitBlock(ctx.block(0));
                }
            }
            return null;
        }
        return null;
    }

    @Override
    public Record visitBlock(ChocopyParser.BlockContext ctx) {
        for (int i = 0; i < ctx.stmt().size(); i++) {
            visitStmt(ctx.stmt(i));
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
            Record r = (Record) visitType(ctx.type());
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
    public Record visitExpr(ChocopyParser.ExprContext ctx) {
        if (ctx.IF() !=  null){
            // EXPR_OR IF EXPR_OR ELSE EXPR
            Record r1 = (Record) visitExpr_or(ctx.expr_or(0));
            Record r2 = (Record) visitExpr_or(ctx.expr_or(1));
            Record r3 = (Record) visitExpr(ctx.expr());
            if (!r2.getType().getFirst().equals("bool")){
                System.err.println("La condicion debe ser de tipo booleano, se recibio: \""+r2.getType().getFirst()+"\"");
                System.exit(1);
            }
            if ((boolean) r2.getValue())
                return r1;
            else
                return r3;
        }
        return visitExpr_or(ctx.expr_or(0));
    }

    @Override
    public Record visitExpr_or(ChocopyParser.Expr_orContext ctx) {
        if (ctx.OR() !=  null){
            // expr_and OR simple_expr
            Record r1 = (Record) visitExpr_or(ctx.expr_or());
            Record r2 = (Record) visitExpr_and(ctx.expr_and());
            if (!r1.getType().getFirst().equals("bool") || !r2.getType().getFirst().equals("bool")){
                System.err.println("La operacion or solo es valida en booleanos, se recibio: \""+r1.getType().getFirst()+"\", \""+r2.getType().getFirst()+"\"");
                System.exit(1);
            }
            return new Record("bool", (boolean) r1.getValue() || (boolean) r2.getValue());
        }
        return visitExpr_and(ctx.expr_and());
    }

    @Override
    public Record visitExpr_and(ChocopyParser.Expr_andContext ctx) {
        if (ctx.AND() !=  null){
            // expr_and AND simple_expr
            Record r1 = (Record) visitExpr_and(ctx.expr_and());
            Record r2 = (Record) visitSimple_expr(ctx.simple_expr());
            if (!r1.getType().getFirst().equals("bool") || !r2.getType().getFirst().equals("bool")){
                System.err.println("La operacion and solo es valida en booleanos, se recibio: \""+r1.getType().getFirst()+"\", \""+r2.getType().getFirst()+"\"");
                System.exit(1);
            }
            return new Record("bool", (boolean) r1.getValue() && (boolean) r2.getValue());
        }
        return visitSimple_expr(ctx.simple_expr());
    }

    @Override
    public Record visitCexpr(ChocopyParser.CexprContext ctx) {
        if (ctx.cexpr() != null){
            //cexpr_sum op_suma cexpr_mul
            Record r1 = (Record) visitCexpr(ctx.cexpr());
            Record r2 = (Record) visitCexpr_sum(ctx.cexpr_sum());
            String op = (String) visitCmp(ctx.cmp()).getValue();

            return evalOp(r1,r2,op);
        }
        //cexpr_mul
        return visitCexpr_sum(ctx.cexpr_sum());
    }

    @Override
    public Record visitCexpr_sum(ChocopyParser.Cexpr_sumContext ctx) {
        if (ctx.cexpr_sum() != null){
            //cexpr_sum op_suma cexpr_mul
            Record r1 = (Record) visitCexpr_sum(ctx.cexpr_sum());
            Record r2 = (Record) visitCexpr_mul(ctx.cexpr_mul());
            String op = (String) visitOp_suma(ctx.op_suma()).getValue();

            return evalOp(r1,r2,op);
        }
        //cexpr_mul
        return visitCexpr_mul(ctx.cexpr_mul());
    }

    @Override
    public Record visitCexpr_mul(ChocopyParser.Cexpr_mulContext ctx) {
        if (ctx.cexpr_mul() != null){
            //cexpr_mul op_mul simple_value
            Record r1 = (Record) visitCexpr_mul(ctx.cexpr_mul());
            Record r2 = (Record) visitSimple_value(ctx.simple_value());
            String op = (String) visitOp_mul(ctx.op_mul()).getValue();
            return evalOp(r1,r2,op);
        }
        //simple_value
        return visitSimple_value(ctx.simple_value());
    }

    @Override
    public Record visitTarget(ChocopyParser.TargetContext ctx) {
        if(ctx.PUNTO() != null){
            Record r = (Record) visitSimple_value(ctx.simple_value());

            if (!symbolTables.get("program").containsKey(r.getType().getFirst())){
                System.err.println("No se encontro el tipo de dato \"" + r.getType().getFirst() + "\"");
                System.exit(1);
            }

            if (!symbolTables.get("program").get(r.getType().getFirst()).getType().getFirst().equals("class")) {
                System.err.println("La expresion \"" + ctx.simple_value().getText() + "\" no retorna una clase");
                System.exit(1);
            }

            callStack.push((String) r.getValue());
            symbolTable = symbolTables.get(r.getValue());

            // SIMPLE_VALUE . ID

            Record r2 = searchClassMember(ctx.ID().getText());
            if (r2 == null){
                System.err.println("El atributo " + ctx.ID().getText() + " no ha sido declarado");
                System.exit(1);
            }
            callStack.pop();
            symbolTable = symbolTables.get(callStack.peek());
            r2.setTrace(r.getTrace());
            r2.addTrace(new Tupla("member", ctx.ID().getText()));
            return r2;
        }
        if(ctx.ID() !=  null){
            Record r;
            String varName = ctx.ID().getText();
            if ( isClass_() ){
                r = searchClassMember(varName);
                if (r == null){
                    System.err.println("El atributo " + varName + " no ha sido declarado");
                    System.exit(1);
                }
            }
            else{
                r = searchID(varName);
                if (r == null){
                    System.err.println("La variable " + varName + " no ha sido declarada");
                    System.exit(1);
                }
            }
            if (r.getType().getFirst().equals("func") || r.getType().getFirst().equals("class")){
                System.err.println(varName +" no una variable");
                System.exit(1);
            }
            var trace = new ArrayList<>();
            trace.add(new Tupla("id",varName));
            r.setTrace(trace);
            return r;
        }
        if(ctx.COR_IZQ() != null){
            Record r = (Record) visitSimple_value(ctx.simple_value());
            Record i = (Record) visitExpr(ctx.expr());
            if (!r.getType().getFirst().equals("list") && !r.getType().getFirst().equals("str")){
                System.err.println("La operacion [] no esta permitida para tipos de dato diferentes a \"str\", \"list\"");
                System.exit(1);
            }
            else if (!i.getType().getFirst().equals("int")){
                System.err.println("El index debe ser de tipo \"int\"");
                System.exit(1);
            }
            else if (r.getValue().equals("None")){
                System.err.println("El índice no se encuentra en el arreglo");
                System.exit(1);
            }
            int len = 0;
            if (r.getType().getFirst().equals("list"))
                len = ((Object[]) r.getValue()).length;
            else if (r.getType().getFirst().equals("str"))
                len = ((String) r.getValue()).length();

            if ((int) i.getValue() >= len){
                System.err.println("El index no se encuentra en el arreglo");
                System.exit(1);
            }
            r.addTrace(new Tupla("index", i.getValue()));
            if (r.getType().getFirst().equals("list")){
                var r1 = (Record)((Object[]) r.getValue())[(Integer) i.getValue()];
                r1.setTrace(r.getTrace());
                return r1;
            }
            if (r.getType().getFirst().equals("str")){
                var r1 = new Record("str", ((String) r.getValue()).charAt((Integer) i.getValue()));
                r1.setTrace(r.getTrace());
                return r1;
            }
        }
        return null;
    }

    @Override
    public Record visitSimple_expr(ChocopyParser.Simple_exprContext ctx) {
        if (ctx.cexpr() != null){
            //cexpr
            return visitCexpr(ctx.cexpr());
        }
        if (ctx.NOT() !=  null){
            //NOT EXPR
            Record r = (Record) visitSimple_expr(ctx.simple_expr());
            if (!r.getType().getFirst().equals("bool")){
                System.err.println("La operacion not solo es valida en booleanos, se recibio: \""+r.getType().getFirst()+"\"");
                System.exit(1);
            }
            r.setValue(!(boolean)r.getValue());
            return r;
        }
        return null;
    }

    @Override
    public Record visitSimple_value(ChocopyParser.Simple_valueContext ctx) {

        if (ctx.ID() !=  null){

            if (ctx.PUNTO() !=  null){

                Record r = (Record) visitSimple_value(ctx.simple_value());

                if (!symbolTables.get("program").containsKey(r.getType().getFirst())){
                    System.err.println("No se encontro el tipo de dato \"" + r.getType().getFirst() + "\"");
                    System.exit(1);
                }

                if (!symbolTables.get("program").get(r.getType().getFirst()).getType().getFirst().equals("class")) {
                    System.err.println("La expresion \"" + ctx.simple_value().getText() + "\" no retorna una clase");
                    System.exit(1);
                }
                callStack.push((String) r.getValue());
                symbolTable = symbolTables.get(r.getValue());

                if (ctx.PAR_IZQ() !=  null){
                    // SIMPLE_VALUE . ID ( EXPR ... )
                    Record result = (Record) func_eval(ctx.ID().getText(), ctx.expr());
                    callStack.pop();
                    symbolTable = symbolTables.get(callStack.peek());
                    return result;
                }

                // SIMPLE_VALUE . ID

                Record r2 = searchClassMember(ctx.ID().getText());
                if (r2 == null){
                    System.err.println("El atributo " + ctx.ID().getText() + " no ha sido declarado");
                    System.exit(1);
                }
                callStack.pop();
                symbolTable = symbolTables.get(callStack.peek());
                r2.setTrace(r.getTrace());
                r2.addTrace(new Tupla("member", ctx.ID().getText()));
                return r2;
            }

            if (ctx.PAR_IZQ() !=  null){
                // ID ( EXPR ... )
                return func_eval(ctx.ID().getText(), ctx.expr());
            }

            // ID
            Record r;
            String varName = ctx.ID().getText();
            if ( isClass_() ){
                r = searchClassMember(varName);
                if (r == null){
                    System.err.println("El atributo " + varName + " no ha sido declarado");
                    System.exit(1);
                }
            }
            else{
                r = searchID(varName);
                if (r == null){
                    System.err.println("La variable " + varName + " no ha sido declarada");
                    System.exit(1);
                }
            }
            if (r.getType().getFirst().equals("func") || r.getType().getFirst().equals("class")){
                System.err.println(varName +" no una variable");
                System.exit(1);
            }

            var trace = new ArrayList<>();
            trace.add(new Tupla("id",varName));
            r.setTrace(trace);
            return r;
        }

        if (ctx.COR_IZQ() != null){

            if (ctx.simple_value() !=  null){
                // SIMPLE_VALUE [ EXPR ]
                Record cexpr = (Record) visitSimple_value(ctx.simple_value());
                if (cexpr.getValue().equals("None")){
                    System.err.println("La operacion [] no esta permitida para tipos de dato \"None\"");
                    System.exit(1);
                }
                if (!cexpr.getType().getFirst().equals("list") && !cexpr.getType().getFirst().equals("str")){
                    System.err.println("La operacion [] no esta permitida para tipos de dato diferentes a \"str\", \"list\"");
                    System.exit(1);
                }
                Record expr = (Record) visitExpr(ctx.expr(0));
                if (!expr.getType().getFirst().equals("int")){
                    System.err.println("El index debe ser de tipo \"int\"");
                    System.exit(1);
                }
                Integer len = 0;
                if (cexpr.getType().getFirst().equals("list"))
                    len = ((Object[]) cexpr.getValue()).length;
                else if (cexpr.getType().getFirst().equals("str"))
                    len = ((String) cexpr.getValue()).length();
                if ((int) expr.getValue() < 0 || (Integer) expr.getValue() >= len){
                    System.err.println("El index no se encuentra en el arreglo");
                    if (cexpr.getValue().equals("None")){
                        System.err.println("El index no se encuentra en el arreglo");
                        System.exit(1);
                    }
                    System.exit(1);
                }
                cexpr.addTrace(new Tupla("index", expr.getValue()));
                if (cexpr.getType().getFirst().equals("list")){
                    var r = (Record)((Object[]) cexpr.getValue())[(Integer) expr.getValue()];
                    r.setTrace(cexpr.getTrace());
                    return r;
                }
                if (cexpr.getType().getFirst().equals("str")){
                    var r = new Record("str", ((String) cexpr.getValue()).charAt((Integer) expr.getValue()));
                    r.setTrace(cexpr.getTrace());
                    return r;
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
            Record r = (Record) visitExpr(ctx.expr(0));
            if (!r.getType().getFirst().equals("str") && !r.getType().getFirst().equals("list")){
                System.err.println("La expresion debe ser de tipo \"lista\" o \"str\", se recibio: \""+r.getType().getFirst()+"\"");
                System.exit(1);
            }
            if (r.getValue().equals("None")){
                System.err.println("La expresion debe ser de tipo \"lista\" o \"str\", se recibio: \""+r.getValue()+"\"");
                System.exit(1);
            }
            if (r.getType().getFirst().equals("str"))
                return new Record("int", ((String)r.getValue()).length());
            else
                return new Record("int", ((Object[])r.getValue()).length);
        }

        if (ctx.INPUT() !=  null){
            // INPUT ( )
            Scanner s = new Scanner(System.in);
            String text = s.nextLine();
            return new Record("str", text);
        }

        if (ctx.MENOS() != null){
            // - CEXPR
            Record cexpr = (Record) visitSimple_value(ctx.simple_value());
            if (!cexpr.getType().getFirst().equals("int")){
                System.err.println("La operacion - no esta permitida para tipos de dato diferentes a \"int\"");
                System.exit(1);
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

    @Override
    public Record visitFunc_body(ChocopyParser.Func_bodyContext ctx) {
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
                Record r = (Record) visitStmt(ctx.stmt(i));
                if (r != null) {
                    return r;
                }
            }
            return null;
        }
        return null;
    }

    @Override
    public Record visitFunc_def(ChocopyParser.Func_defContext ctx) {
        String funcName = ctx.ID().getText();

        if ( isClass_() ){
            if (searchClassMember(funcName) != null  && !funcName.equals("__init__")){
                System.err.println("El metodo " + funcName + " ya fue declarado");
                System.exit(1);
            }
        }
        else{
            if (searchID(funcName) != null){
                System.err.println("La funcion " + funcName + " ya fue declarada");
                System.exit(1);
            }
        }

        Record func = new Record("func", ctx);
        symbolTable.put(funcName, func);
        return  null;
    }

    @Override
    public Record visitVar_def(ChocopyParser.Var_defContext ctx){
        String varName = ctx.typed_var().ID().getText();
        if ( isClass_() ){
            if (searchClassMember(varName) != null){
                System.err.println("El atributo " + varName + " ya fue declarado");
                System.exit(1);
            }
        }
        else{
            if (searchID(varName) != null){
                System.err.println("La variable " + varName + " ya fue declarada");
                System.exit(1);
            }
        }

        Record literal = (Record)visitLiteral(ctx.literal());
        Record type = (Record) visitType(ctx.typed_var().type());

        if (!literal.getType().getFirst().equals(type.getValue()) && !literal.getValue().equals("None")){
            System.err.println("No se puede asignar un " + type.getValue() + " a una variable " + literal.getType().getFirst());
            System.exit(1);
        }

        Record var = new Record((String) type.getValue(), literal.getValue());
        symbolTable.put(varName, var);
        return  null;
    }

    @Override
    public Record visitClass_body(ChocopyParser.Class_bodyContext ctx) {
        if (ctx.PASS() != null){
            return null;
        }
        if (ctx.class_body_def() != null){
            return visitClass_body_def(ctx.class_body_def());
        }
        return null;
    }

    @Override
    public Record visitClass_body_def(ChocopyParser.Class_body_defContext ctx) {
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
    public Record visitClass_def(ChocopyParser.Class_defContext ctx) {
        String className = ctx.ID(0).getText();
        String parentName = ctx.ID(1).getText();

        if (symbolTable.containsKey(className)){
            System.err.println("La clase " + className + " ya fue definida");
            System.exit(1);
        }
        if (!parentName.equals("object") && !symbolTable.containsKey(parentName)){
            System.err.println("La clase " + parentName + " no ha sido definida");
            System.exit(1);
        }
        Record new_class = new Record("class", ctx);
        symbolTable.put(className, new_class);
        return  null;
    }

    @Override
    public Record visitGlobal_decl(ChocopyParser.Global_declContext ctx) {
        Hashtable<String, Record> st = symbolTables.get("program");

        //search for id
        if (!st.containsKey(ctx.ID().getText())) {
            System.err.println("La variable " + ctx.ID().getText() + " no ha sido declarada");
            System.exit(1);
            return null;
        }
        symbolTable.put(ctx.ID().getText(),st.get(ctx.ID().getText()));
        return st.get(ctx.ID().getText());
    }

    @Override
    public Record visitNonlocal_decl(ChocopyParser.Nonlocal_declContext ctx) {
        String aux = callStack.pop();
        Hashtable<String, Record> st = symbolTables.get(callStack.peek());

        //search for id
        if (!st.containsKey(ctx.ID().getText())) {
            System.err.println("La variable " + ctx.ID().getText() + " no ha sido declarada");
            System.exit(1);
        }
        symbolTable.put(ctx.ID().getText(),st.get(ctx.ID().getText()));
        callStack.push(aux);
        return st.get(ctx.ID().getText());
    }
}

