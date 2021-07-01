import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class planlamaci_GUI extends JFrame implements Runnable{
    private JPanel panel2;
    //private JTable table1;
    //private JScrollPane sp;
    //private JList<String> list1;

    public planlamaci_GUI(String username) {
        JFrame guiframe = new JFrame();
        String[][] tabledata = new String[10][5];
        String[] columnname = {"name", "id", "status", "speed", "tur"};


        guiframe.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    Socket s = new Socket("localhost",4999);
                    PrintWriter pr = new PrintWriter(s.getOutputStream());
                    String exitMsg = "PO";
                    pr.print(exitMsg + "+");
                    pr.println(username);
                    pr.flush();
                    System.out.println(username + " kullanıcısı offline oluyor!");

                    s.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                System.exit(0);
            }
        });
/*
        DefaultListModel<String> listModel = new DefaultListModel<String>();
        //listModel.addElement("pat");
        JList<String> list1 = new JList<String>(listModel);
        list1.setBounds(0, 20, 200, 200);
*/
        //panel Emir Paneli
        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel panelLabel = new JLabel("İŞ EMRİ");
        panelLabel.setBounds(10, 10, 80, 25);
        panel.add(panelLabel);
        JLabel turLabel = new JLabel("iş türü: ");
        turLabel.setBounds(10,40,80,25);
        panel.add(turLabel);
        JTextField turTextField = new JTextField();
        turTextField.setBounds(100,40,165,25);
        panel.add(turTextField);
        JLabel uzunlukLabel = new JLabel("iş uzunluğu: ");
        uzunlukLabel.setBounds(10,70,80,25);
        panel.add(uzunlukLabel);
        JTextField uzunlukTextField = new JTextField();
        uzunlukTextField.setBounds(100,70,165,25);
        panel.add(uzunlukTextField);

        JButton sendButton = new JButton("Emir Gönder");
        sendButton.setBounds(100,105,165,25);
        panel.add(sendButton);

        JButton showButton = new JButton("Kuyruktaki Emirleri Göster");
        showButton.setBounds(100,130,165,25);
        panel.add(showButton);

        JButton jobDoneButton = new JButton("Bitmiş İşleri Göster");
        jobDoneButton.setBounds(100,155,165,25);
        panel.add(jobDoneButton);

        //panel2 Makine Tablo Paneli
        JPanel panel2 = new JPanel();
        panel2.setLayout(null);

        JLabel label = new JLabel("Available Machines");
        label.setBounds(10,0,120,20);
        panel2.add(label);

        JTable table1 = new JTable(tabledata, columnname);
        //table1.setBounds(0,30,300,280);
        table1.setAutoCreateRowSorter(true);
        JScrollPane sp = new JScrollPane(table1);
        sp.setBounds(10,30,300,280);
        panel2.add(sp);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBounds(200,0,100,25);
        panel2.add(refreshButton);

        guiframe.add(panel2);
        guiframe.add(panel);
        guiframe.setSize(200,400);
        guiframe.setLayout(new GridLayout(0,1));

        guiframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        guiframe.pack();
        guiframe.setVisible(true);

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //sunucuya istek atalım ki istemeye yüzümüz olsun

                    Socket socket = new Socket("localhost", 4999);
                    String plangui = "PG";
                    PrintWriter pr = new PrintWriter(socket.getOutputStream());
                    pr.println(plangui);
                    pr.flush();

                    InputStreamReader in = new InputStreamReader(socket.getInputStream());
                    BufferedReader bf = new BufferedReader(in);
                    String str = bf.readLine();
                    JOptionPane.showMessageDialog(null,"Str: "+str);
                    if(!str.equals("[]")){
                        //JOptionPane.showMessageDialog(null,"plangui: not null");
                        str = str.replace("[", "");
                        str = str.replace("]", "");
                        str = str.replace("M+", "");
                        JOptionPane.showMessageDialog(null,"str: "+str);
                        String[] afterSplit = str.split(",");
                        String[] temp = null;
                        String temp2;
                        //max 10 işmakinası olabilir kısıtı!!!!
                        //tabledata = new String[10][5];

                        //reset tabledata

                        for(int i = 0; i < table1.getRowCount(); i++){
                            for(int j = 0; j < table1.getColumnCount(); j++){
                                tabledata[i][j] = null;
                            }
                        }

                        for(int i = 0; i < afterSplit.length; i++){
                            //JOptionPane.showMessageDialog(null,"aftersplit: "+afterSplit[i]);
                            temp2 = afterSplit[i];
                            //JOptionPane.showMessageDialog(null,"temp2: "+temp2);
                            temp = temp2.split("\\+");

                            for(int j = 0; j < 5; j++){
                                //format: name id status speed tur
                                //empty durumundaki makinelerin listesi
                                if(temp[2].equals("EMPTY")){
                                    tabledata[i][j] = temp[j];

                                }
                            }
                        }
                        //dtm.addRow(tabledata);
                        //table1.addRowSelectionInterval(0,0);
                        //listModel.addElement(tabledata[0][1]);
                        table1.repaint();

                    }else{
                        JOptionPane.showMessageDialog(null,"Sistemde makine bulunamadı");
                    }

                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        });

        sendButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                String turText = turTextField.getText();
                String uzunlukText = uzunlukTextField.getText();
                String plangui = "PGE";
                if(turText != null && uzunlukText != null && !turText.equals("") && !uzunlukText.equals("")) {
                    try {
                        Socket s = new Socket("localhost",4999);
                        PrintWriter pr = new PrintWriter(s.getOutputStream());
                        pr.print(plangui + "+");
                        pr.print(turText + "+");
                        pr.println(uzunlukText);
                        pr.flush();
                        JOptionPane.showMessageDialog(null,"Emir Sunucuya Gönderildi");

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                } else{
                    JOptionPane.showMessageDialog(null,"Boş emir gönderilemez!");
                }
            }
        });

        showButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Socket socket = new Socket("localhost", 4999);
                    String plangui = "PGS";
                    PrintWriter pr = new PrintWriter(socket.getOutputStream());
                    pr.println(plangui);
                    pr.flush();

                    InputStreamReader in = new InputStreamReader(socket.getInputStream());
                    BufferedReader bf = new BufferedReader(in);
                    String str = bf.readLine();

                    if(!str.equals("[]")){
                        JOptionPane.showMessageDialog(null,"Emirler: \n"+str);
                    } else{
                        JOptionPane.showMessageDialog(null,"Kuyrukta Emir Yok!");
                    }

                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        });

        jobDoneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Socket socket = new Socket("localhost", 4999);
                    String plangui = "PGJ";
                    PrintWriter pr = new PrintWriter(socket.getOutputStream());
                    pr.println(plangui);
                    pr.flush();

                    InputStreamReader in = new InputStreamReader(socket.getInputStream());
                    BufferedReader bf = new BufferedReader(in);
                    String str = bf.readLine();
                    if(!str.equals("[]")){
                        JOptionPane.showMessageDialog(null,"Bitmiş İşler: \n"+str);
                    } else{
                        JOptionPane.showMessageDialog(null,"Bitmiş İş Yok!");
                    }

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

    }

    @Override
    public void run() {

    }

}
