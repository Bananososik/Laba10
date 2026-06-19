import greenfoot.Color;
import greenfoot.GreenfootImage;

public final class SpriteFactory {
    private SpriteFactory() {
    }

    public static GreenfootImage createBox(int width, int height, Color fill, Color border) {
        GreenfootImage image = new GreenfootImage(width, height);
        image.setColor(fill);
        image.fillRect(0, 0, width, height);
        image.setColor(border);
        image.drawRect(0, 0, width - 1, height - 1);
        return image;
    }

    public static GreenfootImage createCircle(int size, Color fill, Color border) {
        GreenfootImage image = new GreenfootImage(size, size);
        image.setColor(new Color(0, 0, 0, 0));
        image.clear();
        image.setColor(fill);
        image.fillOval(0, 0, size, size);
        image.setColor(border);
        image.drawOval(0, 0, size - 1, size - 1);
        return image;
    }

    public static GreenfootImage createTextPanel(String text, int width, int height, Color fill, Color border, Color textColor) {
        GreenfootImage image = createBox(width, height, fill, border);
        image.setColor(textColor);
        image.drawString(text, 8, height / 2 + 5);
        return image;
    }

    public static GreenfootImage loadOrPlaceholder(String assetPath, int width, int height, GreenfootImage placeholder) {
        try {
            GreenfootImage img = new GreenfootImage(assetPath);
            if (width > 0 && height > 0) img.scale(width, height);
            return img;
        } catch (Throwable t) {
            // asset not found or failed to load - return placeholder
            if (placeholder != null) {
                if (width > 0 && height > 0) placeholder.scale(width, height);
                return placeholder;
            }
            return createBox(Math.max(16, width), Math.max(16, height), new Color(100,100,100), new Color(40,40,40));
        }
    }

    public static GreenfootImage loadAny(String[] assetPaths, int width, int height, GreenfootImage placeholder) {
        if (assetPaths == null) {
            return placeholder;
        }

        for (String assetPath : assetPaths) {
            try {
                GreenfootImage img = new GreenfootImage(assetPath);
                if (width > 0 && height > 0) {
                    img.scale(width, height);
                }
                return img;
            } catch (Throwable t) {
                // try next path
            }
        }

        return placeholder;
    }

    public static GreenfootImage[][] sliceGrid(String assetPath, int cols, int rows) {
        try {
            GreenfootImage sheet = new GreenfootImage(assetPath);
            int cellW = sheet.getWidth() / cols;
            int cellH = sheet.getHeight() / rows;
            GreenfootImage[][] out = new GreenfootImage[rows][cols];
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    GreenfootImage cell = new GreenfootImage(cellW, cellH);
                    // draw the sheet shifted so desired cell appears at 0,0
                    cell.drawImage(sheet, -c * cellW, -r * cellH);
                    out[r][c] = cell;
                }
            }
            return out;
        } catch (Throwable t) {
            return null;
        }
    }
}