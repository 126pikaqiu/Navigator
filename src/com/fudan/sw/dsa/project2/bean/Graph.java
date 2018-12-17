package com.fudan.sw.dsa.project2.bean;

import java.util.ArrayList;

class Graph {   //一个图,老实说没有用到过，测试的时候可能有用
    Graph(){}
    private ArrayList<Edge> edges = new ArrayList<>();
    private ArrayList<Vertex> vertices;
    Graph(ArrayList<Vertex> vertices){
        this.vertices = vertices;
        for(Vertex vertex:vertices){
            for(Edge edge:vertex.getEdges()){
                if(!edges.contains(edge)){
                    edges.add(edge);
                }
            }
        }
    }

    ArrayList<Vertex> getVertices(){
        return vertices;
    }

    ArrayList<Edge> getEdges(){
        return edges;
    }
}
