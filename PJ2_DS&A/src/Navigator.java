import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

class Navigator {
    private ArrayList<Vertex> vertices = new ArrayList<>();
    private HashMap<String,Vertex> map = new HashMap<>();
    void loadMap(String path) {
        File input = new File(path);
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
                    stationName = sheet.getCell(0, i).getContents();
                    time2 = sheet.getCell(1, i).getContents();

                    //两个浦电路站,特殊处理
                    if(name.equals("Line 4") && stationName.equals("浦电路")){
                        stationName = stationName + "4";
                    }else if(name.equals("Line 6") && stationName.equals("浦电路")){
                        stationName = stationName + "6";
                    }

                    if(time2.equals("--") || (sheet.getColumns() > 2 && sheet.getCell(2, i).getContents().equals("--"))){ //跳过某些站点
                        if(binStation == null){
                            binStation = preStation;
                            j = i;
                            time3 = time1;
                        }
                        if(time2.equals("--"))
                            continue;
                    }

                    newStation = exist(stationName);

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
                    time2 = sheet.getCell(2, j).getContents();

                    if(time2.equals("--")){ //跳过某些站点
                        continue;
                    }

                    newStation = exist(stationName);
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

            //四号线是环线
            Vertex vertex1 = exist("宜山路");
            Vertex vertex2 = exist("虹桥路");
            Edge edge = new Edge(vertex1,vertex2,"Line 4",3);//3分钟
            assert vertex1 != null;
            vertex1.getEdges().add(edge);
            assert vertex2 != null;
            vertex2.getEdges().add(edge);
            vertex1.getVertices().add(vertex2);
            vertex2.getVertices().add(vertex1);

        }catch (BiffException | IOException e) {
            e.printStackTrace();
        }
    }

    private Vertex exist(String station) {  //判断是否已经导入了点
        Vertex newStation = map.get(station);
        if(newStation == null){
            newStation = new Vertex(station);
            vertices.add(newStation);
            map.put(station,newStation);
        }
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

    private ArrayList<Vertex> getPath1(String start, String end){
        Dijkstra dijkstra = new Dijkstra();
        Vertex startV = exist(start);
        Vertex endV = exist(end);
        if(startV == null || endV == null) {
            return null; //抱歉没有这个站点
        }
        ArrayList<Vertex> path = new ArrayList<>();
        if(startV == endV){
            path.add(startV);
            return path;
        }
        return dijkstra.getPath(vertices,startV,endV);
    }

    private ArrayList<String> getPath(String start, String end){
        ArrayList<Vertex> path = getPath1(start,end);
        ArrayList<String> path1 = new ArrayList<>();
        if(path == null){
            path1.add("您输入的站点不存在");
            return path1;
        }
        Vertex preVertex = path.get(0);
        Vertex vertex;
        String line = "";
        for (int i = 1; i < path.size(); i++){
            vertex = path.get(i);
            if(!vertex.getEdge(preVertex).getLine().equals(line)){
                line = vertex.getEdge(preVertex).getLine();
                path1.add(preVertex.getName());
                path1.add("-");
                path1.add(line);
                path1.add("-");
            }
            preVertex = vertex;
        }
        path1.add(end);
        return path1;
    }

    ArrayList<String> getPath(String[] station){
        ArrayList<String> path = new ArrayList<>();
        for(String value:station){
            if(exist(value) == null){
                path.add("您输入的站点不存在");
                return path;
            }
        }

        String pre = station[0];
        getPath(pre,station[station.length - 1]); //走一遍从起点到终点的路线，可以作为记忆,可能极其重要哟。
        ArrayList<String> path1;
        int i;
        for(i = 1; i < station.length; i++){
            path1 = getPath(pre,station[i]);
            path1.remove(path1.size() - 1);
            path.addAll(path1);
            pre = station[i];
        }
        path.add(station[i - 1]);
        return path;
    }
}
