import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GirisEkrani {
    private JFrame mainFrame;
    public static String[] characterNames;

    public GirisEkrani() {
        prepareMainFrame();
    }

    private void prepareMainFrame() {
        mainFrame = new JFrame("Who Is It Game");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        JLabel logoLabel = new JLabel(new ImageIcon("images\\Logo.png"));

        JButton startButton = new JButton("Başla");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });

        panel.add(logoLabel, BorderLayout.CENTER);
        panel.add(startButton, BorderLayout.SOUTH);

        mainFrame.getContentPane().add(panel);
        mainFrame.setSize(400, 400);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    private void startGame() {
        mainFrame.setVisible(false);

        OyunEkrani frame2 = new OyunEkrani();
        frame2.showFrame();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GirisEkrani();
            }
        });
    }
}
