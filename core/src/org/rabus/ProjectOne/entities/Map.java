package org.rabus.ProjectOne.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;
import com.sun.org.apache.xerces.internal.impl.dv.dtd.NOTATIONDatatypeValidator;

public class Map
{
    /* TILES */
    public static int EMPTY = 0; // BLACK (0 0 0)
    public static int START = 0xff0000; // RED (255 0 0)
    public static int CHECKPOINT = 0xff0100; // LIGHT RED (255 1 0)
    public static int END = 0xff00ff; // PURPLE (255 0 255)
    public static int BORDER = 0x646464; // GRAY (100 100 100)
    public static int MONSTER_BORDER = 0x969696; // GRAY (150 150 150)
    public static int TILE = 0xffffff; // WHITE (255 255 255)
    public static int ROCK = 0x303030; // DARK GRAY
    public static int PLATFORM = 0xc8c8c8; // LIGHT GRAY
    public static int WATER = 0x008aff; // LIGHT BLUE
    public static int LAVA = 0xff7500; // ORANGE
    public static int ACID = 0x008a00; // LIGHT GREEN
    public static int BLUECRYSTAL = 0x0099ff; // LIGHT BLUE (0 99 255)
    public static int GREENCRYSTAL = 0x99ff00; // LIMETTE
    public static int REDCRYSTAL = 0xff0099; // PINK
    public static int SPIKES = 0x00ff00; // GREEN
    public static int ENERGY_BALL = 0x0000ff; // 0 0 255
    public static int ROCKET = 0x1010ff; // 16 16 255
    public static int ARROW = 0x2020ff; // 32 32 255
    public static int MOVING_SPIKES = 0xffff00; // YELLOW
    public static int LASER = 0x00ffff; // CYAN
    public static int SPAWNER = 0xff6464; // PINK
    /* MONSTERS */
    public static int ORC = 0xa0b0c0;
    /* ITEMS */
    public static int ARMOR_WARRIOR = 0x321919; // BROWN (50 25 25)

    public static final float PIXELS_PER_METER = 20.0f;

    public AssetManager assets;
    public float volume = 0.5f;

    TiledMap map;

    public int[][] tiles; //, fluids;
    public int width, height;
    int savedCrystals = 0;
    int savedScore = 0;
    int savedHealth = 0;
    public Player player;
    public EndFlag endFlag;
    public Array<Monster> monsters = new Array<Monster>();
    public Array<Checkpoint> checkpoints = new Array<Checkpoint>();
    public Array<Spawner> spawners = new Array<Spawner>(); // Monster spawners
    public Checkpoint activeCheckpoint = null;
    public Array<Projectile> projectiles = new Array<Projectile>();
    public Array<Crystal> crystals = new Array<Crystal>();
    public Array<PickupItem> pickupItems = new Array<PickupItem>();
    public Array<MovingSpikes> movingSpikes = new Array<MovingSpikes>();
    public Array<Platform> platforms = new Array<Platform>();
    public Array<Fluid> fluids = new Array<Fluid>();
    public Array<Spike> spikes = new Array<Spike>();
    public Array<Block> blocks = new Array<Block>();

    public Map(AssetManager assets, TiledMap mapname)
    {
        this.assets = assets;
        this.map = mapname;
        loadTiledEntities(mapname);
        initializeEntities();
    }

    public Map(AssetManager assets, Pixmap mapname)
    {
        this.assets = assets;
        loadPixmapEntities(mapname);
        initializeEntities();
    }

    private int getCellType(TiledMapTileLayer layer, int x, int y)
    {
        String type = null;
        if (layer.getCell(x, y).getTile().getProperties().containsKey("type"))
            type = layer.getCell(x, y).getTile().getProperties().get("type", String.class);
        else
            type = "0";
        if (type.equals("START"))
            return START;
        else if (type.equals("CHECKPOINT"))
            return CHECKPOINT;
        else if (type.equals("END"))
            return END;
        else if (type.equals("BORDER"))
            return BORDER;
        else if (type.equals("TILE"))
            return TILE;
        else if (type.equals("WATER"))
            return WATER;
        else
            return EMPTY;
    }

    private void loadTiledEntities(TiledMap mapname)
    {
        //TiledMap map = mapname; // Map loading, providing width and height as well as structure
        TiledMapTileLayer pixmap = (TiledMapTileLayer) mapname.getLayers().get(0);
        width = pixmap.getWidth();
        height = pixmap.getHeight();
        tiles = new int[width][height];
        for (int y = 0; y < this.height; y++) // Using height from pixmap
        {
            for (int x = 0; x < this.width; x++) // Using width from pixmap
            {
                int pix = getCellType(pixmap, x, y);
                //int pix = (Integer.parseInt(pixmap.getCell(x, y).getTile().getProperties().get("type", String.class)) >>> 8) & 0xffffff;
                //Gdx.app.log("Map", x + ", " + y + ", " + Integer.toHexString(pix)); // Display debug information about every pixel in log.
                if (match(pix, START))
                {
                    Checkpoint checkpoint = new Checkpoint(this, x, y); // pixmap.getHeight() - 1 - y // Reversed y-axis with pixmaps
                    checkpoints.add(checkpoint);
                    activeCheckpoint = checkpoint;
                    activeCheckpoint.active = true;
                    player = new Player(assets, this, activeCheckpoint.bounds.x, activeCheckpoint.bounds.y);
                    player.state = Player.SPAWN;
                    Gdx.app.log("Map: ", "Added SPAWN Point at: " + Integer.toString(x) + " " + Integer.toString(pixmap.getHeight() - 1 - y));
                }
                else if (match(pix, CHECKPOINT))
                {
                    checkpoints.add(new Checkpoint(this, x, y));
                }
                else if (match(pix, SPAWNER))
                {
                    spawners.add(new Spawner(this, x, y));
                }
                else if (match(pix, ORC))
                {
                    monsters.add(new Monster(this.assets, this, x, y, Monster.ORC));
                }
                else if (match(pix, ENERGY_BALL))
                {
                    projectiles.add(new Projectile(assets, this, x, y, Projectile.ENERGY_BALL));
                }
                else if (match(pix, ROCKET))
                {
                    projectiles.add(new Projectile(assets, this, x, y, Projectile.ROCKET));
                }
                else if (match(pix, BLUECRYSTAL))
                {
                    crystals.add(new Crystal(assets, this, x, y, Crystal.BLUE));
                }
                else if (match(pix, GREENCRYSTAL))
                {
                    crystals.add(new Crystal(assets, this, x, y, Crystal.GREEN));
                }
                else if (match(pix, REDCRYSTAL))
                {
                    crystals.add(new Crystal(assets, this, x, y, Crystal.RED));
                }
                else if (match(pix, ARMOR_WARRIOR))
                {
                    pickupItems.add(new PickupItem(assets, this, x, y, PickupItem.ARMOR_WARRIOR));
                }
                else if (match(pix, MOVING_SPIKES))
                {
                    movingSpikes.add(new MovingSpikes(this, x, y));
                }
                else if (match(pix, SPIKES))
                {
                    spikes.add(new Spike(assets, this, x, y, Spike.NORMAL));
                }
                else if (match(pix, PLATFORM))
                {
                    platforms.add(new Platform(this, x, y));
                }
                else if (match(pix, LASER))
                {
                }
                else if (match(pix, END))
                {
                    endFlag = new EndFlag(x, y);
                }
                else if (match(pix, WATER))
                {
                    fluids.add(new Fluid(assets, this, x, y, Fluid.WATER));
                }
                else if (match(pix, LAVA))
                {
                    fluids.add(new Fluid(assets, this, x, y, Fluid.LAVA));
                }
                else if (match(pix, ACID))
                {
                    fluids.add(new Fluid(assets, this, x, y, Fluid.ACID));
                }
                else
                {
                    tiles[x][y] = pix;
                }
            }
        }
        Gdx.app.log("ProjectOne", "Map created successfully!");
    }

    private void loadPixmapEntities(Pixmap mapname)
    {
        Pixmap pixmap = mapname; // Map loading, providing width and height as well as structure
        width = pixmap.getWidth();
        height = pixmap.getHeight();
        tiles = new int[pixmap.getWidth()][pixmap.getHeight()];
        //fluids = new int[pixmap.getWidth()][pixmap.getHeight()];
        for (int y = 0; y < this.height; y++) // Using height from pixmap
        {
            for (int x = 0; x < this.width; x++) // Using width from pixmap
            {
                int pix = (pixmap.getPixel(x, y) >>> 8) & 0xffffff;
                //Gdx.app.log("Map", x + ", " + y + ", " + Integer.toHexString(pix)); // Display debug information about every pixel in log.
                if (match(pix, START))
                {
                    Checkpoint checkpoint = new Checkpoint(this, x, pixmap.getHeight() - 1 - y);
                    checkpoints.add(checkpoint);
                    activeCheckpoint = checkpoint;
                    activeCheckpoint.active = true;
                    player = new Player(assets, this, activeCheckpoint.bounds.x, activeCheckpoint.bounds.y);
                    player.state = Player.SPAWN;
                }
                else if (match(pix, CHECKPOINT))
                {
                    checkpoints.add(new Checkpoint(this, x, pixmap.getHeight() - 1 - y));
                }
                else if (match(pix, SPAWNER))
                {
                    spawners.add(new Spawner(this, x, pixmap.getHeight() - 1 - y));
                }
                else if (match(pix, ORC))
                {
                    monsters.add(new Monster(this.assets, this, x, pixmap.getHeight() - 1 - y, Monster.ORC));
                }
                else if (match(pix, ENERGY_BALL))
                {
                    projectiles.add(new Projectile(assets, this, x, pixmap.getHeight() - 1 - y, Projectile.ENERGY_BALL));
                }
                else if (match(pix, ROCKET))
                {
                    projectiles.add(new Projectile(assets, this, x, pixmap.getHeight() - 1 - y, Projectile.ROCKET));
                }
                else if (match(pix, BLUECRYSTAL))
                {
                    crystals.add(new Crystal(assets, this, x, pixmap.getHeight() - 1 - y, Crystal.BLUE));
                }
                else if (match(pix, GREENCRYSTAL))
                {
                    crystals.add(new Crystal(assets, this, x, pixmap.getHeight() - 1 - y, Crystal.GREEN));
                }
                else if (match(pix, REDCRYSTAL))
                {
                    crystals.add(new Crystal(assets, this, x, pixmap.getHeight() - 1 - y, Crystal.RED));
                }
                else if (match(pix, ARMOR_WARRIOR))
                {
                    pickupItems.add(new PickupItem(assets, this, x, pixmap.getHeight() - 1 - y, PickupItem.ARMOR_WARRIOR));
                }
                else if (match(pix, MOVING_SPIKES))
                {
                    movingSpikes.add(new MovingSpikes(this, x, pixmap.getHeight() - 1 - y));
                }
                else if (match(pix, SPIKES))
                {
                    spikes.add(new Spike(assets, this, x, pixmap.getHeight() - 1 - y, Spike.NORMAL));
                }
                else if (match(pix, PLATFORM))
                {
                    platforms.add(new Platform(this, x, pixmap.getHeight() - 1 - y));
                }
                else if (match(pix, LASER))
                {
                }
                else if (match(pix, END))
                {
                    endFlag = new EndFlag(x, pixmap.getHeight() - 1 - y);
                }
                else if (match(pix, WATER))
                {
                    fluids.add(new Fluid(assets, this, x, pixmap.getHeight() - 1 - y, Fluid.WATER));
                }
                else if (match(pix, LAVA))
                {
                    fluids.add(new Fluid(assets, this, x, pixmap.getHeight() - 1 - y, Fluid.LAVA));
                }
                else if (match(pix, ACID))
                {
                    fluids.add(new Fluid(assets, this, x, pixmap.getHeight() - 1 - y, Fluid.ACID));
                }
                else
                {
                    tiles[x][y] = pix;
                }
            }
        }
        Gdx.app.log("ProjectOne", "Map created successfully!");
    }

    private void initializeEntities()
    {
        // Entities initialization:

        for (int i = 0; i < movingSpikes.size; i++) // Create movingSpikes
        {
            movingSpikes.get(i).init();
        }

        //		for (int i = 0; i < platforms.size; i++) // Create Platforms
        //		{
        //			platforms.get(i).init();
        //		}
    }

    public boolean match(int src, int dst)
    {
        return src == dst;
    }

    public void update(float deltaTime)
    {
        player.update(deltaTime);
        if (player.state == Player.DEAD)
        {
            // If player dier, he looses one health
            player.health -= 1;
            // Backup health, score, crystals
            savedHealth = player.health;
            savedScore = player.score;
            savedCrystals = player.crystals;
            // Respawns at last touched checkpoint
            player = new Player(assets, this, activeCheckpoint.bounds.x, activeCheckpoint.bounds.y);
            // Restore health, score, crystals
            player.health = savedHealth;
            player.score = savedScore;
            player.crystals = savedCrystals;
        }
        for (int i = 0; i < checkpoints.size; i++)
        {
            if (player.bounds.overlaps(checkpoints.get(i).bounds))
            {
                activeCheckpoint.active = false;
                activeCheckpoint = checkpoints.get(i);
                activeCheckpoint.active = true;
                //savedScore = player.score;
            }
        }
        for (Monster monster : monsters) // Modify every update to this structure
        {
            monster.update(deltaTime);
            if (monster.state == Monster.DEAD)
                monsters.removeValue(monster, true);
        }
        for (int i = 0; i < projectiles.size; i++)
        {
            if (this.player.pos.dst2(projectiles.get(i).pos) < 80f)
                projectiles.get(i).update(deltaTime);
        }
        for (int i = 0; i < crystals.size; i++)
        {
            if (this.player.pos.dst2(crystals.get(i).pos) < 40f)
            {
                crystals.get(i).update(deltaTime);
                if (crystals.get(i).collected)
                    crystals.removeIndex(i);
            }
        }
        for (int i = 0; i < pickupItems.size; i++)
        {
            if (this.player.pos.dst2(pickupItems.get(i).pos) < 40f)
            {
                pickupItems.get(i).update(deltaTime);
                if (pickupItems.get(i).collected)
                    pickupItems.removeIndex(i);
            }
        }
        for (int i = 0; i < movingSpikes.size; i++)
        {
            //			MovingSpikes spikes = movingSpikes.get(i);
            //			spikes.update(deltaTime);
            if (this.player.pos.dst2(movingSpikes.get(i).pos) < 240f)
                movingSpikes.get(i).update(deltaTime);
        }
        //		for (int i = 0; i < platforms.size; i++)
        //		{
        //			//			Platform platform = platforms.get(i);
        //			//			platform.update(deltaTime);
        //		}
        for (int i = 0; i < fluids.size; i++)
        {
            //			Fluid fluid = fluids.get(i);
            //			fluid.update(deltaTime);
            if (this.player.pos.dst2(fluids.get(i).pos) < 40f)
                fluids.get(i).update(deltaTime);
        }
        for (int i = 0; i < spikes.size; i++)
        {
            //			Fluid fluid = fluids.get(i);
            //			fluid.update(deltaTime);
            if (this.player.pos.dst2(spikes.get(i).pos) < 40f)
                spikes.get(i).update(deltaTime);
        }
    }

    public void dispose()
    {
        player.dispose();

        for (int i = 0; i < projectiles.size; i++)
            projectiles.clear();
        for (int i = 0; i < crystals.size; i++)
            crystals.clear();
        for (int i = 0; i < fluids.size; i++)
            fluids.clear();
        for (int i = 0; i < spikes.size; i++)
            spikes.clear();
        for (int i = 0; i < blocks.size; i++)
            blocks.clear();
        monsters.clear();
    }
}
