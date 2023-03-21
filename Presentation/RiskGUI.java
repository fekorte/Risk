package Presentation;

import Business.*;
import Persistence.FilePersistence;
import Persistence.IPersistence;

import java.io.IOException;

public class RiskGUI {

    public RiskGUI(IWorldManager worldManager, IPlayerManager playerManager, IGameManager gameManager){

        StartView sView = new StartView(worldManager, playerManager, gameManager);
    }

    public static void main(String[] args) throws IOException {

        IPersistence persistence = new FilePersistence();

        IWorldManager worldManager = new WorldManager(persistence);
        IPlayerManager playerManager = new PlayerManager(worldManager, persistence);
        IGameManager gameManager = new GameManager(playerManager, worldManager, persistence);

        new RiskGUI(worldManager, playerManager, gameManager);
    }
}
