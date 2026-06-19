import greenfoot.Actor;
import greenfoot.Greenfoot;
import greenfoot.Color;
import greenfoot.GreenfootImage;

public class LUButton extends Actor {
    private final String id;
    private final LevelUpMenu menu;

    public LUButton(String title, String description, String subtitle, LevelUpMenu menu, String id) {
        this.id = id;
        this.menu = menu;
        int width = 360;
        int height = 190;
        GreenfootImage img = SpriteFactory.createBox(width, height, new Color(28, 30, 42), new Color(220, 220, 220));
        GreenfootImage titleText = new GreenfootImage(title, 32, new Color(255, 255, 255), new Color(0, 0, 0, 0));
        GreenfootImage descText = new GreenfootImage(description, 20, new Color(210, 220, 255), new Color(0, 0, 0, 0));
        GreenfootImage subText = new GreenfootImage(subtitle, 18, new Color(255, 210, 120), new Color(0, 0, 0, 0));
        img.drawImage(titleText, Math.max(8, (width - titleText.getWidth()) / 2), 40);
        img.drawImage(subText, Math.max(8, (width - subText.getWidth()) / 2), 82);
        img.drawImage(descText, Math.max(8, (width - descText.getWidth()) / 2), 126);
        setImage(img);
    }

    public void act() {
        if (Greenfoot.mouseClicked(this)) {
            menu.applyChoice(id);
        }
    }
}
