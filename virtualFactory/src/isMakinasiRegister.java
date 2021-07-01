import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

public class isMakinasiRegister implements Runnable {
    private JTextField ad;
    private JTextField id;
    private JTextField status;
    private JTextField hiz;
    private JTextField tur;
    private JButton baglanButon;
    private JPanel panel3;

    public String threadID;
    public String speed, job;
    public int busytime;

    //thread başlatan constructor
    isMakinasiRegister(String speed, String job, String id){
        this.speed = speed;
        this.job = job;
        this.busytime = Integer.parseInt(job) / Integer.parseInt(speed);
        this.threadID = id;
    }
    //ui başlatan constructor
    isMakinasiRegister(){

        baglanButon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = ad.getText();
                String ID = id.getText();
                String status = "EMPTY";
                String speed = hiz.getText();
                String tur2 = tur.getText();
                String makine = "M";
                if(name != null && ID != null && status != null && speed != null && tur2 != null){
                    try {
                        Socket s = new Socket("localhost",4999);
                        PrintWriter pr = new PrintWriter(s.getOutputStream());
                        pr.print(makine + "+");
                        pr.print(name + "+");
                        pr.print(ID + "+");
                        pr.print(status + "+");
                        pr.print(speed + "+");
                        pr.println(tur2);
                        pr.flush();

                        threadID = ID;
                        InputStreamReader in = new InputStreamReader(s.getInputStream());
                        BufferedReader bf = new BufferedReader(in);
                        String str = bf.readLine();
                        if(str == null) System.out.println("im null");
                        if(str.equals("yes")){
                            JOptionPane.showMessageDialog(null,"Makine Eklendi!");
                            baglanButon.setEnabled(false);

                        } else if(str.equals("no")){
                            JOptionPane.showMessageDialog(null,"Aynı ID ile başka bir makine mevcut!\nFarklı bir id ile tekrardan girin");
                        }

                        s.close();


                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                } else{
                    System.out.println("Bilgileri eksiksiz gir!");
                }
            }
        });

    }

    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("isMakinasiRegister");
        isMakinasiRegister jp = new isMakinasiRegister();
        frame.setContentPane(jp.panel3);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                try {
                    Socket s = new Socket("localhost",4999);
                    PrintWriter pr = new PrintWriter(s.getOutputStream());
                    String exitMsg = "MO";
                    pr.print(exitMsg + "+");
                    pr.println(jp.threadID + "+");
                    pr.flush();
                    System.out.println(jp.threadID + " idli Makine siliniyor!");

                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            System.exit(0);
            }
        });

        frame.pack();
        frame.setVisible(true);

    }

    @Override
    public void run() {

        try{
            System.out.println(threadID + " id'li makine "+busytime+" saniye kadar bekleyecek!");
            Thread.sleep(this.busytime * 1000);
            Socket s = new Socket("localhost",4999);
            PrintWriter pr = new PrintWriter(s.getOutputStream());
            //thread bitiş formatı: PGED+threadID+job
            pr.print("PGED+");
            pr.print(threadID+"+");
            pr.println(job);
            pr.flush();
            s.close();

        }catch (Exception e){
            System.out.println(threadID + " id'li makinede hata var!");
        }


    }
}
