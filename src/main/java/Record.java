public class Record{

    public String type;
    public Object value;

    public Record(String type, Object value) {
        this.type = type;
        this.value = value;
    }

    public Record(Record r) {
        this.type = r.getType();
        this.value = r.getValue();
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

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        if(this.type.equals("list")){
            Object[] r = (Object[]) this.value;
            s.append("[");
            for (Object record : r) {
                s.append(record.toString());
                s.append(", ");
            }
            s.delete(s.length()-2,s.length());
            s.append("]");
            return s.toString();
        }
        return this.value.toString();
    }
}
