package Presentation;

import Business.*;
import Persistence.FilePersistence;
import Persistence.IPersistence;

import java.io.IOException;

public class RiskGUI {

    public RiskGUI(IWorldManager worldManager, IPlayerManager playerManager, GameManager gameManager) throws IOException {

        RiskView rView = new RiskView(worldManager, playerManager, gameManager);
    }

    public static void main(String[] args) throws IOException {

        IPersistence persistence = new FilePersistence();

        IWorldManager worldManager = new World(persistence);
        IPlayerManager playerManager = new PlayerManager(worldManager, persistence);
        GameManager gameManager = new Game(playerManager, worldManager, persistence);

        new RiskGUI(worldManager, playerManager, gameManager);
    }
}
