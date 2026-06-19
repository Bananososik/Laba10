import greenfoot.Actor;
import greenfoot.Color;
import greenfoot.GreenfootImage;

public class Wall extends Actor {
    public enum Variant {
        TOP_LEFT("wall_top_left.png"),
        TOP("wall_top.png"),
        TOP_RIGHT("wall_top_right.png"),
        LEFT("wall_left.png"),
        CENTER("wall_center.png"),
        RIGHT("wall_right.png"),
        BOTTOM_LEFT("wall_bottom_left.png"),
        BOTTOM("wall_bottom.png"),
        BOTTOM_RIGHT("wall_bottom_right.png");

        private final String fileName;

        Variant(String fileName) {
            this.fileName = fileName;
        }
    }

    public Wall() {
        this(Variant.CENTER);
    }

    public Wall(Variant variant) {
        setImage(loadVariant(variant));
    }

    public static Variant chooseVariant(boolean north, boolean east, boolean south, boolean west) {
        int count = 0;
        if (north) count++;
        if (east) count++;
        if (south) count++;
        if (west) count++;

        if (count <= 1) {
            if (north) return Variant.BOTTOM;
            if (east) return Variant.LEFT;
            if (south) return Variant.TOP;
            if (west) return Variant.RIGHT;
            return Variant.CENTER;
        }

        if (south && east && !north && !west) return Variant.TOP_LEFT;
        if (south && west && !north && !east) return Variant.TOP_RIGHT;
        if (north && east && !south && !west) return Variant.BOTTOM_LEFT;
        if (north && west && !south && !east) return Variant.BOTTOM_RIGHT;

        if (!north && east && west && south) return Variant.TOP;
        if (!south && east && west && north) return Variant.BOTTOM;
        if (!west && north && south && east) return Variant.LEFT;
        if (!east && north && south && west) return Variant.RIGHT;

        return Variant.CENTER;
    }

    private GreenfootImage loadVariant(Variant variant) {
        Color fill = new Color(72, 78, 92);
        switch (variant) {
            case TOP_LEFT:
            case TOP_RIGHT:
            case BOTTOM_LEFT:
            case BOTTOM_RIGHT:
                fill = new Color(88, 94, 108);
                break;
            case TOP:
            case BOTTOM:
                fill = new Color(80, 86, 100);
                break;
            case LEFT:
            case RIGHT:
                fill = new Color(76, 82, 96);
                break;
            default:
                break;
        }

        return SpriteFactory.createBox(40, 40, fill, new Color(33, 36, 44));
    }
}