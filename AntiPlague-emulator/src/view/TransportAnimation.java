package view;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TransportAnimation {
    private BufferedImage transportImage;
    private Rectangle startBounds;
    private Rectangle endBounds;
    private Point currentPos;
    private Point targetPos;
    private boolean isCompleted = false;
    private boolean isInfected;

    public TransportAnimation(BufferedImage transportImage, Rectangle startBounds, Rectangle endBounds, boolean isInfected) {
        this.transportImage = transportImage;
        this.startBounds = startBounds;
        this.endBounds = endBounds;
        this.currentPos = new Point(startBounds.x, startBounds.y);
        this.targetPos = new Point(endBounds.x, endBounds.y);
        this.isInfected = isInfected;
    }

    public void update() {
        if (isCompleted) return;

        int deltaX = targetPos.x - currentPos.x;
        int deltaY = targetPos.y - currentPos.y;

        int speed = 20;
        if (Math.abs(deltaX) < speed && Math.abs(deltaY) < speed) {
            currentPos = new Point(targetPos.x, targetPos.y);
            isCompleted = true;
        } else {
            currentPos.x += Math.signum(deltaX) * speed;
            currentPos.y += Math.signum(deltaY) * speed;
        }
    }

    public void draw(Graphics g) {
        if (!isCompleted) {
            g.drawImage(transportImage, currentPos.x, currentPos.y, null);
        }
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public Rectangle getBounds() {
        return new Rectangle(currentPos.x, currentPos.y, transportImage.getWidth(), transportImage.getHeight());
    }

    public boolean isInfected() {
        return isInfected;
    }
}

