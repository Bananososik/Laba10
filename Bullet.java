import greenfoot.Actor;
import greenfoot.Color;
import java.util.List;

public class Bullet extends Actor {
    private double px;
    private double py;
    private double vx;
    private double vy;
    private int life;
    private final int damage;
    private int pierceLeft;
    private int ricochetLeft;
    private final boolean homing;

    public Bullet(double vx, double vy, int life, int damage, int pierceLeft, int ricochetLeft, boolean homing) {
        this.vx = vx;
        this.vy = vy;
        this.life = life;
        this.damage = damage;
        this.pierceLeft = Math.max(0, pierceLeft);
        this.ricochetLeft = Math.max(0, ricochetLeft);
        this.homing = homing;
        setImage(SpriteFactory.createBulletSprite("normal"));
    }

    /** Перекрашивает пулю под тип оружия (laser/nova/drone/chain) — визуальный эффект по ТЗ. */
    public Bullet withKind(String kind) {
        setImage(SpriteFactory.createBulletSprite(kind));
        if ("laser".equals(kind)) {
            // вытянутый луч поворачиваем по направлению полёта
            setRotation((int) Math.round(Math.toDegrees(Math.atan2(vy, vx))));
        }
        return this;
    }

    public int getDamage() {
        return damage;
    }

    public void addedToWorld(greenfoot.World world) {
        px = getX();
        py = getY();
    }

    public void act() {
        MyWorld w = (MyWorld) getWorld();
        if (w != null && w.isPaused()) return;

        if (homing) {
            steerToNearestEnemy();
        }

        px += vx;
        py += vy;
        setLocation((int) Math.round(px), (int) Math.round(py));

        if (isTouching(Wall.class)) {
            if (ricochetLeft > 0) {
                vx = -vx;
                vy = -vy;
                ricochetLeft--;
                px += vx;
                py += vy;
                setLocation((int) Math.round(px), (int) Math.round(py));
            } else {
                getWorld().removeObject(this);
            }
            return;
        }

        Enemy enemy = (Enemy) getOneIntersectingObject(Enemy.class);
        if (enemy != null) {
            enemy.takeDamage(damage);
            if (pierceLeft > 0) {
                pierceLeft--;
            } else {
                getWorld().removeObject(this);
                return;
            }
        }

        life--;
        if (life <= 0) {
            getWorld().removeObject(this);
        }
    }

    private void steerToNearestEnemy() {
        if (getWorld() == null) {
            return;
        }
        List<Enemy> enemies = getWorld().getObjects(Enemy.class);
        if (enemies.isEmpty()) {
            return;
        }

        Enemy nearest = null;
        double best = Double.MAX_VALUE;
        for (Enemy e : enemies) {
            double d = Math.hypot(e.getX() - getX(), e.getY() - getY());
            if (d < best) {
                best = d;
                nearest = e;
            }
        }
        if (nearest == null) {
            return;
        }

        double speed = Math.max(0.1, Math.hypot(vx, vy));
        double dx = nearest.getX() - getX();
        double dy = nearest.getY() - getY();
        double dist = Math.max(1.0, Math.hypot(dx, dy));
        double targetVx = (dx / dist) * speed;
        double targetVy = (dy / dist) * speed;

        // smooth homing to avoid instant snapping
        vx = vx * 0.82 + targetVx * 0.18;
        vy = vy * 0.82 + targetVy * 0.18;
    }
}