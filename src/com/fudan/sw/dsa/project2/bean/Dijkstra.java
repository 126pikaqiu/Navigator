package com.fudan.sw.dsa.project2.bean;

import java.util.ArrayList;
import java.util.Stack;

class Dijkstra {
    private double minutes;
    private void DIJKSTRA(ArrayList<Vertex> vertices, Vertex start, Vertex end){ //某个算法
        initialize_single_source(vertices,start);
        MinHeap minHeap = new MinHeap(vertices);
        while (!minHeap.isEmpty()){
            Vertex u = minHeap.extract_min();
            if(u == end ){   //找到了,结束
                minutes = u.getDistance();
                break;
            }
            for(Vertex vertex:u.getVertices()){
                relex(u,vertex,minHeap);
            }
        }
    }

    private void initialize_single_source(ArrayList<Vertex> graph, Vertex start){ //初始化所有点的距离
        for(Vertex vertex:graph){
            vertex.setDistance(Double.MAX_VALUE);
        }
        start.setDistance(0);
    }

    private void initialize_single_source1(ArrayList<Vertex> graph, Vertex start){ //初始化所有点的距离
        for(Vertex vertex:graph){
            vertex.setChangeTimes(17);
            vertex.setToLine("");
            vertex.setDistance(Double.MAX_VALUE);
            vertex.clearToLine();
        }
        start.setChangeTimes(0);
    }

    private void relex(Vertex preVertex, Vertex now,MinHeap minHeap){ //松弛一条边
        if(now.getDistance() > preVertex.getDistance() + preVertex.getEdge(now).getWeight()){
            now.setPreVertex(preVertex);
            now.setDistance(preVertex.getDistance() + preVertex.getEdge(now).getWeight());
            minHeap.update(now);
        }
    }

    private void relex1(Vertex preVertex, Vertex now,MinHeap1 minHeap){ //松弛一条边
        if(now.getChangeTimes() > preVertex.getChangeTimes() + preVertex.getChangeOrNot(now)){
            now.setPreVertex(preVertex);
            now.setChangeTimes(preVertex.getChangeTimes() + preVertex.getChangeOrNot(now));
            now.setDistance(preVertex.getDistance() + now.getEdge(preVertex).getWeight());
            now.setToLine(preVertex.getEdge(now).getLine());
            minHeap.update(now);
        }
    }

    ArrayList<Vertex> getPath(ArrayList<Vertex> vertices1, Vertex start, Vertex end,int type){ //返回路径列表
        if(type == 0)
            return getPath1(vertices1,start,end);
        else{
            return getPath2(vertices1,start,end);
        }

    }

    private ArrayList<Vertex> getPath1(ArrayList<Vertex> vertices1, Vertex start, Vertex end){ //返回路径列表
        DIJKSTRA(vertices1,start,end);
        ArrayList<Vertex> path = new ArrayList<>();
        Stack<Vertex> vertices = new Stack<>();
        vertices.push(end);
        while (end != start && end.getPreVertex() != null){
            end = end.getPreVertex();
            vertices.push(end);
        }
        while (!vertices.empty()){
            path.add(vertices.pop());
        }
        return path;
    }

    private ArrayList<Vertex> getPath2(ArrayList<Vertex> vertices1, Vertex start, Vertex end){ //返回路径列表
        Vertex sameLineVertex = DIJKSTRA1(vertices1,start,end);
        Stack<Vertex> vertices2 = new Stack<>();
        Vertex end2 = sameLineVertex;
        int changeTimes = sameLineVertex.getChangeTimes();
        while (end2 != start && end2.getPreVertex() != null){
            end2 = end2.getPreVertex();
            vertices2.push(end2);
        }

        double distance1 = sameLineVertex.getDistance();

        Vertex vertex1 = new Vertex("temp","0","0");
        vertex1.setLine("Line 120");
        end.setLine("Line 120");
        Edge edge = new Edge(vertex1,end,"Line 120",0);
        end.getEdges().add(edge);
        vertex1.getEdges().add(edge);

        Vertex end1 = DIJKSTRA1(vertices1,sameLineVertex,vertex1);

        double distance2 = end1.getDistance();
        minutes = distance1 + distance2;
        end.setChangeTimes(changeTimes + 1);

        end.removeLine("Line 120");
        end.getEdges().remove(edge);
        end.getVertices().remove(vertex1);
        vertices1.remove(vertex1);

        ArrayList<Vertex> path = new ArrayList<>();
        Stack<Vertex> vertices = new Stack<>();
        vertices.push(end1);
        while (end1 != sameLineVertex && end.getPreVertex() != null){
            end1 = end1.getPreVertex();
            vertices.push(end1);
        }

        while (!vertices2.empty()){
            path.add(vertices2.pop());
        }

        while (!vertices.empty()){
            path.add(vertices.pop());
        }

        return path;
    }
    double getMinutes(){
        return minutes;
    }

    private Vertex DIJKSTRA1(ArrayList<Vertex> vertices, Vertex start, Vertex end){ //某个算法
        initialize_single_source1(vertices,start);
        MinHeap1 minHeap = new MinHeap1(vertices);
        while (!minHeap.isEmpty()){
            Vertex u = minHeap.extract_min();
            if( u.sameLine(end)){   //找到了共线站,结束
                return u;
            }
            for(Vertex vertex:u.getVertices()){
                relex1(u,vertex,minHeap);
            }
        }
        return null;
    }

    private int getIndex(String lin, String lines){
        String[] lines1 = lines.split(":");
        for(int i = 0; i < lines1.length; i++){
            if(lines1[i].equals(lin)){
                return i;
            }
        }
        return 0;
    }
}

