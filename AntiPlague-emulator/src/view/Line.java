package view;

import java.awt.*;

public class Line {
    private Rectangle startBounds;
    private Rectangle endBounds;

    public Line(Rectangle startBounds, Rectangle endBounds) {
        this.startBounds = startBounds;
        this.endBounds = endBounds;
    }

    public Rectangle getStartBounds() {
        return startBounds;
    }

    public Rectangle getEndBounds() {
        return endBounds;
    }
}
