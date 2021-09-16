package com.tsp.android.hilibrary.log;

public interface HiLogFormatter<T> {

    String format(T data);
}