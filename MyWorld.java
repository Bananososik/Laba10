import greenfoot.*;

public class MyWorld extends World {
    private final Hud hud;
    private Player player;
    private static final int TILE = 40;
    private static final int GRID_W = 40;
    private static final int GRID_H = 22;
    private boolean paused = false;
    private int spawnCooldown = 0;
    private int nextBossLevel = 4;
    private int bossesSpawned = 0;
    private boolean gameOver = false;

    public MyWorld() {
        super(GRID_W * TILE, GRID_H * TILE, 1);
        setBackground(SpriteFactory.createSpaceDeckBackground(GRID_W * TILE, GRID_H * TILE, TILE));
        setPaintOrder(GameOverScreen.class, LUButton.class, LevelUpMenu.class, Hud.class, Pickup.class, Bullet.class, EnemyBullet.class, Boss.class, Enemy.class, Player.class, Wall.class);
        buildLevel();
        player = new Player();
        addObject(player, 2 * TILE + TILE / 2, 2 * TILE + TILE / 2);
        hud = new Hud();
        // верхний-левый угол, панель целиком на экране (центр актёра = половина размеров + отступ)
        addObject(hud, 10 + Hud.PANEL_W / 2, 8 + Hud.PANEL_H / 2);
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

    /** Вызывается игроком при смерти — показывает экран GAME OVER и замораживает мир. */
    public void onPlayerDied() {
        if (gameOver) {
            return;
        }
        gameOver = true;
        paused = true;
        int level = player != null ? player.getLevel() : 1;
        int kills = player != null ? player.getKillCount() : 0;
        addObject(new GameOverScreen(level, kills), getWidth() / 2, getHeight() / 2);
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void act() {
        if (paused || gameOver || player == null) {
            return;
        }

        if (spawnCooldown > 0) {
            spawnCooldown--;
        }

        int level = player.getLevel();

        // боссы появляются на пороговых уровнях, по одному за раз; тип чередуется
        if (level >= nextBossLevel && getObjects(Boss.class).isEmpty()) {
            spawnBoss();
            nextBossLevel += 4;
        }

        // ramp: после порогового уровня врагов больше и спавн чаще
        int targetEnemies = enemyTarget(level);
        int enemyCount = getObjects(Enemy.class).size();
        if (enemyCount < targetEnemies && spawnCooldown <= 0) {
            // на высоких уровнях спавним пачками
            int burst = level >= 12 ? 3 : (level >= 6 ? 2 : 1);
            for (int i = 0; i < burst && getObjects(Enemy.class).size() < targetEnemies; i++) {
                spawnEnemy(level);
            }
            spawnCooldown = spawnDelay(level);
        }
    }

    /** Целевое число врагов на арене растёт с уровнем, после 6 — ускоренно. */
    private int enemyTarget(int level) {
        int base = 4 + level;
        if (level >= 6) {
            base += (level - 5) * 2; // резкий рост после 6 уровня
        }
        return Math.min(48, base);
    }

    /** Задержка между волнами спавна падает с уровнем (враги идут чаще). */
    private int spawnDelay(int level) {
        int delay = 80 - level * 4;
        if (level >= 6) {
            delay -= (level - 5) * 3;
        }
        return Math.max(8, delay);
    }

    private void spawnBoss() {
        Boss.Kind kind = Boss.Kind.values()[bossesSpawned % Boss.Kind.values().length];
        bossesSpawned++;
        int level = player.getLevel();
        int margin = TILE * 3;
        int tries = 40;
        while (tries-- > 0) {
            int x = Greenfoot.getRandomNumber(getWidth() - margin * 2) + margin;
            int y = Greenfoot.getRandomNumber(getHeight() - margin * 2) + margin;

            if (Math.hypot(player.getX() - x, player.getY() - y) < TILE * 7) {
                continue;
            }
            if (!getObjectsAt(x, y, Wall.class).isEmpty()) {
                continue;
            }

            addObject(new Boss(kind, level), x, y);
            return;
        }
        addObject(new Boss(kind, level), getWidth() / 2, getHeight() / 2);
    }

    private void spawnEnemy(int level) {
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

            addObject(new Enemy(Enemy.rollKind(level), level), x, y);
            return;
        }
    }

    private void buildLevel() {
        boolean[][] solid = new boolean[GRID_W][GRID_H];

        // внешняя обшивка корабля
        for (int x = 0; x < GRID_W; x++) {
            solid[x][0] = true;
            solid[x][GRID_H - 1] = true;
        }
        for (int y = 1; y < GRID_H - 1; y++) {
            solid[0][y] = true;
            solid[GRID_W - 1][y] = true;
        }

        // переборки-отсеки: прямоугольные блоки-колонны и короткие стены
        int[][] rooms = {
            // {x, y, w, h} — небольшие препятствия-«колонны» и переборки
            {6, 5, 3, 1}, {6, 5, 1, 4},
            {38, 4, 4, 1}, {41, 4, 1, 5},
            {10, 22, 4, 1}, {10, 19, 1, 4},
            {36, 23, 4, 1}, {36, 20, 1, 4},
            {22, 8, 4, 1}, {22, 21, 4, 1},
            {18, 14, 2, 2}, {28, 14, 2, 2},
            {14, 11, 1, 3}, {33, 11, 1, 3},
            {23, 3, 1, 3}, {23, 24, 1, 3}
        };
        for (int[] r : rooms) {
            for (int dx = 0; dx < r[2]; dx++) {
                for (int dy = 0; dy < r[3]; dy++) {
                    int x = r[0] + dx;
                    int y = r[1] + dy;
                    if (x > 0 && y > 0 && x < GRID_W - 1 && y < GRID_H - 1) {
                        solid[x][y] = true;
                    }
                }
            }
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
                addObject(new Wall(variant, x, y), x * TILE + TILE / 2, y * TILE + TILE / 2);
            }
        }
    }

    private boolean isSolid(boolean[][] solid, int x, int y) {
        if (x < 0 || y < 0 || x >= GRID_W || y >= GRID_H) {
            return false;
        }
        return solid[x][y];
    }
}
