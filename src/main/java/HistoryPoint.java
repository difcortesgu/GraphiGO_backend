import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Stack;

public class HistoryPoint {

    Stack<String> callStack;
    Hashtable<String, Hashtable<String, Record>> symbolTables;
    ArrayList<String> outputs;
    int lineNumber;

    public HistoryPoint(Stack<String> callStack, Hashtable<String, Hashtable<String, Record>> symbolTables, ArrayList<String> outputs, int lineNumber) {
        this.callStack = new Stack<>();
        callStack.forEach(s -> {
            this.callStack.push(s);
        });

        this.symbolTables = new Hashtable<>();
        symbolTables.forEach((s, stringRecordHashtable) -> {
            Hashtable<String, Record> hs = new Hashtable<>();
            this.symbolTables.put(s, hs);
            stringRecordHashtable.forEach((s1, record) -> {
                hs.put(s1, new Record(record));
            });
        });

        this.outputs = new ArrayList<>();
        this.outputs.addAll(outputs);
        
        this.lineNumber = lineNumber;
    }
}
