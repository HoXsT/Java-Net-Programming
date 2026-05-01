package server;

import interfaces.Result;

import java.io.Serializable;

public class ResultImpl implements Result, Serializable {
    private static final long serialVersionUID = 1L;
    private Object output;
    private double scoreTime;

    public ResultImpl(Object o, double c) {
        output = o;
        scoreTime = c;
    }

    @Override
    public Object output() {
        return output;
    }

    @Override
    public double scoreTime() {
        return scoreTime;
    }
}