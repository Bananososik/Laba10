import greenfoot.Actor;
import greenfoot.Color;
import greenfoot.Greenfoot;
import greenfoot.GreenfootImage;

public class Enemy extends Actor {
    private int hp = 3;
    private int attackCooldown = 0;
    private double px, py;
    private final boolean ranged;

    public Enemy() {
        this(false);
    }

    public Enemy(boolean ranged) {
        this.ranged = ranged;
        GreenfootImage placeholder = SpriteFactory.createCircle(32, new Color(210, 70, 70), new Color(100, 20, 20));
        if (ranged) {
            placeholder = SpriteFactory.createCircle(32, new Color(170, 110, 255), new Color(80, 30, 140));
        }
        setImage(placeholder);
        if (ranged) {
            hp = 2;
        }
    }

    public void act() {
        if (getWorld() == null) {
            return;
        }
        MyWorld w = (MyWorld)getWorld();
        if (w.isPaused()) return;

        Player player = ((MyWorld) getWorld()).getPlayer();
        if (player == null) {
            return;
        }


        if (px == 0 && py == 0) { px = getX(); py = getY(); }

        if (attackCooldown > 0) {
            attackCooldown--;
        }

        double dx = player.getX() - px;
        double dy = player.getY() - py;
        double dist = Math.hypot(dx, dy);

        if (ranged) {
            // keep distance and shoot at player
            if (dist > 260) {
                double speed = 0.65;
                px += (dx / Math.max(1.0, dist)) * speed;
                py += (dy / Math.max(1.0, dist)) * speed;
            } else if (dist < 170) {
                double speed = 0.55;
                px -= (dx / Math.max(1.0, dist)) * speed;
                py -= (dy / Math.max(1.0, dist)) * speed;
            }
            setLocation((int)Math.round(px), (int)Math.round(py));

            if (attackCooldown <= 0 && dist < 420) {
                double projSpeed = 4.0;
                double vx = (dx / Math.max(1.0, dist)) * projSpeed;
                double vy = (dy / Math.max(1.0, dist)) * projSpeed;
                getWorld().addObject(new EnemyBullet(vx, vy, 100, 1), getX(), getY());
                attackCooldown = 80 + Greenfoot.getRandomNumber(30);
            }
        } else if (dist > 0.5) {
            double speed = 0.9;
            px += (dx / dist) * speed;
            py += (dy / dist) * speed;
            setLocation((int)Math.round(px), (int)Math.round(py));
        }

        if (!ranged && getIntersectingObjects(Player.class).size() > 0) {
            player.takeDamage(1);
        }

        if (hp <= 0) {
            MyWorld world = (MyWorld) getWorld();
            Player p = world.getPlayer();
            if (p != null) {
                p.onEnemyKilled(5);
            }
            world.removeObject(this);
            world.notifyHit();
        }
    }

    private void move(int dx, int dy) {
        int stepX = dx * 1;
        int stepY = dy * 1;

        // move more gradually to avoid overwhelming the player
        setLocation(getX() + stepX, getY());
        if (isTouching(Wall.class)) {
            setLocation(getX() - stepX, getY());
        }

        setLocation(getX(), getY() + stepY);
        if (isTouching(Wall.class)) {
            setLocation(getX(), getY() - stepY);
        }
    }

    public void takeDamage(int amount) {
        hp -= amount;
        if (ranged) {
            setImage(SpriteFactory.createCircle(32, new Color(170, 110, 255), new Color(80, 30, 140)));
        } else {
            setImage(SpriteFactory.createCircle(32, new Color(230, 100, 100), new Color(100, 20, 20)));
        }
    }
}