import java.util.ArrayList;
import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Navigator navigator = new Navigator();
        navigator.loadMap("src/Timetable.xls");
        Scanner scanner = new Scanner(System.in);
        while (true){
            System.out.println("Please input some stations:(q for exit)");
            String line = scanner.nextLine();
            if(line.trim().equals("q")){
                break;
            }
            if(line.split(" ").length < 2){
                continue;
            }

            long time1 = System.nanoTime();
            String[] paths = navigator.getPath(line.split(" "));
            long time2 = System.nanoTime();
            for (String aPath : paths) {
                System.out.println(aPath);
            }
            System.out.println("查询时间为 " + (time2 - time1) + "ns（" + (time2 - time1) / 1000000000 + "ms)");
        }
        System.out.println("bye bye");
    }

}
