package com.example.owner.winez.Utils;

/**
 * Created by Ziv on 08/02/2017.
 */

/**
 * Hold a pair of items
 * @param <T>
 * @param <X>
 */
public class Tuple<T,X> {
    private X x;
    private T t;

    public Tuple(T t, X x){
        this.t = t;
        this.x = x;
    }

    public X getX(){
        return this.x;
    }

    public T  getT(){
        return this.t;
    }
}
