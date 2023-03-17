package Presentation;

import Business.*;
import Persistence.FilePersistence;
import Persistence.IPersistence;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class RiskBoard extends JFrame {
    private final IPlayerManager playerManager;
    private final IWorldManager worldManager;
    private final GameManager gameManager;
    private BufferedImage colorMap;
    private JLabel boardLabel;

    public RiskBoard(IWorldManager worldManager, IPlayerManager playerManager, GameManager gameManager) {

        super("Risk Board");

        this.worldManager = worldManager;
        this.playerManager = playerManager;
        this.gameManager = gameManager;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 1000);

        BufferedImage boardImage = null;
        try {
            boardImage = ImageIO.read(new File("Data/RiskBoard.jpeg"));
            colorMap = ImageIO.read(new File("Data/PaintedMap.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        JPanel boardPanel = new JPanel();
        boardLabel = new JLabel(new ImageIcon(Objects.requireNonNull(boardImage)));
        boardPanel.add(boardLabel);

        boardLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                int x = e.getX();
                int y = e.getY();

                Color pixelColor = new Color(colorMap.getRGB(x, y));
                String selectedCountry = worldManager.getCountryNameByColor(pixelColor);

                System.out.println(selectedCountry);
            }
        });

        setLayout(new BorderLayout());
        add(boardPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    public static void main(String[] args) throws IOException {

        IPersistence persistence = new FilePersistence();

        IWorldManager worldManager = new World(persistence);
        IPlayerManager playerManager = new PlayerManager(worldManager, persistence);
        GameManager gameManager = new Game(playerManager, worldManager, persistence);

        new RiskBoard(worldManager, playerManager, gameManager);
    }

}
                                                                                    