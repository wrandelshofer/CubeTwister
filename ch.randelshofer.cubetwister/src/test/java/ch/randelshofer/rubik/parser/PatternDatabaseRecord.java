package ch.randelshofer.rubik.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Javascript Array with String key and numeric indices.
 */
class PatternDatabaseRecord implements Cloneable, Iterable<String> {
    String[] data;
    List<String> keys;

    public PatternDatabaseRecord(String[] data, List<String> keys) {
        this.keys = keys;
        if (data.length < keys.size()) {
            this.data = new String[keys.size()];
            System.arraycopy(data, 0, this.data, 0, data.length);
        } else {
            this.data = data;
        }
        if (this.data.length != keys.size()) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public Iterator<String> iterator() {
        return Arrays.asList(data).iterator();
    }

    public int length() {
        return data.length;
    }

    public String get(int index) {
        return data[index];
    }

    public boolean hasKey(String key) {
        return keys.contains(key);
    }

    public String get(String key) {
        int index = keys.indexOf(key);
        return index == -1 ? null : data[index];
    }

    public void set(int index, String value) {
        data[index] = value;
    }

    public void set(String key, String value) {
        ;
        int index = ensureCapacity(key);
        data[index] = value;
    }

    private int ensureCapacity(String key) {
        int index = keys.indexOf(key);
        if (index == -1) {
            String[] tmp = data;
            data = new String[data.length + 1];
            System.arraycopy(tmp, 0, data, 0, tmp.length);
            keys = new ArrayList<>(keys);
            keys.add(key);
            return keys.size() - 1;
        }
        return index;
    }


    @Override
    public PatternDatabaseRecord clone() {
        try {
            PatternDatabaseRecord that = (PatternDatabaseRecord) super.clone();
            that.data = this.data.clone();
            return that;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PatternDatabaseRecord)) {
            return false;
        }
        PatternDatabaseRecord that = (PatternDatabaseRecord) o;
        return Arrays.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    static PatternDatabaseRecord createDataRecord(String line, List<String> headers) {
        String[] data = line.replace('\u000b', '\n').split("\t");
        return new PatternDatabaseRecord(data, headers);
    }
}
