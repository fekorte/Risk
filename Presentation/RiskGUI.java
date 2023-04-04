package Presentation;

import Business.*;
import Persistence.FilePersistence;
import Persistence.IPersistence;

import java.io.IOException;

public class RiskGUI {
    public static void main(String[] args) throws IOException {

        IWorldManager worldManager = new WorldManager();
        IPlayerManager playerManager = new PlayerManager(worldManager);
        IGameManager gameManager = new GameManager(playerManager, worldManager);

        StartView sView = new StartView(worldManager, playerManager, gameManager);
    }
}
