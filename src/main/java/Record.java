public class Record{

    public String type;
    public Object value;

    public Record(String type, Object value) {
        this.type = type;
        this.value = value;
    }

    public Record(Record r) {
        this.type = r.getType();
        if(r.getType().equals("list") && !r.getValue().equals("None")){
            this.value = copyList((Object[])r.getValue());
        }else{
            this.value = r.getValue();
        }
    }

    private Record[] copyList(Object[] r){
        Record[] new_list = new Record[r.length];
        for (int i = 0; i < r.length; i++) {
            Record aux = (Record) r[i];
            if(aux.getType().equals("list")){
                new_list[i] = new Record("list", copyList((Object[]) aux.getValue()));
            }else {
                new_list[i] = new Record(aux);
            }
        }
        return new_list;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
        if(this.type.equals("list") && !this.value.equals("None")){
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
