import greenfoot.*;

public class MyWorld extends World {
    private final Hud hud;
    private Player player;
    private static final int TILE = 40;
    private static final int GRID_W = 32;
    private static final int GRID_H = 20;
    private boolean paused = false;
    private int spawnCooldown = 0;
    private int nextBossLevel = 5;

    public MyWorld() {
        super(GRID_W * TILE, GRID_H * TILE, 1);
        setBackground(SpriteFactory.createSpaceDeckBackground(GRID_W * TILE, GRID_H * TILE, TILE));
        setPaintOrder(Hud.class, Pickup.class, Bullet.class, EnemyBullet.class, Boss.class, Enemy.class, Player.class, Wall.class);
        buildLevel();
        player = new Player();
        addObject(player, 2 * TILE + TILE / 2, 2 * TILE + TILE / 2);
        hud = new Hud();
        addObject(hud, GRID_W * TILE / 2, 48);
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isPaused() { return paused; }
    public void setPaused(boolean p) { paused = p; }

    public void notifyHit() {
        hud.markDirty();
    }

    public void restartLevel() {
        Greenfoot.setWorld(new MyWorld());
    }

    public void act() {
        if (paused || player == null) {
            return;
        }

        if (spawnCooldown > 0) {
            spawnCooldown--;
        }

        // мини-босс появляется на пороговых уровнях, по одному за раз
        if (player.getLevel() >= nextBossLevel && getObjects(Boss.class).isEmpty()) {
            spawnBoss();
            nextBossLevel += 5;
        }

        int enemyCount = getObjects(Enemy.class).size();
        int targetEnemies = Math.min(14, 3 + player.getLevel());
        if (enemyCount < targetEnemies && spawnCooldown <= 0) {
            spawnEnemy();
            spawnCooldown = Math.max(20, 90 - player.getLevel() * 2);
        }
    }

    private void spawnBoss() {
        int margin = TILE * 3;
        int tries = 40;
        while (tries-- > 0) {
            int x = Greenfoot.getRandomNumber(getWidth() - margin * 2) + margin;
            int y = Greenfoot.getRandomNumber(getHeight() - margin * 2) + margin;

            if (Math.hypot(player.getX() - x, player.getY() - y) < TILE * 6) {
                continue;
            }
            if (!getObjectsAt(x, y, Wall.class).isEmpty()) {
                continue;
            }

            addObject(new Boss(), x, y);
            return;
        }
        // запасной вариант: центр арены
        addObject(new Boss(), getWidth() / 2, getHeight() / 2);
    }

    private void spawnEnemy() {
        int margin = TILE * 2;
        int tries = 40;
        while (tries-- > 0) {
            int x = Greenfoot.getRandomNumber(getWidth() - margin * 2) + margin;
            int y = Greenfoot.getRandomNumber(getHeight() - margin * 2) + margin;

            if (Math.hypot(player.getX() - x, player.getY() - y) < TILE * 5) {
                continue;
            }
            if (!getObjectsAt(x, y, Wall.class).isEmpty()) {
                continue;
            }

            boolean ranged = Greenfoot.getRandomNumber(100) < 30;
            addObject(new Enemy(ranged), x, y);
            return;
        }
    }

    private void buildLevel() {
        boolean[][] solid = new boolean[GRID_W][GRID_H];

        // outer walls
        for (int x = 0; x < GRID_W; x++) {
            solid[x][0] = true;
            solid[x][GRID_H - 1] = true;
        }

        for (int y = 1; y < GRID_H - 1; y++) {
            solid[0][y] = true;
            solid[GRID_W - 1][y] = true;
        }

        int[][] blocks = {
            {4, 3}, {5, 3}, {6, 3},
            {12, 4}, {12, 5}, {12, 6},
            {7, 9}, {8, 9}, {9, 9}, {10, 9},
            {15, 10}, {15, 11}, {15, 12}
        };

        for (int[] block : blocks) {
            solid[block[0]][block[1]] = true;
        }

        for (int x = 0; x < GRID_W; x++) {
            for (int y = 0; y < GRID_H; y++) {
                if (!solid[x][y]) {
                    continue;
                }

                boolean north = isSolid(solid, x, y - 1);
                boolean east = isSolid(solid, x + 1, y);
                boolean south = isSolid(solid, x, y + 1);
                boolean west = isSolid(solid, x - 1, y);
                Wall.Variant variant = Wall.chooseVariant(north, east, south, west);
                addObject(new Wall(variant), x * TILE + TILE / 2, y * TILE + TILE / 2);
            }
        }

        addObject(new Enemy(false), 15 * TILE + TILE/2, 3 * TILE + TILE/2);
        addObject(new Enemy(false), 16 * TILE + TILE/2, 10 * TILE + TILE/2);
        addObject(new Enemy(true), 4 * TILE + TILE/2, 11 * TILE + TILE/2);
    }

    private boolean isSolid(boolean[][] solid, int x, int y) {
        if (x < 0 || y < 0 || x >= GRID_W || y >= GRID_H) {
            return false;
        }
        return solid[x][y];
    }
}
