import greenfoot.Actor;
import greenfoot.Color;
import greenfoot.GreenfootImage;

/**
 * HUD: уровень, HP и опыт в виде заполняющихся ЯЧЕЕК (без тёмной подложки).
 * Щит — отдельная ячеистая полоса. Панель целиком помещается у верхнего края.
 */
public class Hud extends Actor {
    private boolean dirty = true;

    // фиксированные размеры панели (целиком влезает на экран)
    public static final int PANEL_W = 560;
    public static final int PANEL_H = 132;

    public void act() {
        MyWorld world = (MyWorld) getWorld();
        Player player = world == null ? null : world.getPlayer();

        boolean shieldActive = player != null && player.hasShield();
        if (!dirty && !shieldActive) {
            return;
        }

        int hp = player == null ? 0 : player.getHp();
        int maxHp = player == null ? 1 : Math.max(1, player.getMaxHp());
        int xp = player == null ? 0 : player.getXp();
        int xpToNext = player == null ? 1 : Math.max(1, player.getXpToNext());
        int level = player == null ? 1 : player.getLevel();
        int shieldTicks = player == null ? 0 : player.getShieldTicks();
        int shieldMax = player == null ? 1 : Math.max(1, player.getShieldMaxTicks());

        GreenfootImage panel = new GreenfootImage(PANEL_W, PANEL_H);
        // без тёмного фона — только полупрозрачная тонкая подложка под текст для читаемости
        panel.setColor(new Color(10, 14, 22, 90));
        panel.fillRect(0, 0, PANEL_W, PANEL_H);
        panel.setColor(new Color(80, 130, 180, 120));
        panel.drawRect(0, 0, PANEL_W - 1, PANEL_H - 1);

        int labelX = 12;
        int barX = 110;
        int barW = 430;

        // уровень
        panel.setColor(new Color(255, 225, 140));
        panel.drawString("УРОВЕНЬ " + level, labelX, 22);

        // HP — ячейки
        panel.setColor(new Color(255, 255, 255));
        panel.drawString("HP", labelX, 52);
        drawCells(panel, barX, 38, barW, 20, hp, maxHp,
                  new Color(220, 70, 70), new Color(120, 30, 30), new Color(60, 18, 18));
        panel.setColor(new Color(255, 255, 255));
        panel.drawString(hp + " / " + maxHp, barX + barW + 6, 53);

        // ЩИТ — ячейки
        panel.setColor(new Color(200, 230, 255));
        panel.drawString("ЩИТ", labelX, 82);
        drawCells(panel, barX, 68, barW, 14, shieldTicks, shieldMax,
                  new Color(90, 180, 255), new Color(40, 90, 150), new Color(20, 40, 70));

        // ОПЫТ — ячейки
        panel.setColor(new Color(210, 190, 255));
        panel.drawString("ОПЫТ", labelX, 112);
        drawCells(panel, barX, 98, barW, 20, xp, xpToNext,
                  new Color(170, 120, 255), new Color(90, 60, 150), new Color(45, 30, 80));
        panel.setColor(new Color(255, 255, 255));
        panel.drawString(xp + " / " + xpToNext, barX + barW + 6, 113);

        setImage(panel);
        dirty = false;
    }

    public void markDirty() {
        dirty = true;
    }

    /**
     * Рисует значение как ряд заполняющихся ячеек.
     * Количество ячеек подбирается так, чтобы они были читаемы при любом maxValue.
     */
    private void drawCells(GreenfootImage t, int x, int y, int width, int height,
                           int value, int maxValue, Color fill, Color fillDim, Color empty) {
        int cells = chooseCellCount(maxValue);
        int gap = 2;
        double cellW = (width - gap * (cells - 1)) / (double) cells;

        double filled = cells * (Math.max(0, Math.min(value, maxValue)) / (double) maxValue);
        int fullCells = (int) Math.floor(filled);
        double frac = filled - fullCells;

        for (int i = 0; i < cells; i++) {
            int cx = x + (int) Math.round(i * (cellW + gap));
            int w = (int) Math.round(cellW);

            // пустая ячейка
            t.setColor(empty);
            t.fillRect(cx, y, w, height);

            if (i < fullCells) {
                t.setColor(fill);
                t.fillRect(cx, y, w, height);
            } else if (i == fullCells && frac > 0.02) {
                // частично заполненная ячейка
                t.setColor(fillDim);
                t.fillRect(cx, y, (int) Math.round(w * frac), height);
            }

            t.setColor(new Color(0, 0, 0, 130));
            t.drawRect(cx, y, w, height);
        }
        // внешняя рамка
        t.setColor(new Color(230, 230, 240, 160));
        t.drawRect(x, y, width, height);
    }

    private int chooseCellCount(int maxValue) {
        if (maxValue <= 10) return Math.max(1, maxValue);
        if (maxValue <= 20) return 10;
        if (maxValue <= 40) return 16;
        return 20;
    }
}
