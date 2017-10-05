package com.lilingkun.core;

/**
 * Created by KunKun on 2017/10/5.
 */
public class Calculator {
    public static int getLastBit(int[] arg) {
        int mod = 0;
        int wights[]= new int[]{ 7,9,10,5,8,4 ,2,1,6,3,7,9,10,5,8,4,2};
        for(int i =0; i < 17 ;++i)
            mod += arg[i]*wights[i];//乘相应系数求和

        mod = mod%11; //对11求余

        int value[]= new int[]{1,0,10,9,8,7,6,5,4,3,2};
        return value[mod];
    }
}
