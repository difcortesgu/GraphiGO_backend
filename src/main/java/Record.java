import java.util.ArrayList;

public class Record {

    public String type;
    public Object value;
    private ArrayList<Object> trace;


    public Record(String type, Object value) {
        this.type = type;
        this.value = value;
        this.trace = new ArrayList<>();
    }

    public Record(Record r) {
        this.type = r.getType();
        this.value = r.getValue();
        this.trace = new ArrayList<>();
    }

    public String getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public ArrayList<Object> getTrace() {
        return trace;
    }

    public void setTrace(ArrayList<Object> trace) {
        this.trace = trace;
    }

    public void addTrace(Tupla member) {
        this.trace.add(member);
    }
}
