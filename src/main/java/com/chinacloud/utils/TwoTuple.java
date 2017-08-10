package com.chinacloud.utils;

/**
 * Created by Administrator on 2017/8/3.
 */
public class TwoTuple<A,B>{
    public final A first;
    public final B second;
    public TwoTuple(A a ,B b){first = a; second=b;}
    public String toString(){
        return "("+first+","+second+")";
    }
}
