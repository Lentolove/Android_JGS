package com.tsp.neibulei;

/**
 * author : shengping.tian
 * time   : 2022/03/09
 * desc   :
 * version: 1.0
 */
public class Outer {

    private int a = 0;

    public class InnerClass{

        public void innerMethod(){
            a = 1;
        }
    }
}

