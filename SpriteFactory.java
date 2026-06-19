import greenfoot.Color;
import greenfoot.Greenfoot;
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

    public static GreenfootImage createSpaceBackground(int width, int height, int starCount) {
        GreenfootImage background = new GreenfootImage(width, height);
        for (int y = 0; y < height; y++) {
            int blend = (int) Math.round(16 + (y / (double) Math.max(1, height)) * 20);
            background.setColor(new Color(6 + blend / 4, 8 + blend / 5, 16 + blend));
            background.drawLine(0, y, width, y);
        }

        background.setColor(new Color(80, 40, 120, 35));
        background.fillOval(width - width / 4, -height / 6, width / 2, height / 2);
        background.setColor(new Color(30, 90, 180, 28));
        background.fillOval(-width / 8, height / 2, width / 3, height / 2);

        background.setColor(new Color(255, 255, 255));
        for (int i = 0; i < starCount; i++) {
            int x = Greenfoot.getRandomNumber(width);
            int y = Greenfoot.getRandomNumber(height);
            int size = 1 + Greenfoot.getRandomNumber(3);
            int alpha = 140 + Greenfoot.getRandomNumber(100);
            background.setColor(new Color(255, 255, 255, alpha));
            background.fillOval(x, y, size, size);
        }

        return background;
    }

    public static GreenfootImage createShipSprite(int size, Color body, Color accent, Color glow) {
        GreenfootImage image = new GreenfootImage(size, size);
        image.setColor(new Color(0, 0, 0, 0));
        image.clear();

        int cx = size / 2;
        int cy = size / 2;

        image.setColor(glow);
        image.fillOval(cx - size / 4, cy - size / 4, size / 2, size / 2);

        image.setColor(body);
        image.fillOval(cx - size / 5, cy - size / 3, size / 2, size * 2 / 3);
        image.fillRect(cx - size / 8, cy - size / 2, size / 4, size * 2 / 3);

        image.setColor(accent);
        image.fillOval(cx - size / 10, cy - size / 6, size / 5, size / 3);
        image.fillRect(cx - size / 18, cy - size / 2, size / 9, size / 2);

        image.setColor(new Color(255, 255, 255, 120));
        image.drawLine(cx, cy - size / 2 + 3, cx, cy + size / 3);

        return image;
    }

    public static GreenfootImage createDroneSprite(int size, Color body, Color core, Color aura) {
        GreenfootImage image = new GreenfootImage(size, size);
        image.setColor(new Color(0, 0, 0, 0));
        image.clear();

        image.setColor(aura);
        image.fillOval(size / 6, size / 6, size * 2 / 3, size * 2 / 3);
        image.setColor(body);
        image.fillOval(size / 4, size / 4, size / 2, size / 2);
        image.setColor(core);
        image.fillOval(size / 2 - size / 8, size / 2 - size / 8, size / 4, size / 4);

        image.setColor(new Color(255, 255, 255, 140));
        image.drawLine(size / 2, 2, size / 2, size - 3);
        image.drawLine(2, size / 2, size - 3, size / 2);

        return image;
    }

    public static GreenfootImage createButtonPanel(String title, String subtitle, int width, int height, Color fill, Color border) {
        GreenfootImage img = createBox(width, height, fill, border);
        img.setColor(new Color(255, 255, 255, 35));
        img.fillRect(4, 4, width - 8, height / 3);
        GreenfootImage titleText = new GreenfootImage(title, Math.max(20, height / 4), new Color(255, 255, 255), new Color(0, 0, 0, 0));
        GreenfootImage subtitleText = new GreenfootImage(subtitle, Math.max(14, height / 8), new Color(210, 220, 255), new Color(0, 0, 0, 0));
        img.drawImage(titleText, Math.max(8, (width - titleText.getWidth()) / 2), Math.max(12, height / 3 - 8));
        img.drawImage(subtitleText, Math.max(8, (width - subtitleText.getWidth()) / 2), Math.min(height - 28, height - 42));
        return img;
    }

    public static GreenfootImage createDeckFloorTile(int size, int seedX, int seedY) {
        GreenfootImage image = new GreenfootImage(size, size);
        int base = 18 + ((seedX * 13 + seedY * 17) % 10);
        image.setColor(new Color(base, base + 4, base + 8));
        image.fillRect(0, 0, size, size);

        image.setColor(new Color(28, 34, 46));
        image.drawRect(0, 0, size - 1, size - 1);
        image.setColor(new Color(42, 50, 66));
        image.drawLine(0, size / 2, size - 1, size / 2);
        image.drawLine(size / 2, 0, size / 2, size - 1);

        image.setColor(new Color(80, 120, 160, 80));
        image.fillRect(size / 6, size / 6, size / 3, size / 8);
        image.fillRect(size * 5 / 8, size * 5 / 8, size / 4, size / 8);

        image.setColor(new Color(255, 255, 255, 28));
        image.drawLine(2, 2, size - 3, 2);
        image.drawLine(2, 2, 2, size - 3);
        return image;
    }

    public static GreenfootImage createSpaceDeckBackground(int width, int height, int tileSize) {
        GreenfootImage background = new GreenfootImage(width, height);
        background.setColor(new Color(9, 12, 18));
        background.fillRect(0, 0, width, height);

        for (int y = 0; y < height; y += tileSize) {
            for (int x = 0; x < width; x += tileSize) {
                GreenfootImage tile = createDeckFloorTile(tileSize, x / tileSize, y / tileSize);
                background.drawImage(tile, x, y);
            }
        }

        background.setColor(new Color(255, 255, 255, 18));
        for (int i = 0; i < 20; i++) {
            int x = Greenfoot.getRandomNumber(width);
            int y = Greenfoot.getRandomNumber(height);
            background.fillOval(x, y, 2, 2);
        }

        return background;
    }

    public static GreenfootImage createWallSprite(int size, Color fill, Color border, Color highlight) {
        GreenfootImage image = new GreenfootImage(size, size);
        image.setColor(fill);
        image.fillRect(0, 0, size, size);

        image.setColor(border);
        image.drawRect(0, 0, size - 1, size - 1);
        image.setColor(highlight);
        image.fillRect(3, 3, size - 6, 4);
        image.fillRect(3, size - 7, size - 6, 3);
        image.drawLine(6, size / 2, size - 7, size / 2);

        image.setColor(new Color(255, 255, 255, 50));
        image.drawLine(1, 1, size - 2, 1);
        image.drawLine(1, 1, 1, size - 2);
        return image;
    }

    public static GreenfootImage createBossSprite(int size) {
        GreenfootImage image = new GreenfootImage(size, size);
        image.setColor(new Color(0, 0, 0, 0));
        image.clear();

        int cx = size / 2;
        int cy = size / 2;

        // menacing outer glow
        image.setColor(new Color(255, 70, 40, 60));
        image.fillOval(0, 0, size, size);
        // armored shell
        image.setColor(new Color(70, 20, 30));
        image.fillOval(size / 10, size / 10, size - size / 5, size - size / 5);
        image.setColor(new Color(140, 30, 40));
        image.fillOval(size / 6, size / 6, size - size / 3, size - size / 3);
        // plating ridges
        image.setColor(new Color(40, 12, 18));
        for (int i = 0; i < 8; i++) {
            double a = Math.PI * 2 * i / 8.0;
            int x1 = (int) Math.round(cx + Math.cos(a) * size / 6.0);
            int y1 = (int) Math.round(cy + Math.sin(a) * size / 6.0);
            int x2 = (int) Math.round(cx + Math.cos(a) * size / 2.4);
            int y2 = (int) Math.round(cy + Math.sin(a) * size / 2.4);
            image.drawLine(x1, y1, x2, y2);
        }
        // glowing core eye
        image.setColor(new Color(255, 220, 120));
        image.fillOval(cx - size / 8, cy - size / 8, size / 4, size / 4);
        image.setColor(new Color(255, 255, 255, 200));
        image.fillOval(cx - size / 16, cy - size / 16, size / 8, size / 8);
        // spikes
        image.setColor(new Color(200, 60, 70));
        for (int i = 0; i < 4; i++) {
            double a = Math.PI / 4 + Math.PI / 2 * i;
            int sx = (int) Math.round(cx + Math.cos(a) * size / 2.2);
            int sy = (int) Math.round(cy + Math.sin(a) * size / 2.2);
            int tx = (int) Math.round(cx + Math.cos(a) * size / 1.9);
            int ty = (int) Math.round(cy + Math.sin(a) * size / 1.9);
            image.drawLine(sx, sy, tx, ty);
        }
        return image;
    }

    public static GreenfootImage createPickupSprite(int size, String kind) {
        GreenfootImage image = new GreenfootImage(size, size);
        image.setColor(new Color(0, 0, 0, 0));
        image.clear();

        int cx = size / 2;
        int cy = size / 2;
        Color glow;
        Color core;
        if ("shield".equals(kind)) {
            glow = new Color(90, 170, 255, 90);
            core = new Color(120, 200, 255);
        } else if ("xp".equals(kind)) {
            glow = new Color(180, 120, 255, 90);
            core = new Color(200, 150, 255);
        } else { // heal
            glow = new Color(80, 230, 120, 90);
            core = new Color(120, 240, 150);
        }

        image.setColor(glow);
        image.fillOval(0, 0, size, size);
        image.setColor(core);
        image.fillOval(size / 5, size / 5, size - 2 * size / 5, size - 2 * size / 5);
        image.setColor(new Color(255, 255, 255, 230));

        if ("shield".equals(kind)) {
            // small shield arc icon
            image.drawOval(cx - size / 5, cy - size / 5, 2 * size / 5, 2 * size / 5);
            image.drawLine(cx, cy - size / 5, cx, cy + size / 5);
        } else if ("xp".equals(kind)) {
            // star-ish burst
            image.drawLine(cx, cy - size / 4, cx, cy + size / 4);
            image.drawLine(cx - size / 4, cy, cx + size / 4, cy);
            image.drawLine(cx - size / 6, cy - size / 6, cx + size / 6, cy + size / 6);
            image.drawLine(cx - size / 6, cy + size / 6, cx + size / 6, cy - size / 6);
        } else {
            // plus / cross for heal
            image.fillRect(cx - size / 12, cy - size / 4, size / 6, size / 2);
            image.fillRect(cx - size / 4, cy - size / 12, size / 2, size / 6);
        }
        return image;
    }

    public static GreenfootImage createAlienEnemySprite(int size, boolean ranged) {
        GreenfootImage image = new GreenfootImage(size, size);
        image.setColor(new Color(0, 0, 0, 0));
        image.clear();

        if (ranged) {
            image.setColor(new Color(175, 110, 255, 70));
            image.fillOval(2, 2, size - 4, size - 4);
            image.setColor(new Color(77, 37, 130));
            image.fillOval(5, 5, size - 10, size - 10);
            image.setColor(new Color(230, 200, 255));
            image.fillOval(size / 2 - 4, size / 2 - 4, 8, 8);
            image.setColor(new Color(255, 255, 255, 120));
            image.drawLine(size / 2, 4, size / 2, size - 5);
            image.drawLine(4, size / 2, size - 5, size / 2);
            image.setColor(new Color(220, 180, 255));
            image.drawLine(size / 2, 0, size / 2 - 7, 7);
            image.drawLine(size / 2, 0, size / 2 + 7, 7);
            image.drawLine(size / 2, size - 1, size / 2 - 7, size - 8);
            image.drawLine(size / 2, size - 1, size / 2 + 7, size - 8);
        } else {
            image.setColor(new Color(255, 60, 90, 70));
            image.fillOval(1, 1, size - 2, size - 2);
            image.setColor(new Color(120, 18, 34));
            image.fillOval(5, 5, size - 10, size - 10);
            image.setColor(new Color(255, 185, 60));
            image.fillOval(size / 2 - 5, size / 2 - 5, 10, 10);
            image.setColor(new Color(255, 90, 120));
            image.drawLine(size / 2, 2, size / 2 - 10, 10);
            image.drawLine(size / 2, 2, size / 2 + 10, 10);
            image.drawLine(size / 2, size - 3, size / 2 - 10, size - 12);
            image.drawLine(size / 2, size - 3, size / 2 + 10, size - 12);
        }

        return image;
    }
}