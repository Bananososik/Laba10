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
        int h = (seedX * 928371 + seedY * 1237) & 0x7fffffff; // детерминированный «хэш» клетки
        int base = 22 + (h % 8);
        image.setColor(new Color(base, base + 5, base + 11));
        image.fillRect(0, 0, size, size);

        // швы панели обшивки
        image.setColor(new Color(34, 40, 54));
        image.drawRect(0, 0, size - 1, size - 1);
        image.setColor(new Color(16, 20, 28));
        image.drawLine(0, 0, size - 1, 0);
        image.drawLine(0, 0, 0, size - 1);

        // заклёпки по углам
        image.setColor(new Color(60, 70, 90));
        image.fillOval(3, 3, 3, 3);
        image.fillOval(size - 6, 3, 3, 3);
        image.fillOval(3, size - 6, 3, 3);
        image.fillOval(size - 6, size - 6, 3, 3);

        // редкие палубные элементы — зависят от «хэша» клетки
        int feature = h % 7;
        if (feature == 0) {
            // люк/гермодверь
            image.setColor(new Color(46, 54, 70));
            image.fillRect(size / 4, size / 4, size / 2, size / 2);
            image.setColor(new Color(70, 82, 104));
            image.drawRect(size / 4, size / 4, size / 2, size / 2);
            image.drawLine(size / 2, size / 4, size / 2, size * 3 / 4);
            image.setColor(new Color(90, 150, 200, 120));
            image.fillOval(size / 2 - 3, size / 2 - 3, 6, 6);
        } else if (feature == 1) {
            // вентиляционная решётка
            image.setColor(new Color(40, 48, 62));
            image.fillRect(size / 5, size / 5, size * 3 / 5, size * 3 / 5);
            image.setColor(new Color(20, 26, 36));
            for (int i = 0; i < 4; i++) {
                int yy = size / 5 + 2 + i * (size * 3 / 5) / 4;
                image.drawLine(size / 5 + 1, yy, size / 5 + size * 3 / 5 - 1, yy);
            }
        } else if (feature == 2) {
            // светящаяся энергетическая жила
            image.setColor(new Color(60, 150, 210, 150));
            image.fillRect(size / 2 - 1, 0, 3, size);
            image.setColor(new Color(120, 210, 255, 110));
            image.fillRect(size / 2, 0, 1, size);
        } else if (feature == 3) {
            // поперечная жила
            image.setColor(new Color(60, 150, 210, 150));
            image.fillRect(0, size / 2 - 1, size, 3);
            image.setColor(new Color(120, 210, 255, 110));
            image.fillRect(0, size / 2, size, 1);
        } else if (feature == 4) {
            // предупреждающая разметка (диагональные полосы)
            image.setColor(new Color(200, 170, 60, 70));
            for (int i = -size; i < size; i += 8) {
                image.drawLine(i, size, i + size, 0);
            }
        }

        // мягкий блик сверху-слева
        image.setColor(new Color(255, 255, 255, 16));
        image.drawLine(1, 1, size - 2, 1);
        image.drawLine(1, 1, 1, size - 2);
        return image;
    }

    /**
     * Стена в стиле обшивки космического корабля. Металлические панели,
     * заклёпки, рёбра жёсткости и подсветка швов. Вариант влияет на акцент грани.
     */
    public static GreenfootImage createHullWall(int size, Wall.Variant variant, int seedX, int seedY) {
        GreenfootImage img = new GreenfootImage(size, size);
        int h = (seedX * 73856093 ^ seedY * 19349663) & 0x7fffffff;

        // базовый металл
        int b = 70 + (h % 12);
        img.setColor(new Color(b, b + 6, b + 16));
        img.fillRect(0, 0, size, size);

        // вертикальный градиент-объём
        img.setColor(new Color(255, 255, 255, 22));
        img.fillRect(0, 0, size, size / 3);
        img.setColor(new Color(0, 0, 0, 55));
        img.fillRect(0, size * 2 / 3, size, size / 3);

        // внутренняя панель с фаской
        img.setColor(new Color(b + 14, b + 22, b + 34));
        img.fillRect(4, 4, size - 8, size - 8);
        img.setColor(new Color(255, 255, 255, 40));
        img.drawLine(4, 4, size - 5, 4);
        img.drawLine(4, 4, 4, size - 5);
        img.setColor(new Color(0, 0, 0, 70));
        img.drawLine(5, size - 5, size - 5, size - 5);
        img.drawLine(size - 5, 5, size - 5, size - 5);

        // заклёпки
        img.setColor(new Color(35, 40, 52));
        int r = Math.max(2, size / 16);
        int[] px = {7, size - 7 - r, 7, size - 7 - r};
        int[] py = {7, 7, size - 7 - r, size - 7 - r};
        for (int i = 0; i < 4; i++) {
            img.fillOval(px[i], py[i], r, r);
            img.setColor(new Color(120, 130, 150, 120));
            img.drawOval(px[i], py[i], r, r);
            img.setColor(new Color(35, 40, 52));
        }

        // ребро жёсткости по центру
        img.setColor(new Color(40, 46, 60));
        img.fillRect(size / 2 - 1, 8, 3, size - 16);

        // акцентная подсветка грани по варианту (внешний контур комнаты)
        Color neon = new Color(70, 150, 220, 180);
        img.setColor(neon);
        switch (variant) {
            case TOP: case TOP_LEFT: case TOP_RIGHT:
                img.fillRect(2, 2, size - 4, 2);
                break;
            case BOTTOM: case BOTTOM_LEFT: case BOTTOM_RIGHT:
                img.fillRect(2, size - 4, size - 4, 2);
                break;
            case LEFT:
                img.fillRect(2, 2, 2, size - 4);
                break;
            case RIGHT:
                img.fillRect(size - 4, 2, 2, size - 4);
                break;
            default:
                break;
        }

        img.setColor(new Color(12, 14, 20));
        img.drawRect(0, 0, size - 1, size - 1);
        return img;
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

    /** Спрайт снаряда игрока. Вид зависит от оружия (ТЗ, раздел 2). */
    public static GreenfootImage createBulletSprite(String kind) {
        switch (kind) {
            case "laser": {
                // продолговатый прожигающий луч
                GreenfootImage img = new GreenfootImage(26, 10);
                img.setColor(new Color(255, 80, 80, 90));
                img.fillOval(0, 0, 26, 10);
                img.setColor(new Color(255, 120, 120));
                img.fillRect(2, 3, 22, 4);
                img.setColor(new Color(255, 255, 255));
                img.fillRect(4, 4, 18, 2);
                return img;
            }
            case "nova": {
                GreenfootImage img = new GreenfootImage(14, 14);
                img.setColor(new Color(150, 120, 255, 120));
                img.fillOval(0, 0, 14, 14);
                img.setColor(new Color(200, 180, 255));
                img.fillOval(3, 3, 8, 8);
                img.setColor(new Color(255, 255, 255));
                img.fillOval(5, 5, 4, 4);
                return img;
            }
            case "drone": {
                GreenfootImage img = new GreenfootImage(10, 10);
                img.setColor(new Color(120, 230, 255, 130));
                img.fillOval(0, 0, 10, 10);
                img.setColor(new Color(180, 245, 255));
                img.fillOval(2, 2, 6, 6);
                return img;
            }
            case "chain": {
                GreenfootImage img = new GreenfootImage(12, 12);
                img.setColor(new Color(255, 130, 200, 120));
                img.fillOval(0, 0, 12, 12);
                img.setColor(new Color(255, 190, 230));
                img.fillOval(3, 3, 6, 6);
                return img;
            }
            default: {
                GreenfootImage img = createCircle(10, new Color(255, 220, 80), new Color(120, 80, 0));
                return img;
            }
        }
    }

    /**
     * Спрайт игрока — космический корабль с читаемым силуэтом (ТЗ, раздел 9).
     * Нос направлен вправо (0 рад); поворот делает сам актёр через setRotation.
     * frame — фаза анимации двигателя; thrust — есть ли тяга (пламя длиннее);
     * shoot — подсветка пушки при стрельбе; shield — рисует орбитальное кольцо щита.
     */
    public static GreenfootImage createPlayerShip(int size, int frame, boolean thrust, boolean shoot, boolean shield) {
        GreenfootImage img = new GreenfootImage(size, size);
        img.setColor(new Color(0, 0, 0, 0));
        img.clear();

        int cx = size / 2;
        int cy = size / 2;

        // --- пламя двигателя (сзади, слева от центра) ---
        int flick = (frame % 2 == 0) ? 0 : 2;
        int flameLen = (thrust ? size / 3 : size / 6) + flick;
        // внешнее пламя
        int[] fxOuter = { cx - size / 5, cx - size / 5 - flameLen, cx - size / 5 };
        int[] fyOuter = { cy - size / 8, cy, cy + size / 8 };
        img.setColor(new Color(255, 140, 40, 200));
        img.fillPolygon(fxOuter, fyOuter, 3);
        // ядро пламени
        int[] fxIn = { cx - size / 5, cx - size / 5 - flameLen * 2 / 3, cx - size / 5 };
        int[] fyIn = { cy - size / 14, cy, cy + size / 14 };
        img.setColor(new Color(255, 230, 150, 230));
        img.fillPolygon(fxIn, fyIn, 3);

        // --- корпус (вытянутый клин носом вправо) ---
        int[] hullX = { cx + size / 2 - 2, cx - size / 4, cx - size / 5, cx - size / 4 };
        int[] hullY = { cy, cy - size / 4, cy, cy + size / 4 };
        img.setColor(new Color(70, 150, 210));
        img.fillPolygon(hullX, hullY, 4);
        // светлая обшивка сверху
        img.setColor(new Color(120, 200, 255));
        int[] topX = { cx + size / 2 - 2, cx - size / 5, cx - size / 6 };
        int[] topY = { cy, cy - size / 6, cy };
        img.fillPolygon(topX, topY, 3);

        // крылья
        img.setColor(new Color(45, 105, 160));
        int[] wxT = { cx - size / 6, cx - size / 3, cx - size / 10 };
        int[] wyT = { cy - size / 8, cy - size / 2 + 2, cy - size / 8 };
        img.fillPolygon(wxT, wyT, 3);
        int[] wxB = { cx - size / 6, cx - size / 3, cx - size / 10 };
        int[] wyB = { cy + size / 8, cy + size / 2 - 2, cy + size / 8 };
        img.fillPolygon(wxB, wyB, 3);

        // кабина-ядро
        img.setColor(shoot ? new Color(255, 245, 180) : new Color(180, 240, 255));
        img.fillOval(cx + size / 12 - 4, cy - 4, 9, 9);
        img.setColor(new Color(255, 255, 255, 160));
        img.fillOval(cx + size / 12 - 2, cy - 3, 4, 4);

        // контур носа
        img.setColor(new Color(20, 40, 70, 180));
        img.drawLine(cx + size / 2 - 2, cy, cx - size / 4, cy - size / 4);
        img.drawLine(cx + size / 2 - 2, cy, cx - size / 4, cy + size / 4);

        // --- щит: вращающееся кольцо ---
        if (shield) {
            img.setColor(new Color(120, 200, 255, 70));
            img.fillOval(2, 2, size - 4, size - 4);
            img.setColor(new Color(160, 220, 255, 200));
            img.drawOval(1, 1, size - 3, size - 3);
            img.drawOval(3, 3, size - 7, size - 7);
            // искры на кольце
            for (int i = 0; i < 3; i++) {
                double a = Math.toRadians(frame * 12 + i * 120);
                int sxp = (int) Math.round(cx + Math.cos(a) * (size / 2 - 3));
                int syp = (int) Math.round(cy + Math.sin(a) * (size / 2 - 3));
                img.setColor(new Color(220, 245, 255));
                img.fillOval(sxp - 2, syp - 2, 4, 4);
            }
        }

        return img;
    }

    // ----- Цвета редкости (по ТЗ, раздел 7) -----
    public static Color rarityColor(String rarity) {
        switch (rarity) {
            case "UNCOMMON": return new Color(80, 210, 110);   // зелёный
            case "RARE":     return new Color(90, 160, 255);   // синий
            case "EPIC":     return new Color(180, 110, 255);  // фиолетовый
            case "LEGENDARY":return new Color(255, 200, 70);   // золотой
            case "SPECIAL":  return new Color(255, 120, 200);  // особый — розовый неон
            default:         return new Color(170, 175, 185);  // серый (common)
        }
    }

    /** Мягкое свечение-ореол под карточку/иконку по цвету редкости. */
    public static GreenfootImage createRarityGlow(int width, int height, String rarity) {
        GreenfootImage img = new GreenfootImage(width, height);
        Color c = rarityColor(rarity);
        for (int i = 0; i < 5; i++) {
            int inset = i * 3;
            int a = 18 + i * 14;
            img.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), a));
            img.drawRect(inset, inset, width - 1 - inset * 2, height - 1 - inset * 2);
        }
        return img;
    }

    /**
     * Процедурная иконка апгрейда. Узнаваемый символ + тематический цвет.
     * Каждому id из UpgradeSystem соответствует свой рисунок.
     */
    public static GreenfootImage createUpgradeIcon(String id, int size) {
        GreenfootImage img = new GreenfootImage(size, size);
        img.setColor(new Color(0, 0, 0, 0));
        img.clear();

        int cx = size / 2;
        int cy = size / 2;
        Color accent = iconAccent(id);

        // фоновая шайба
        img.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 45));
        img.fillOval(0, 0, size - 1, size - 1);
        img.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 160));
        img.drawOval(1, 1, size - 3, size - 3);
        img.setColor(accent);

        switch (id) {
            case "rapid_burst": // три вертикальных штриха = скорострельность
                img.fillRect(cx - size / 4, cy - size / 4, size / 12 + 1, size / 2);
                img.fillRect(cx - size / 16, cy - size / 4, size / 12 + 1, size / 2);
                img.fillRect(cx + size / 8, cy - size / 4, size / 12 + 1, size / 2);
                break;
            case "scatter_shot": // веер из трёх лучей
                img.drawLine(cx, cy + size / 4, cx, cy - size / 4);
                img.drawLine(cx, cy + size / 4, cx - size / 4, cy - size / 5);
                img.drawLine(cx, cy + size / 4, cx + size / 4, cy - size / 5);
                break;
            case "drone_orbit": // спутник на орбите
                img.drawOval(cx - size / 4, cy - size / 4, size / 2, size / 2);
                img.fillOval(cx - 3, cy - 3, 6, 6);
                img.fillOval(cx + size / 4 - 3, cy - 3, 6, 6);
                break;
            case "nova_pulse": // 8 лучей из центра
                for (int i = 0; i < 8; i++) {
                    double a = Math.PI * 2 * i / 8.0;
                    img.drawLine(cx, cy, (int)(cx + Math.cos(a) * size / 3), (int)(cy + Math.sin(a) * size / 3));
                }
                img.fillOval(cx - 3, cy - 3, 6, 6);
                break;
            case "laser_array": // толстый луч
                img.fillRect(cx - size / 16, cy - size / 3, size / 8, size * 2 / 3);
                img.setColor(new Color(255, 255, 255, 180));
                img.drawLine(cx, cy - size / 3, cx, cy + size / 3);
                break;
            case "damage_core": // меч/остриё
                img.fillOval(cx - size / 6, cy - size / 6, size / 3, size / 3);
                img.setColor(new Color(255, 255, 255, 160));
                img.drawLine(cx, cy - size / 4, cx, cy + size / 4);
                break;
            case "crit_chance": // прицел
                img.drawOval(cx - size / 4, cy - size / 4, size / 2, size / 2);
                img.drawLine(cx, cy - size / 3, cx, cy - size / 6);
                img.drawLine(cx, cy + size / 6, cx, cy + size / 3);
                img.drawLine(cx - size / 3, cy, cx - size / 6, cy);
                img.drawLine(cx + size / 6, cy, cx + size / 3, cy);
                break;
            case "crit_damage": // взрыв-звезда
                for (int i = 0; i < 6; i++) {
                    double a = Math.PI * 2 * i / 6.0;
                    img.drawLine(cx, cy, (int)(cx + Math.cos(a) * size / 3), (int)(cy + Math.sin(a) * size / 3));
                }
                break;
            case "bullet_speed": // стрелка вправо
                img.drawLine(cx - size / 4, cy, cx + size / 4, cy);
                img.drawLine(cx + size / 4, cy, cx + size / 12, cy - size / 6);
                img.drawLine(cx + size / 4, cy, cx + size / 12, cy + size / 6);
                break;
            case "range_boost": // концентрические дуги
                img.drawOval(cx - size / 5, cy - size / 5, 2 * size / 5, 2 * size / 5);
                img.drawOval(cx - size / 3, cy - size / 3, 2 * size / 3, 2 * size / 3);
                break;
            case "regen": // крест-плюс
                img.fillRect(cx - size / 12, cy - size / 4, size / 6, size / 2);
                img.fillRect(cx - size / 4, cy - size / 12, size / 2, size / 6);
                break;
            case "shield": // щит-контур
                img.drawLine(cx - size / 4, cy - size / 5, cx, cy - size / 3);
                img.drawLine(cx + size / 4, cy - size / 5, cx, cy - size / 3);
                img.drawLine(cx - size / 4, cy - size / 5, cx, cy + size / 3);
                img.drawLine(cx + size / 4, cy - size / 5, cx, cy + size / 3);
                break;
            case "dodge": // двойная стрелка-уклон
                img.drawLine(cx - size / 4, cy + size / 5, cx, cy - size / 5);
                img.drawLine(cx, cy + size / 5, cx + size / 4, cy - size / 5);
                break;
            case "xp_amp": // ромб опыта
                img.drawLine(cx, cy - size / 4, cx + size / 4, cy);
                img.drawLine(cx + size / 4, cy, cx, cy + size / 4);
                img.drawLine(cx, cy + size / 4, cx - size / 4, cy);
                img.drawLine(cx - size / 4, cy, cx, cy - size / 4);
                break;
            case "luck": // четырёхлистник/клевер
                img.fillOval(cx - size / 5, cy - size / 5, size / 5, size / 5);
                img.fillOval(cx, cy - size / 5, size / 5, size / 5);
                img.fillOval(cx - size / 5, cy, size / 5, size / 5);
                img.fillOval(cx, cy, size / 5, size / 5);
                break;
            case "pierce": // стрела насквозь
                img.drawLine(cx - size / 3, cy, cx + size / 3, cy);
                img.drawLine(cx + size / 3, cy, cx + size / 8, cy - size / 8);
                img.drawLine(cx + size / 3, cy, cx + size / 8, cy + size / 8);
                img.fillOval(cx - size / 3, cy - 2, 4, 4);
                break;
            case "ricochet": // ломаная отражения
                img.drawLine(cx - size / 4, cy - size / 4, cx, cy + size / 6);
                img.drawLine(cx, cy + size / 6, cx + size / 4, cy - size / 4);
                break;
            case "low_hp_berserk": // череп-ярость (треугольник вниз)
                img.drawLine(cx - size / 4, cy - size / 5, cx + size / 4, cy - size / 5);
                img.drawLine(cx - size / 4, cy - size / 5, cx, cy + size / 4);
                img.drawLine(cx + size / 4, cy - size / 5, cx, cy + size / 4);
                break;
            case "kill_wave": // расходящаяся волна (две дуги из отрезков)
                drawArcLines(img, cx, cy, size / 3, Math.toRadians(200), Math.toRadians(340));
                drawArcLines(img, cx, cy, size / 5, Math.toRadians(200), Math.toRadians(340));
                break;
            case "homing": // мишень со стрелкой
                img.drawOval(cx - size / 5, cy - size / 5, 2 * size / 5, 2 * size / 5);
                img.fillOval(cx - 3, cy - 3, 6, 6);
                img.drawLine(cx, cy, cx + size / 3, cy - size / 3);
                break;
            case "chain_bullets": // цепь-звенья
                img.drawOval(cx - size / 3, cy - size / 8, size / 4, size / 4);
                img.drawOval(cx, cy - size / 8, size / 4, size / 4);
                break;
            case "vampirism": // капля
                img.fillOval(cx - size / 6, cy - size / 8, size / 3, size / 3);
                img.drawLine(cx, cy - size / 3, cx - size / 6, cy + size / 12);
                img.drawLine(cx, cy - size / 3, cx + size / 6, cy + size / 12);
                break;
            case "chaos_drive": // спираль хаоса
                for (int i = 0; i < 10; i++) {
                    double a = i * 0.7;
                    double r = i * size / 28.0;
                    img.fillOval((int)(cx + Math.cos(a) * r) - 1, (int)(cy + Math.sin(a) * r) - 1, 3, 3);
                }
                break;
            default: // запасной — знак вопроса/точка
                img.fillOval(cx - size / 8, cy - size / 8, size / 4, size / 4);
                break;
        }

        // блик
        img.setColor(new Color(255, 255, 255, 60));
        drawArcLines(img, cx, cy, size / 2 - 3, Math.toRadians(205), Math.toRadians(285));
        return img;
    }

    /** Рисует дугу окружности отрезками (drawArc в этой версии Greenfoot отсутствует). */
    private static void drawArcLines(GreenfootImage img, int cx, int cy, int r, double a0, double a1) {
        int steps = 14;
        int prevX = (int) Math.round(cx + Math.cos(a0) * r);
        int prevY = (int) Math.round(cy + Math.sin(a0) * r);
        for (int i = 1; i <= steps; i++) {
            double a = a0 + (a1 - a0) * i / steps;
            int x = (int) Math.round(cx + Math.cos(a) * r);
            int y = (int) Math.round(cy + Math.sin(a) * r);
            img.drawLine(prevX, prevY, x, y);
            prevX = x;
            prevY = y;
        }
    }

    private static Color iconAccent(String id) {
        switch (id) {
            case "rapid_burst": case "scatter_shot": case "damage_core":
            case "low_hp_berserk": case "crit_damage":
                return new Color(255, 110, 90);   // боевой красный
            case "drone_orbit": case "nova_pulse": case "laser_array":
            case "kill_wave": case "homing":
                return new Color(120, 200, 255);  // энергетический голубой
            case "shield": case "regen": case "dodge": case "vampirism":
                return new Color(110, 230, 150);  // защитный зелёный
            case "crit_chance": case "bullet_speed": case "range_boost":
            case "pierce": case "ricochet":
                return new Color(255, 210, 110);  // точность — золотой
            case "luck": case "xp_amp":
                return new Color(180, 150, 255);  // удача/опыт — фиолетовый
            case "chain_bullets": case "chaos_drive":
                return new Color(255, 130, 200);  // особый розовый
            default:
                return new Color(200, 205, 215);
        }
    }

    public static GreenfootImage createBossSprite(int size) {
        return createBossSprite(size, "gunner");
    }

    /** Спрайт босса по типу (gunner/spinner/warlord). */
    public static GreenfootImage createBossSprite(int size, String kind) {
        GreenfootImage image = new GreenfootImage(size, size);
        image.setColor(new Color(0, 0, 0, 0));
        image.clear();

        int cx = size / 2;
        int cy = size / 2;

        Color glowC, shellC, coreShellC, eyeC, accentC;
        if ("spinner".equals(kind)) {
            glowC = new Color(120, 80, 255, 70);
            shellC = new Color(40, 28, 80);
            coreShellC = new Color(80, 50, 150);
            eyeC = new Color(190, 160, 255);
            accentC = new Color(150, 110, 255);
        } else if ("warlord".equals(kind)) {
            glowC = new Color(255, 150, 40, 70);
            shellC = new Color(70, 45, 16);
            coreShellC = new Color(150, 95, 30);
            eyeC = new Color(255, 220, 120);
            accentC = new Color(255, 170, 60);
        } else { // gunner
            glowC = new Color(255, 70, 40, 70);
            shellC = new Color(70, 20, 30);
            coreShellC = new Color(140, 30, 40);
            eyeC = new Color(255, 200, 120);
            accentC = new Color(200, 60, 70);
        }

        // grozный внешний ореол
        image.setColor(glowC);
        image.fillOval(0, 0, size, size);
        // броневой панцирь
        image.setColor(shellC);
        image.fillOval(size / 10, size / 10, size - size / 5, size - size / 5);
        image.setColor(coreShellC);
        image.fillOval(size / 6, size / 6, size - size / 3, size - size / 3);

        // рёбра брони
        image.setColor(new Color(0, 0, 0, 90));
        int ridges = "spinner".equals(kind) ? 10 : 8;
        for (int i = 0; i < ridges; i++) {
            double a = Math.PI * 2 * i / ridges + ("spinner".equals(kind) ? 0.3 : 0.0);
            int x1 = (int) Math.round(cx + Math.cos(a) * size / 6.0);
            int y1 = (int) Math.round(cy + Math.sin(a) * size / 6.0);
            int x2 = (int) Math.round(cx + Math.cos(a) * size / 2.4);
            int y2 = (int) Math.round(cy + Math.sin(a) * size / 2.4);
            image.drawLine(x1, y1, x2, y2);
        }

        // светящееся ядро-глаз
        image.setColor(eyeC);
        image.fillOval(cx - size / 8, cy - size / 8, size / 4, size / 4);
        image.setColor(new Color(255, 255, 255, 210));
        image.fillOval(cx - size / 16, cy - size / 16, size / 8, size / 8);

        // внешние акценты по типу
        image.setColor(accentC);
        if ("spinner".equals(kind)) {
            // изогнутые лопасти-спирали
            for (int i = 0; i < 4; i++) {
                double a = Math.PI / 2 * i;
                drawArcLines(image, cx, cy, size / 2 - 3, a, a + Math.toRadians(55));
            }
        } else if ("warlord".equals(kind)) {
            // короны-рога
            for (int i = 0; i < 6; i++) {
                double a = Math.PI / 3 * i;
                int sx = (int) Math.round(cx + Math.cos(a) * size / 2.3);
                int sy = (int) Math.round(cy + Math.sin(a) * size / 2.3);
                int tx = (int) Math.round(cx + Math.cos(a) * size / 1.85);
                int ty = (int) Math.round(cy + Math.sin(a) * size / 1.85);
                image.drawLine(sx, sy, tx, ty);
                image.fillOval(tx - 2, ty - 2, 4, 4);
            }
        } else {
            // gunner: 4 шипа-пушки
            for (int i = 0; i < 4; i++) {
                double a = Math.PI / 4 + Math.PI / 2 * i;
                int sx = (int) Math.round(cx + Math.cos(a) * size / 2.2);
                int sy = (int) Math.round(cy + Math.sin(a) * size / 2.2);
                int tx = (int) Math.round(cx + Math.cos(a) * size / 1.9);
                int ty = (int) Math.round(cy + Math.sin(a) * size / 1.9);
                image.drawLine(sx, sy, tx, ty);
            }
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

    /** Спрайт врага по типу (chaser/ranger/fast/tank/bomber). */
    public static GreenfootImage createEnemySprite(int size, String kind) {
        GreenfootImage img = new GreenfootImage(size, size);
        img.setColor(new Color(0, 0, 0, 0));
        img.clear();
        int cx = size / 2;
        int cy = size / 2;

        switch (kind) {
            case "ranger": {
                img.setColor(new Color(175, 110, 255, 70));
                img.fillOval(2, 2, size - 4, size - 4);
                img.setColor(new Color(77, 37, 130));
                img.fillOval(5, 5, size - 10, size - 10);
                img.setColor(new Color(230, 200, 255));
                img.fillOval(cx - 4, cy - 4, 8, 8);
                img.setColor(new Color(220, 180, 255));
                img.drawLine(cx, 1, cx - 7, 8);
                img.drawLine(cx, 1, cx + 7, 8);
                img.drawLine(cx, size - 2, cx - 7, size - 9);
                img.drawLine(cx, size - 2, cx + 7, size - 9);
                break;
            }
            case "fast": {
                // острый дротик-рой
                img.setColor(new Color(120, 255, 200, 80));
                img.fillOval(2, 2, size - 4, size - 4);
                int[] xs = { cx, size - 3, cx, 3 };
                int[] ys = { 3, cy, size - 3, cy };
                img.setColor(new Color(30, 150, 110));
                img.fillPolygon(xs, ys, 4);
                img.setColor(new Color(150, 255, 220));
                img.fillOval(cx - 3, cy - 3, 6, 6);
                break;
            }
            case "tank": {
                // бронированный шестиугольник
                int r = size / 2 - 3;
                int[] xs = new int[6];
                int[] ys = new int[6];
                for (int i = 0; i < 6; i++) {
                    double a = Math.PI / 3 * i;
                    xs[i] = (int) Math.round(cx + Math.cos(a) * r);
                    ys[i] = (int) Math.round(cy + Math.sin(a) * r);
                }
                img.setColor(new Color(120, 130, 60, 80));
                img.fillOval(0, 0, size, size);
                img.setColor(new Color(90, 84, 40));
                img.fillPolygon(xs, ys, 6);
                img.setColor(new Color(160, 150, 70));
                img.drawPolygon(xs, ys, 6);
                img.setColor(new Color(255, 220, 120));
                img.fillOval(cx - 5, cy - 5, 10, 10);
                img.setColor(new Color(40, 36, 18));
                img.drawOval(cx - 5, cy - 5, 10, 10);
                break;
            }
            case "bomber": {
                img.setColor(new Color(255, 120, 40, 90));
                img.fillOval(0, 0, size, size);
                img.setColor(new Color(150, 50, 20));
                img.fillOval(4, 4, size - 8, size - 8);
                // фитиль/детонатор
                img.setColor(new Color(255, 200, 80));
                img.fillOval(cx - 4, cy - 4, 8, 8);
                img.setColor(new Color(255, 240, 180));
                img.fillOval(cx - 2, cy - 2, 4, 4);
                // шипы
                img.setColor(new Color(255, 150, 70));
                for (int i = 0; i < 6; i++) {
                    double a = Math.PI / 3 * i;
                    img.drawLine((int)(cx + Math.cos(a) * (size/2 - 6)), (int)(cy + Math.sin(a) * (size/2 - 6)),
                                 (int)(cx + Math.cos(a) * (size/2 - 1)), (int)(cy + Math.sin(a) * (size/2 - 1)));
                }
                break;
            }
            default: { // chaser
                img.setColor(new Color(255, 60, 90, 70));
                img.fillOval(1, 1, size - 2, size - 2);
                img.setColor(new Color(120, 18, 34));
                img.fillOval(5, 5, size - 10, size - 10);
                img.setColor(new Color(255, 185, 60));
                img.fillOval(cx - 5, cy - 5, 10, 10);
                img.setColor(new Color(255, 90, 120));
                img.drawLine(cx, 2, cx - 10, 10);
                img.drawLine(cx, 2, cx + 10, 10);
                img.drawLine(cx, size - 3, cx - 10, size - 12);
                img.drawLine(cx, size - 3, cx + 10, size - 12);
                break;
            }
        }
        return img;
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