package com.fudan.sw.dsa.project2.bean;

import java.util.ArrayList;

class Vertex {
    private Address address;
    private ArrayList<Edge> edges = new ArrayList<>();
    private ArrayList<Vertex> vertices = new ArrayList<>();
    private double distance;
    private Vertex preVertex;
    private String toLine = ""; //到达当前站的线路
    private String line = "";
    Vertex(String address,String longitude, String latitude){
        this.address = new Address(address,longitude,latitude);
    }
    Vertex(){}
    Vertex(Address address){
        this.address = address;
    }

    public int getChangeTimes(){
        return address.getChangeTimes();
    }

    public void setChangeTimes(int changeTimes){
        address.setChangeTimes(changeTimes);
    }

    void AddEdge(Vertex vertex1,String line,double weight){  //加入一边
        Edge edge = new Edge(this,vertex1,line,weight);
        edges.add(edge);
    }


    String getName(){
        return address.getAddress();
    }

    ArrayList<Edge> getEdges(){
        return edges;
    }

    void setDistance(double distance){
        this.distance = distance;
    }

    double getDistance(){
        return distance;
    }

    Edge getEdge(Vertex vertex1){
        for(int i = edges.size() - 1; i >= 0; i--){
            if(edges.get(i).getOtherVertex(this) == vertex1){
                return edges.get(i);
            }
        }
        return null;//一般不可到达，到达即为错误
    }

    void setPreVertex(Vertex vertex){ //设置前驱点
        this.preVertex = vertex;
    }

    Vertex getPreVertex(){  //获得前驱点
        return preVertex;
    }

    void addVertex(Vertex vertex){
        vertices.add(vertex);
    }

    ArrayList<Vertex> getVertices(){
        return vertices;
    }

    Address getAddress(){
        return address;
    }

    int getChangeOrNot(Vertex vertex){
        if(toLine.equals(this.getEdge(vertex).getLine())){
            return 0;
        }
        return 1;
    }

    void setToLine(String toLine){
        this.toLine = toLine;
    }

    void setLine(String line){
        if(this.line.equals("")){
            this.line = line;
        }
        else if(!this.line.contains(line)){
            this.line = this.line + ":" + line;
        }
    }

    String getLine(){
        return line;
    }

    void clearToLine(){
        toLine = "";
    }

    String getToLine(){
        return toLine;
    }

    boolean sameLine(Vertex vertex){
        String[] lines1 = vertex.getLine().split(":");
        String[] lines2 = line.split(":");
        for(String lin1:lines1)
            for (String lin2:lines2){
                if(lin1.equals(lin2))
                    return true;
            }
        return false;
    }

    void removeLine(String line){
        this.line.replace(line,"");
    }
}
