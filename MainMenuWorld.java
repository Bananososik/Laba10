import greenfoot.Color;
import greenfoot.Greenfoot;
import greenfoot.GreenfootImage;
import greenfoot.World;

/**
 * Стартовый экран. Кнопка запускает забег напрямую в MyWorld (хаб удалён).
 * Это исходник взамен утерянного — теперь проект собирается целиком из .java.
 */
public class MainMenuWorld extends World {
    public MainMenuWorld() {
        super(1280, 800, 1);
        setBackground(SpriteFactory.createSpaceBackground(1280, 800, 160));

        GreenfootImage title = SpriteFactory.createButtonPanel(
            "КОСМИЧЕСКИЙ РОГАЛИК",
            "авто-стрельба • выживание • улучшения",
            720, 150,
            new Color(10, 14, 22, 225),
            new Color(120, 200, 255));
        addObject(new StaticImage(title), 640, 210);

        GreenfootImage hint = SpriteFactory.createButtonPanel(
            "Управление",
            "WASD / стрелки — движение, стрельба автоматическая",
            760, 110,
            new Color(8, 12, 18, 205),
            new Color(120, 120, 140));
        addObject(new StaticImage(hint), 640, 360);

        addObject(new MenuButton("НАЧАТЬ ЗАБЕГ", "Запустить игру", "start_run"), 640, 540);
    }

    public void handleAction(String action) {
        if ("start_run".equals(action)) {
            Greenfoot.setWorld(new MyWorld());
        }
    }
}
