import greenfoot.Actor;
import greenfoot.Color;
import greenfoot.GreenfootImage;

public class Hud extends Actor {
    private boolean dirty = true;

    public void act() {
        MyWorld world = (MyWorld) getWorld();
        Player player = world == null ? null : world.getPlayer();

        // щит убывает со временем — пока он активен, перерисовываем каждый кадр
        boolean shieldActive = player != null && player.hasShield();
        if (!dirty && !shieldActive) {
            return;
        }

        int hp = player == null ? 0 : player.getHp();
        int maxHp = player == null ? 1 : Math.max(1, player.getMaxHp());
        int xp = player == null ? 0 : player.getXp();
        int xpToNext = player == null ? 1 : Math.max(1, player.getXpToNext());
        int level = player == null ? 1 : player.getLevel();
        int activeSlots = player == null ? 0 : player.getActiveSlotCount();
        int passiveSlots = player == null ? 0 : player.getPassiveSlotCount();
        int maxActive = player == null ? 6 : player.getMaxActiveSlots();
        int maxPassive = player == null ? 6 : player.getMaxPassiveSlots();
        int shieldTicks = player == null ? 0 : player.getShieldTicks();
        int shieldMax = player == null ? 1 : Math.max(1, player.getShieldMaxTicks());

        int panelW = 1220;
        int panelH = 150;
        GreenfootImage panel = new GreenfootImage(panelW, panelH);
        panel.setColor(new Color(12, 12, 18, 235));
        panel.fillRect(0, 0, panelW, panelH);
        panel.setColor(new Color(255, 220, 120, 40));
        panel.fillRect(0, 0, panelW, 24);
        panel.setColor(new Color(180, 180, 190));
        panel.drawRect(0, 0, panelW - 1, panelH - 1);

        panel.setColor(new Color(255, 255, 255));
        panel.drawString("ЗДОРОВЬЕ", 20, 30);
        panel.drawString("ЩИТ", 20, 70);
        panel.drawString("ОПЫТ ДО УРОВНЯ", 20, 116);
        panel.drawString("УРОВЕНЬ: " + level, 1030, 30);
        panel.drawString("СЛОТЫ A/P: " + activeSlots + "/" + maxActive + "  " + passiveSlots + "/" + maxPassive, 900, 116);

        int barX = 170;
        int hpBarW = 760;
        drawBar(panel, barX, 14, hpBarW, 22, hp, maxHp, new Color(190, 60, 60), new Color(70, 16, 16));
        panel.setColor(new Color(255, 255, 255));
        panel.drawString(hp + "/" + maxHp, barX + hpBarW + 14, 30);

        // отдельная полоса щита (ТЗ, раздел 8)
        int shBarW = 760;
        drawBar(panel, barX, 54, shBarW, 18, shieldTicks, shieldMax, new Color(90, 180, 255), new Color(20, 44, 80));
        panel.setColor(new Color(210, 235, 255));
        panel.drawString(shieldActive ? "АКТИВЕН" : "—", barX + shBarW + 14, 70);

        int xpBarW = 930;
        drawBar(panel, barX, 98, xpBarW, 22, xp, xpToNext, new Color(150, 110, 255), new Color(48, 30, 90));
        panel.setColor(new Color(255, 255, 255));
        panel.drawString(xp + "/" + xpToNext, barX + xpBarW + 14, 116);

        setImage(panel);
        dirty = false;
    }

    public void markDirty() {
        dirty = true;
    }

    private void drawBar(GreenfootImage target, int x, int y, int width, int height, int value, int maxValue, Color fill, Color bg) {
        target.setColor(bg);
        target.fillRect(x, y, width, height);
        int fillWidth = (int) Math.round(width * (Math.max(0, Math.min(value, maxValue)) / (double) maxValue));
        target.setColor(fill);
        target.fillRect(x, y, fillWidth, height);
        target.setColor(new Color(240, 240, 240));
        target.drawRect(x, y, width, height);
    }
}