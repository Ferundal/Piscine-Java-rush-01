package edu.school21.tanks.client.gameobjects;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
public class Tank extends ImageView {
    private Tank() {}
    private double diffX;
    private double diffY;
    private int heals;
    {
        heals = 100;
    }
    public Tank(String tankSpriteFile) {
        Image tankImage = new Image(tankSpriteFile);
        this.setImage(tankImage);
        this.diffX = tankImage.getWidth() / 2;
        this.diffY = tankImage.getHeight() / 2; //tankImage.getHeight() / 2;
    }

    public final void setXPosition(int xPosition) {
        this.setLayoutX(xPosition - this.diffX);
    }
    public final void setYPosition(int yPosition) {
        this.setLayoutY(yPosition - this.diffY);
    }
    public final void getDamage(int damage) {
        this.heals -=damage;
        if (this.heals < 0)
            this.heals = 0;
    }

    public int getHeals() {
        return heals;
    }
}
