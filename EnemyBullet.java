import greenfoot.Actor;
import greenfoot.Color;

public class EnemyBullet extends Actor {
    private double px;
    private double py;
    private final double vx;
    private final double vy;
    private int life;
    private final int damage;

    public EnemyBullet(double vx, double vy, int life, int damage) {
        this.vx = vx;
        this.vy = vy;
        this.life = life;
        this.damage = damage;
        setImage(SpriteFactory.createCircle(8, new Color(255, 120, 120), new Color(120, 40, 40)));
    }

    public void addedToWorld(greenfoot.World world) {
        px = getX();
        py = getY();
    }

    public void act() {
        MyWorld w = (MyWorld) getWorld();
        if (w == null || w.isPaused()) {
            return;
        }

        px += vx;
        py += vy;
        setLocation((int)Math.round(px), (int)Math.round(py));

        if (isTouching(Wall.class)) {
            getWorld().removeObject(this);
            return;
        }

        Player player = (Player) getOneIntersectingObject(Player.class);
        if (player != null) {
            player.takeDamage(damage);
            getWorld().removeObject(this);
            return;
        }

        life--;
        if (life <= 0) {
            getWorld().removeObject(this);
        }
    }
}