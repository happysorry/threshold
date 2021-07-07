package happysorry.src.main.java.threshold;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class service implements Runnable{
    public static double sim_time = 0;
    public  String con_name = "";
    public void run(){
        double startTime = System.nanoTime();
        int iter = 0;
        while((System.nanoTime() - startTime) / 1e9 < sim_time){
            iter ++;
            int flag = read();
            if(flag != 0){
                write(iter);
                Wait(40000);
            }
                
            // System.out.println("sim_time " + sim_time);
            double use = get_state2();
            System.out.println(con_name + " use " + use);
            if(use > 80){
                int replicas = get_cons();
                if(replicas < 4){
                    replicas ++;
                    add_cons(replicas);
                }
            }
            if(use < 10){
                int replicas = get_cons();
                if(replicas > 1){
                    replicas --;
                    add_cons(replicas);
                }
            }

            Wait(30000);
        }
    }
    public int read(){
        String filename = "signal.txt";
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader r = new BufferedReader(fr);
            int line = 0;
            try {
                line = Integer.parseInt(r.readLine());
            if(line==1){
                return 1;
            }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

    // public static void main(String[]args){
    //     String con_name = "app_mn1";
    //     int replicas = get_cons();
    //     add_cons(replicas);
    // }
    public service(String con_name , double sim_time){
        this.con_name = con_name;
        this.sim_time = sim_time;
    }

    public double get_state2() {
        FileReader fr;
        String filename = "use/" + con_name  + "_use2.txt";
        double avg = 0.0;
        try {
            fr = new FileReader(filename);
            BufferedReader r = new BufferedReader(fr);
            String line = "";
            try {
                while ((line = r.readLine()) != null) {
                    avg = Double.parseDouble(line);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return avg;
    }
    public int get_cons(){
        String filename = "con/" + con_name + "_con2.txt";
        int replicas = 0;
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader r = new BufferedReader(fr);
            String line = "";
            try {
                while ((line = r.readLine()) != null) {
                    replicas = (int) Double.parseDouble(line);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return replicas;
    }

    public  void add_cons(int replicas){
        Runtime run = Runtime.getRuntime();
        Process pr;
        String cmd = "sudo docker-machine ssh default docker service scale " + con_name + "=" + replicas;
        System.out.println(cmd);
        try {
            pr = run.exec(cmd);
        }catch(IOException e){

        }
    }
    void write(int iter) {
        try {
            String filename = "error.txt";
            FileWriter fw1 = new FileWriter(filename,true);
            fw1.write(iter + "\n");
            fw1.flush();
            fw1.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void Wait(long t){
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
