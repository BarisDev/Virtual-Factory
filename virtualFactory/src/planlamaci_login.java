import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class planlamaci_login extends JFrame implements Runnable {
    private JPanel panel1;
    private JButton loginButton;
    private JTextField textField1;
    private JPasswordField passwordField1;

    public planlamaci_login(JFrame frame) {

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //butona tıklayınca olacak şeyler
                String username = textField1.getText();
                String password = String.valueOf(passwordField1.getPassword());
                String plan = "P";
                if(password != null && username != null){
                    try {
                        Socket s = new Socket("localhost",4999);
                        PrintWriter pr = new PrintWriter(s.getOutputStream());
                        pr.print(plan + "+");
                        pr.print(username+"+");
                        pr.println(password);
                        pr.flush();
                        JOptionPane.showMessageDialog(null,"Sent to server");


                        InputStreamReader in = new InputStreamReader(s.getInputStream());
                        BufferedReader bf = new BufferedReader(in);
                        String str = bf.readLine();
                        System.out.println("client: "+str);
                        if(str.equals("yes")) {
                            System.out.println(username + " is Online!");

                            planlamaci_GUI gui = new planlamaci_GUI(username);

                            frame.setVisible(false);
                            frame.dispose();
                        }
                        if(str.equals("no")) System.out.println("Bu kullanıcı zaten online\nBaşka bir kullanıcı adı deneyin!");
                        s.close();

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                else{
                    JOptionPane.showMessageDialog(null,"Enter your login info!");
                }
            }
        });
    }

    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("planlamaci1_login");
        frame.setContentPane(new planlamaci_login(frame).panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }

    @Override
    public void run() {

    }

}
