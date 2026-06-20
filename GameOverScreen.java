import greenfoot.Actor;
import greenfoot.Color;
import greenfoot.Greenfoot;
import greenfoot.GreenfootImage;

/**
 * Экран «GAME OVER» поверх арены. Показывает статистику забега и
 * предлагает рестарт (R / клик) или выход в меню (M / клик).
 */
public class GameOverScreen extends Actor {
    private final int level;
    private final int kills;
    private int tick = 0;

    public GameOverScreen(int level, int kills) {
        this.level = level;
        this.kills = kills;
    }

    protected void addedToWorld(greenfoot.World w) {
        render();
    }

    private void render() {
        int width = getWorld().getWidth();
        int height = getWorld().getHeight();
        GreenfootImage img = new GreenfootImage(width, height);

        // затемнение арены
        img.setColor(new Color(8, 6, 12, 215));
        img.fillRect(0, 0, width, height);

        int cx = width / 2;
        int cy = height / 2;

        // центральная панель
        int pw = 640;
        int ph = 380;
        img.setColor(new Color(18, 16, 26, 245));
        img.fillRect(cx - pw / 2, cy - ph / 2, pw, ph);
        img.setColor(new Color(220, 70, 70));
        img.drawRect(cx - pw / 2, cy - ph / 2, pw, ph);
        img.drawRect(cx - pw / 2 + 2, cy - ph / 2 + 2, pw - 4, ph - 4);

        GreenfootImage title = new GreenfootImage("GAME OVER", 72, new Color(255, 80, 80), new Color(0, 0, 0, 0));
        img.drawImage(title, cx - title.getWidth() / 2, cy - ph / 2 + 40);

        GreenfootImage lvl = new GreenfootImage("Достигнут уровень: " + level, 30, new Color(230, 230, 245), new Color(0, 0, 0, 0));
        GreenfootImage kil = new GreenfootImage("Уничтожено врагов: " + kills, 30, new Color(230, 230, 245), new Color(0, 0, 0, 0));
        img.drawImage(lvl, cx - lvl.getWidth() / 2, cy - 30);
        img.drawImage(kil, cx - kil.getWidth() / 2, cy + 12);

        GreenfootImage r = new GreenfootImage("[R] Начать заново", 26, new Color(120, 220, 150), new Color(0, 0, 0, 0));
        GreenfootImage m = new GreenfootImage("[M] Главное меню", 26, new Color(150, 200, 255), new Color(0, 0, 0, 0));
        img.drawImage(r, cx - r.getWidth() / 2, cy + ph / 2 - 88);
        img.drawImage(m, cx - m.getWidth() / 2, cy + ph / 2 - 50);

        setImage(img);
    }

    public void act() {
        tick++;
        if (Greenfoot.isKeyDown("r")) {
            Greenfoot.setWorld(new MyWorld());
        } else if (Greenfoot.isKeyDown("m")) {
            Greenfoot.setWorld(new MainMenuWorld());
        }
    }
}
