package main.mylang.var_table;

import java.util.HashMap;
import java.util.Map;

public class VarTable {
    public String value;

//    private class VarData {
//        public String value;
//
//        public VarData(String type, String value) {
//            this.value = value;
//        }
//
//        public String toString() {
//            return value;
//        }
//    }

    private HashMap<String, String> hashMap;

    public VarTable() {
        hashMap = new HashMap<>();
    }

    public void add(String var, String value) {
        hashMap.put(var,  value);
    }

//    public void add(String var, String type, String value) {
//        hashMap.put(var, value);
//    }

//    public String getType(String var) {
//        return hashMap.get(var).type;
//    }

    public String getValue(String var) {
        return hashMap.get(var);
    }

//    public void setType(String var, String type) {
//        hashMap.get(var).type = type;
//    }

    public void setNewValue(String var, String value) {
        hashMap.replace(var, value);

    }

    public String toString() {
        StringBuilder builder = new StringBuilder("{");
        boolean first = true;

        for (Map.Entry<String, String> entry : hashMap.entrySet()) {
            if (!first) {
                builder.append(", ");
            }
            builder.append("[" + entry.getKey() + " : " + entry.getValue() + "]");
            first = false;
        }
        builder.append("}");

        return builder.toString();
    }

}