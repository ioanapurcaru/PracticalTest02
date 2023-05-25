package ro.pub.cs.systems.eim.practicaltest02;

public class DataModel {
    String value;
    String key;

    public DataModel(String value, String minute) {
        this.value = value;
        this.key = key;
    }

    @Override
    public String toString() {
        return "value: " + value + "  KEY " + key;
    }
}
