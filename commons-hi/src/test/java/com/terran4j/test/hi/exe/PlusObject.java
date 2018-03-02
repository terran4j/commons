package com.terran4j.test.hi.exe;

public class PlusObject {

    private long a;

    private long b;

    private long sum;

    public PlusObject(long a, long b) {
        this.a = a;
        this.b = b;
        sum = a + b;
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

    public long getSum() {
        return sum;
    }

    public void setSum(long sum) {
        this.sum = sum;
    }

}
