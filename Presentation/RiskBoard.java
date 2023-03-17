package Presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class RiskBoard extends JFrame {
    private BufferedImage colorMap;
    //private HashMap<Color, String> countryMap;
    private JLabel boardLabel;

    public RiskBoard() {

        super("Risk Board");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        BufferedImage boardImage = null;
        try {
            boardImage = ImageIO.read(new File("Data/RiskBoard.jpeg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            colorMap = ImageIO.read(new File("Data/RiskPainted.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        JPanel boardPanel = new JPanel();
        boardLabel = new JLabel(new ImageIcon(Objects.requireNonNull(boardImage)));
        boardPanel.add(boardLabel);

        boardLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Color alaskaColor = new Color(204, 204, 204);


                int x = e.getX();
                int y = e.getY();

                Color pixelColor = new Color(colorMap.getRGB(x, y));

                if (pixelColor.equals(alaskaColor)) {
                    System.out.println("You clicked on " + alaskaColor);
                }
            }
        });

        setLayout(new BorderLayout());
        add(boardPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    public static void main(String[] args) {
        new RiskBoard();
    }
}
                                                                                    