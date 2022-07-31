package edu.school21.tanks.client.gameobjects;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Projectile extends ImageView {
    private Projectile() {
    }

    private double diffX;
    private double diffY;

    public Projectile(String projectileSpriteFile) {
        Image projectileImage = new Image(projectileSpriteFile);
        this.setImage(projectileImage);
        this.diffX = projectileImage.getWidth() / 2;
        this.diffY = projectileImage.getHeight() / 2;
    }

    public final void setXPosition(int xPosition) {
        this.setLayoutX(xPosition - this.diffX);
    }

    public final void setYPosition(int yPosition) {
        this.setLayoutY(yPosition - this.diffY);
    }
    public Projectile clone() {
        Projectile clone = new Projectile();
        clone.diffX = this.diffX;
        clone.diffY = this.diffY;
        clone.setImage(this.getImage());
        return clone;
    }
}
