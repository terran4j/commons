package com.terran4j.test.hi.api2doc;

public class MultiplyObject {

    private long a;

    private long b;

    private long result;

    public MultiplyObject(long a, long b) {
        this.a = a;
        this.b = b;
        result = a * b;
    }

    public long getA() {
        return a;
    }

    public void setA(long a) {
        this.a = a;
    }

    public long getB() {
        return b;
    }

    public void setB(long b) {
        this.b = b;
    }

    public long getResult() {
        return result;
    }

    public void setResult(long result) {
        this.result = result;
    }

}
