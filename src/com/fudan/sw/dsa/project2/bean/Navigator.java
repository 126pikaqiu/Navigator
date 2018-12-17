package com.fudan.sw.dsa.project2.bean;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Navigator {
    private ArrayList<Vertex> vertices = new ArrayList<>();
    private HashMap<String,Vertex> map = new HashMap<>();
    private double minutes;//耗时
    private double walkDistance;//步行距离
    public void loadMap(File input) { //导入地图
        try {
            Workbook book = Workbook.getWorkbook(input);
            int sheetSize = book.getNumberOfSheets();
            String name;
            String stationName;
            String time1 = null;
            String time2;
            int startRow;//起始行
            Vertex newStation;
            Vertex preStation;
            Vertex binStation;//两个方向的站点
            for(int k = 0; k < sheetSize; k++) {
                name = book.getSheetNames()[k];
                Sheet sheet = book.getSheet(k);

                if(k == 9 || k == 10) {
                    startRow = 2;
                }else {
                    startRow = 1;
                }

                newStation = null;
                int j = sheet.getRows();//两个方向的站点对应的索引
                String time3 = null;
                binStation = null;

                //导入中间站点
                for(int i = startRow; i < sheet.getRows(); i++) { //第一个方向和第二个方向的部分站点

                    preStation = newStation;
                    stationName = sheet.getCell(0, i).getContents();//这个工作簿的表的数量
                    time2 = sheet.getCell(3, i).getContents();

                    if(time2.equals("--") || (sheet.getColumns() > 2 && sheet.getCell(2, i).getContents().equals("--"))){ //跳过某些站点
                        if(binStation == null){
                            binStation = preStation;
                            j = i;
                            time3 = time1;
                        }
                        if(time2.equals("--"))
                            continue;
                    }

                    newStation = exist(stationName,sheet.getCell(1, i).getContents(),sheet.getCell(2, i).getContents(),name);

                    if(preStation != null){ //也就第一个站点的问题
                        preStation.getVertices().add(newStation);
                        newStation.getVertices().add(preStation);
                        Edge edge = new Edge(preStation, newStation, name, getTime(time1, time2));
                        preStation.getEdges().add(edge);
                        newStation.getEdges().add(edge);
                        edge = null; //我想释放资源
                    }
                    time1 = time2;
                }

                preStation = binStation;
                time1 = time3;
                for(; j < sheet.getRows(); j++) { //第二个方向的分站点
                    stationName = sheet.getCell(0, j).getContents();
                    time2 = sheet.getCell(4, j).getContents();

                    if(time2.equals("--")){ //跳过某些站点
                        continue;
                    }

                    newStation = exist(stationName,sheet.getCell(1, j).getContents(),sheet.getCell(2, j).getContents(),name);
                    assert preStation != null;
                    preStation.getVertices().add(newStation);
                    newStation.getVertices().add(preStation);
                    Edge edge = new Edge(preStation, newStation, name, getTime(time1, time2));
                    preStation.getEdges().add(edge);
                    newStation.getEdges().add(edge);
                    edge = null; //我想释放资源
                    time1 = time2;
                    preStation = newStation;
                }

            }

        }catch (BiffException | IOException e) {
            e.printStackTrace();
        }
    }

    private Vertex exist(String station,String longitude, String latitude,String line) {  //判断是否已经导入了点
        Vertex newStation = map.get(station);
        if(newStation == null){
            newStation = new Vertex(station,longitude, latitude);
            vertices.add(newStation);
            map.put(station,newStation);
        }
        newStation.setLine(line);
        return  newStation;
    }


    private int getTime(String start, String end) { //返回分钟数
        int length1 = start.length();
        int length2 = end.length();
        int minute = (int)end.charAt(length2 - 1) - (int)start.charAt(length1 - 1);
        int ten = (int)end.charAt(length2 - 2) - (int)start.charAt(length1 - 2);
        int hour = (int)end.charAt(length2 - 4) - (int)start.charAt(length1 - 4);
        hour = hour >= 0? hour : hour + 4;
        return hour * 60 + ten * 10 + minute;
    }

    private ArrayList<Address> getPath(String start, String end,int type){
        Dijkstra dijkstra = new Dijkstra();
        Vertex startV = map.get(start);
        Vertex endV = map.get(end);
        if(startV == null || endV == null){
            return null;
        }
        ArrayList<Address> pathA = new ArrayList<>();
        if(startV == endV){
            pathA.add(startV.getAddress());
            return pathA;
        }
        ArrayList<Vertex> pathV = dijkstra.getPath(vertices,startV,endV,type);
        minutes = dijkstra.getMinutes();
        for(Vertex v:pathV){
            if(v.getName().length() != 1){ //过滤掉补充的点
                pathA.add(v.getAddress());
            }
        }
        return pathA;
    }

    private ArrayList<Address> getPath(Address startPoint, Address endPoint,int type){ //获得路径
        String start = startPoint.getAddress();
        String end = endPoint.getAddress();
        return getPath(start,end,type);
    }

    public ArrayList<Address> getPath_OF_MINTIME(Address startPoint,Address endPoint){ //最短时间，重新建图
        Vertex vertex1 = new Vertex(startPoint.getAddress() + "000",startPoint.getLongitude() + "",startPoint.getLatitude() + "");
        Vertex vertex2 = new Vertex(endPoint.getAddress() + "111",endPoint.getLongitude() + "",endPoint.getLatitude() + "");
        double tim1;
        double tim2;
        for(Vertex vertex:vertices){
            tim1 = distance.getDistance(vertex1.getAddress(),vertex.getAddress()) / 1000 * 12;
            tim2 = distance.getDistance(vertex2.getAddress(),vertex.getAddress()) / 1000 * 12;
            Edge edge1 = new Edge(vertex1,vertex,"00",tim1);
            Edge edge2 = new Edge(vertex2,vertex,"00",tim2);
            vertex.getEdges().add(edge2);
            vertex.getVertices().add(vertex2);
            vertex1.getVertices().add(vertex);
            vertex1.getEdges().add(edge1);
        }
        vertices.add(vertex1);
        vertices.add(vertex2);
        map.put(startPoint.getAddress() + "000",vertex1);
        map.put(endPoint.getAddress() + "111",vertex2);
        ArrayList<Address> path =  getPath(startPoint.getAddress() + "000",endPoint.getAddress() + "111",0);
        for(Vertex vertex:vertices){ //从图中删除加入点，加入边
            vertex.getVertices().remove(vertex2);
        }
        vertices.remove(vertex1);
        vertices.remove(vertex2);
        map.remove(startPoint.getAddress() + "000");
        map.remove(endPoint.getAddress() + "111");
        assert path != null;
        walkDistance = distance.getDistance(path.get(1),startPoint) + distance.getDistance(path.get(path.size() - 2),endPoint);
        path.remove(0);
        path.remove(path.size() - 1);
        return path;
    }
    public ArrayList<Address> getPath_OF_MINWALIKING(Address startPoint,Address endPoint) { //最短步行
        Address start = getNearestStation(startPoint);//最近的站点
        Address end = getNearestStation(endPoint);//最近的站点
        ArrayList<Address> addresses1 = getPath(start, end,0);
        ArrayList<Address> addresses = new ArrayList<>(addresses1);
        walkDistance = distance.getDistance(start,startPoint) + distance.getDistance(end,endPoint);
        minutes += walkDistance / 1000 * 12;
        return addresses;
    }

    public ArrayList<Address> getPath_OF_MINCHANGETIMES (Address startPoint, Address endPoint){ //最少换乘次数
        ArrayList<ArrayList<Address>> arrayLists = new ArrayList<>();
        Address[] addresses1 = getNearestThreeStation(startPoint);
        Address[] addresses2 = getNearestThreeStation(endPoint);
        int[] changeTimes = new int[9];
        double[] minutes = new double[9];
        for(int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++){
                arrayLists.add(getPath(addresses1[i],addresses2[j],1));
                changeTimes[3 * i + j] = addresses2[j].getChangeTimes();
                minutes[3 * i + j] = this.minutes;
            }
        for(int i = 0; i < minutes.length; i++){
            minutes[i] += (distance.getDistance(startPoint,arrayLists.get(i).get(0)) + distance.getDistance(endPoint,arrayLists.get(i).get(arrayLists.get(i).size() - 1))) / 1000 * 12;
        }
        int j = getBestPath(changeTimes,minutes);
        walkDistance = distance.getDistance(arrayLists.get(j).get(0),startPoint) + distance.getDistance(arrayLists.get(j).get(arrayLists.get(j).size() - 1),endPoint);
        this.minutes = minutes[j] + walkDistance / 1000 * 12;
        return arrayLists.get(0);
    }

    private int getBestPath(int[] changeTimes,double[] minutes){ //选出最小换乘，相同换乘去时间较短者
        int minChangeTimes = 0;int j = 0;
        for (int changeTime : changeTimes) {
            if (minChangeTimes == 0 || changeTime < minChangeTimes) {
                minChangeTimes = changeTime;
            }
        }
        double minute = 0;
        for(int k = 0; k < changeTimes.length; k++){
            if(changeTimes[k] == minChangeTimes && (minute == 0 || minute > minutes[k])){
                minute = minutes[k];
                j = k;
            }
        }
        return j;
    }

    private Address getNearestStation(Address address){ //寻找距离该点最近的地铁站
        double dis1 = 0;
        double dis2;
        Vertex nearestStation = vertices.get(0);
        for(Vertex v:vertices){
            dis2 = distance.getDistance(address,v.getAddress());
            if(dis1 == 0 || dis1 > dis2 ){
                dis1 = dis2;
                nearestStation = v;
            }
        }
        return nearestStation.getAddress();
    }


    private Address[] getNearestThreeStation(Address address){ //寻找距离该点最近的3个地铁站
        Address[] addresses = new Address[3];
        Vertex[] vertices1 = new Vertex[3];
        double dis2;
        double dis1;
        for(int i = 0; i < 3; i++){
            dis1 = 0;
            for(Vertex v:vertices){
                dis2 = distance.getDistance(address,v.getAddress());
                if(dis1 == 0 || dis1 > dis2 ){
                    dis1 = dis2;
                    addresses[i] = v.getAddress();
                    vertices1[i] = v;
                }
            }
            vertices.remove(vertices1[i]);
        }

        vertices.add(vertices1[0]);
        vertices.add(vertices1[1]);
        vertices.add(vertices1[2]);
        return addresses;
    }

    public double getMinutes(){ //获得总时间
        return minutes;
    }

    public double getWalkDistance(){ //获得步行距离
        return walkDistance;
    }

    private Distance distance = new Distance(); //计算距离的对象

    class Distance{
        private double EARTH_RADIUS = 6378.137;

        private double rad(double d) {
            return d * Math.PI / 180.0;
        }

        /**
         * 通过经纬度获取距离(单位：米)
         *
         * @param address1
         * @param address2
         * @return 距离
         */
        public double getDistance(Address address1,Address address2) {
            double lat1 = address1.getLatitude();
            double lng1 = address1.getLongitude();
            double lat2 = address2.getLatitude();
            double lng2 = address2.getLongitude();
            double radLat1 = rad(lat1);
            double radLat2 = rad(lat2);
            double a = radLat1 - radLat2;
            double b = rad(lng1) - rad(lng2);
            double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                    + Math.cos(radLat1) * Math.cos(radLat2)
                    * Math.pow(Math.sin(b / 2), 2)));
            s = s * EARTH_RADIUS;
            s = Math.round(s * 10000d) / 10000d;
            s = s * 1000;
            return s;
        }
    }

}
