import greenfoot.Actor;
import greenfoot.Color;
import greenfoot.GreenfootImage;

public class Hud extends Actor {
    private boolean dirty = true;

    public void act() {
        if (!dirty) {
            return;
        }

        MyWorld world = (MyWorld) getWorld();
        Player player = world.getPlayer();
        int hp = player == null ? 0 : player.getHp();
        int maxHp = player == null ? 1 : Math.max(1, player.getMaxHp());
        int xp = player == null ? 0 : player.getXp();
        int xpToNext = player == null ? 1 : Math.max(1, player.getXpToNext());
        int level = player == null ? 1 : player.getLevel();
        int activeSlots = player == null ? 0 : player.getActiveSlotCount();
        int passiveSlots = player == null ? 0 : player.getPassiveSlotCount();
        int maxActive = player == null ? 6 : player.getMaxActiveSlots();
        int maxPassive = player == null ? 6 : player.getMaxPassiveSlots();
        String shieldText = (player != null && player.hasShield()) ? "  ЩИТ: АКТИВЕН" : "";
        GreenfootImage panel = new GreenfootImage(1220, 96);
        panel.setColor(new Color(12, 12, 18, 225));
        panel.fillRect(0, 0, 1220, 96);
        panel.setColor(new Color(180, 180, 190));
        panel.drawRect(0, 0, 1219, 95);

        panel.setColor(new Color(255, 255, 255));
        panel.drawString("ЗДОРОВЬЕ", 20, 28);
        panel.drawString("ОПЫТ ДО УРОВНЯ" + shieldText, 20, 72);
        panel.drawString("УРОВЕНЬ: " + level, 1030, 28);
        panel.drawString("СЛОТЫ A/P: " + activeSlots + "/" + maxActive + "  " + passiveSlots + "/" + maxPassive, 900, 72);

        int hpBarX = 170;
        int hpBarY = 14;
        int hpBarW = 760;
        int hpBarH = 20;
        drawBar(panel, hpBarX, hpBarY, hpBarW, hpBarH, hp, maxHp, new Color(190, 60, 60), new Color(70, 16, 16));
        panel.setColor(new Color(255, 255, 255));
        panel.drawString(hp + "/" + maxHp, hpBarX + hpBarW + 14, 30);

        int xpBarX = 170;
        int xpBarY = 58;
        int xpBarW = 930;
        int xpBarH = 20;
        drawBar(panel, xpBarX, xpBarY, xpBarW, xpBarH, xp, xpToNext, new Color(90, 180, 255), new Color(20, 58, 96));
        panel.setColor(new Color(255, 255, 255));
        panel.drawString(xp + "/" + xpToNext, xpBarX + xpBarW + 14, 74);

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