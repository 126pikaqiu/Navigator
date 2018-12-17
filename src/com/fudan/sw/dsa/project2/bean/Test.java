package com.fudan.sw.dsa.project2.bean;

import com.fudan.sw.dsa.project2.constant.FileGetter;

import java.util.ArrayList;

public class Test {
    public static void main(String[] args){
        FileGetter fileGetter = new FileGetter();
        Navigator navigator = new Navigator();
        navigator.loadMap(fileGetter.readFileFromClasspath());
        Address address5 = new Address("五角场","121.521178","31.303797");
        Address address6 = new Address("张江高科","121.593923","31.207892");
        ArrayList<Address> addresses2 = navigator.getPath_OF_MINCHANGETIMES(address6,address5);
        for(Address address:addresses2){
            System.out.print(address.getAddress() + "--");
        }
    }
}
