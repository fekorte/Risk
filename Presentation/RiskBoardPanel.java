package Presentation;

import Business.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.swing.*;

public class RiskBoardPanel extends JPanel {

    private BufferedImage paintedMap;

    public interface RiskBoardListener {
        void onCountrySelected(String countryName);
    }

    public RiskBoardPanel(IWorldManager worldManager, RiskBoardListener listener){

        BufferedImage boardImage = null;
        try {
            boardImage = ImageIO.read(new File("Images/RiskBoard.jpeg"));
            paintedMap = ImageIO.read(new File("Images/RiskMapPainted.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        JLabel riskBoard = new JLabel(new ImageIcon(Objects.requireNonNull(boardImage)));
        add(riskBoard);

        riskBoard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                int x = e.getX();
                int y = e.getY();

                Color pixelColor = new Color(paintedMap.getRGB(x, y));
                String selectedCountry = worldManager.getCountryNameByColor(pixelColor);
                if (listener != null) {
                    listener.onCountrySelected(selectedCountry);
                }
            }
        });
    }
}
                                                                                    