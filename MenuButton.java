import greenfoot.Actor;
import greenfoot.Color;
import greenfoot.Greenfoot;
import greenfoot.GreenfootImage;
import greenfoot.MouseInfo;

public class MenuButton extends Actor {
    private final String action;
    private final String title;
    private final String subtitle;

    public MenuButton(String title, String subtitle, String action) {
        this.title = title;
        this.subtitle = subtitle;
        this.action = action;
        draw(false);
    }

    public void act() {
        MouseInfo mouse = Greenfoot.getMouseInfo();
        boolean hover = mouse != null && mouse.getActor() == this;
        draw(hover);
        if (Greenfoot.mouseClicked(this)) {
            if (getWorld() instanceof MainMenuWorld) {
                ((MainMenuWorld) getWorld()).handleAction(action);
            } else if (getWorld() instanceof HubWorld) {
                ((HubWorld) getWorld()).handleAction(action);
            }
        }
    }

    private void draw(boolean hover) {
        int width = 420;
        int height = 132;
        Color fill = hover ? new Color(50, 58, 84, 240) : new Color(28, 30, 42, 230);
        Color border = hover ? new Color(255, 220, 120) : new Color(220, 220, 220);
        GreenfootImage img = SpriteFactory.createButtonPanel(title, subtitle, width, height, fill, border);
        setImage(img);
    }
}