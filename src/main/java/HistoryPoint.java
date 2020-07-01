import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Stack;

public class HistoryPoint {

    Stack<String> callStack;
    Hashtable<String, Record> symbolTable;
    ArrayList<String> outputs;
    int lineNumber;

    public HistoryPoint(Stack<String> callStack, Hashtable<String, Record> symbolTable, ArrayList<String> outputs, int lineNumber) {
        this.callStack = new Stack<>();
        callStack.forEach(s -> {
            this.callStack.push(s);
        });

        this.symbolTable = new Hashtable<>();
        symbolTable.forEach((s, record) -> {
            Record r = new Record(record);
            if((r.getType().equals("func") || r.getType().equals("class")) && !s.equals(".")){
                r.setValue(null);
            }
            this.symbolTable.put(s, new Record(r));
        });

        this.outputs = new ArrayList<>();
        this.outputs.addAll(outputs);

        this.lineNumber = lineNumber;
    }
}
