package Presentation;

import Business.*;
import Persistence.FilePersistence;
import Persistence.IPersistence;

import java.io.IOException;

public class RiskGame {

    public static void main(String[] args) throws IOException {

        IPersistence persistence = new FilePersistence();

        IWorldManager worldManager = new World(persistence);
        IPlayerManager playerManager = new PlayerManager(worldManager, persistence);
        GameManager gameManager = new Game(playerManager, worldManager, persistence);

        RiskCUI cui;
        try {
            cui = new RiskCUI(worldManager, playerManager, gameManager);
            cui.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
