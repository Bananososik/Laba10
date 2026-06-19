import greenfoot.Color;
import greenfoot.Greenfoot;
import greenfoot.GreenfootImage;
import greenfoot.World;

public class HubWorld extends World {
    public HubWorld() {
        super(1280, 800, 1);
        setBackground(SpriteFactory.createSpaceBackground(1280, 800, 140));

        GreenfootImage title = SpriteFactory.createButtonPanel("ХАБ РОГАЛИКА", "точка между забегами", 560, 130, new Color(10, 14, 20, 220), new Color(110, 200, 255));
        addObject(new StaticImage(title), 640, 110);

        GreenfootImage info = SpriteFactory.createButtonPanel("Тренировочная зона", "запуск забега, будущий магазин, постоянные апгрейды", 980, 120, new Color(7, 12, 18, 210), new Color(120, 120, 140));
        addObject(new StaticImage(info), 640, 180);

        addObject(new MenuButton("ОТПРАВИТЬСЯ В ЗАБЕГ", "Запустить боевой раунд", "start_run"), 640, 345);
        addObject(new MenuButton("ГЛАВНОЕ МЕНЮ", "Вернуться на стартовый экран", "back_menu"), 640, 500);
    }

    public void handleAction(String action) {
        if ("start_run".equals(action)) {
            Greenfoot.setWorld(new MyWorld());
        } else if ("back_menu".equals(action)) {
            Greenfoot.setWorld(new MainMenuWorld());
        }
    }
}