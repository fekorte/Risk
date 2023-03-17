package Presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class RiskBoard extends JFrame {
    private BufferedImage colorMap;
    private HashMap<Color, String> countryMap;
    private JLabel boardLabel;

    public RiskBoard() {
        super("Risk Board");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        // Load the board image
        BufferedImage boardImage = null;
        try {
            boardImage = ImageIO.read(new File("Data/RiskBoard.jpeg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Load the color map
        try {
            colorMap = ImageIO.read(new File("Data/RiskPainted.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create a JPanel to hold the board image
        JPanel boardPanel = new JPanel();
        boardLabel = new JLabel(new ImageIcon(boardImage));
        boardPanel.add(boardLabel);

        // Add a mouse listener to the board label
        boardLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Get the color of the pixel that was clicked
                Color alaskaColor = new Color(204, 204, 204);


                int x = e.getX();
                int y = e.getY();

                Color pixelColor = new Color(colorMap.getRGB(x, y));

                if (pixelColor.equals(alaskaColor)) {
                    System.out.println("You clicked on " + alaskaColor);
                }
            }
        });

        // Add the board panel to the center of the frame
        setLayout(new BorderLayout());
        add(boardPanel, BorderLayout.CENTER);

        // Show the frame
        setVisible(true);
    }

    public static void main(String[] args) {
        new RiskBoard();
    }
}
                                                                                    