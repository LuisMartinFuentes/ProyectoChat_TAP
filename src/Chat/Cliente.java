
package Chat;

/**
 *
 * @author Martin Fuentes
 */
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cliente {
    BufferedReader in;
    PrintWriter out;

    Socket cnx;

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                (new Cliente()).start();
            }
        });
    }

    void start() {
        JFrame frame = new JFrame("Cliente");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 410);

        JPanel panel = new JPanel();
        frame.add(panel);

        JLabel label = new JLabel("Text:");
        panel.add(label);

        JTextField textField = new JTextField(20);
        panel.add(textField);

        JButton button = new JButton("Enviar");
        panel.add(button);

        JTextArea textArea = new JTextArea(20, 30);
        panel.add(new JScrollPane(textArea));

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String comando = textField.getText();
                out.println(comando);
            }
        });

        frame.setVisible(true);

        Conexion hilo;
        try {
            this.cnx = new Socket("127.0.0.1", 3000);

            in = new BufferedReader(new InputStreamReader(cnx.getInputStream()));
            out = new PrintWriter(cnx.getOutputStream(), true);

            hilo = new Conexion(in, textArea);
            hilo.start(); // Hilo encargado de lecturas del servidor

        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    class Conexion extends Thread {
        public boolean ejecutar = true;
        BufferedReader in;
        JTextArea textArea;

        public Conexion(BufferedReader in, JTextArea textArea) {
            this.in = in;
            this.textArea = textArea;
        }

        @Override
        public void run() {
            String respuesta;
            while (ejecutar) {
                try {
                    respuesta = in.readLine();
                    if (respuesta != null) {
                        textArea.append(respuesta + "\n");
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}

