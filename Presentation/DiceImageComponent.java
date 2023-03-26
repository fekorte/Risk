package Presentation;

import java.awt.*;

public class DiceImageComponent extends Component {

    private Image diceImage;
    public DiceImageComponent() {}

    public void setDiceImage(Image diceImage) {
        this.diceImage = diceImage;
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        if (diceImage != null) {
            g.drawImage(diceImage, 0, 0, null);
        }
    }
}
