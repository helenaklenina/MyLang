package main.mylang.var_table;

import main.mylang.token.Token;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class VarTable {
    private HashMap<String, Integer> hashMap;
    private static VarTable instance;

    public VarTable() {

        hashMap = new HashMap<>();
    }

    public static VarTable getInstance() {
        if (instance == null) {
            instance = new VarTable();
        }

        return instance;
    }

    public void add(String var, Integer value) {
        hashMap.put(var,  value);
    }

//    public void setValue(String transitionVarName, int transitionVarValue) {
//    }

    public int getValue(String  var) throws NullPointerException{
        return hashMap.get(var);
    }

    public void setNewValue(String var, Integer value) {
        hashMap.replace(var, value);
    }


    public String toString() {
        StringBuilder builder = new StringBuilder("{");
        boolean first = true;

        for (Map.Entry<String, Integer> entry : hashMap.entrySet()) {
            if (!first) {
                builder.append(", ");
            }
            builder.append("[" + entry.getKey() + " : " + entry.getValue() + "]");
            first = false;
        }
        builder.append("}");

        return builder.toString();
    }

    public void remove(String var) {
        hashMap.remove(var);
    }

    public Set<String> keySet() {
        return hashMap.keySet();
    }

    public Map<String, Integer> getData() {
        return hashMap;
    }

    public void setData(Map<String, Integer> data) {
        hashMap = new HashMap<>(data);
    }

    public void clear() {
        hashMap.clear();
    }

}