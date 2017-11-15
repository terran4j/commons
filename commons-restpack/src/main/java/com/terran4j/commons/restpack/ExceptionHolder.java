package com.terran4j.commons.restpack;

public class ExceptionHolder {

    private static final ThreadLocal<Exception> localException = new ThreadLocal<>();

    public static final void set(Exception e) {
        localException.set(e);
    }

    public static final Exception get() {
        return localException.get();
    }

    public static final void remove() {
        localException.remove();
    }

}
