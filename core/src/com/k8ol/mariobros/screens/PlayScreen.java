package com.k8ol.mariobros.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.k8ol.mariobros.MarioBros;
import com.k8ol.mariobros.scenes.HUD;
import com.k8ol.mariobros.sprites.Enemies.Enemy;
import com.k8ol.mariobros.sprites.Items.Item;
import com.k8ol.mariobros.sprites.Items.ItemDef;
import com.k8ol.mariobros.sprites.Items.Mushroom;
import com.k8ol.mariobros.sprites.Mario;
import com.k8ol.mariobros.tools.B2WorldCreator;
import com.k8ol.mariobros.tools.WorldContactListener;

import java.util.PriorityQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class PlayScreen implements Screen {
    public MarioBros game;
    private TextureAtlas atlas;
    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private HUD hud;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private World world;
    private B2WorldCreator creator;

    private Mario mario;
    private Music music;
    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;
    public boolean completedLevel;
    private int width, height;

    public PlayScreen(MarioBros game, String level) {
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();
        completedLevel = false;
        atlas = new TextureAtlas("Mario_And_Enemies.atlas");

        this.game = game;
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(MarioBros.V_WIDTH / MarioBros.PPM, (float) MarioBros.V_HEIGHT / MarioBros.PPM, gameCam);
        hud = new HUD(game.batch);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load(level);
        renderer = new OrthogonalTiledMapRenderer(map, 1 / MarioBros.PPM);

        gameCam.position.set(gamePort.getWorldWidth()/2, gamePort.getWorldHeight()/2, 0);

        world = new World(new Vector2(0, -10), true);

        creator = new B2WorldCreator(this);

        mario = new Mario(this);

        world.setContactListener(new WorldContactListener());

        music = MarioBros.manager.get("audio/music/mario_music.ogg", Music.class);
        music.setLooping(true);
        music.play();

        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();
    }

    public void spawnItem(ItemDef idef) {
        itemsToSpawn.add(idef);
    }

    public void handleSpawningItems() {
        if (!itemsToSpawn.isEmpty()) {
            ItemDef idef = itemsToSpawn.poll();
            if (idef.type == Mushroom.class) {
                items.add(new Mushroom(this, idef.position.x, idef.position.y));
            }
        }
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    @Override
    public void show() {

    }

    public void handleInput(float dt) {
        if (mario.currentState != Mario.State.DEAD) {
            if (!mario.flagged) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
                    if (mario.onGround()) {
                        mario.b2body.applyLinearImpulse(new Vector2(0, 4f), mario.b2body.getWorldCenter(), true);
                        (MarioBros.manager.get("audio/sounds/jump.wav", Sound.class)).play();
                    }
                }
                if (Gdx.input.isKeyPressed(Input.Keys.D) && mario.b2body.getLinearVelocity().x <= 2)
                    mario.b2body.applyLinearImpulse(new Vector2(0.1f, 0f), mario.b2body.getWorldCenter(), true);
                if (Gdx.input.isKeyPressed(Input.Keys.A) && mario.b2body.getLinearVelocity().x >= -2)
                    mario.b2body.applyLinearImpulse(new Vector2(-0.1f, 0f), mario.b2body.getWorldCenter(), true);
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
            if (Gdx.graphics.isFullscreen()) {
                Gdx.graphics.setWindowedMode(width, height);
            } else {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            }
        }
    }

    public void update(float dt) {
        handleInput(dt);
        handleSpawningItems();

        world.step(1/60f, 6, 2);

        mario.update(dt);
        for (Enemy enemy: creator.getEnemies()) {
            enemy.update(dt);
            if (enemy.getX() < mario.getX() + 224 / MarioBros.PPM) {
                enemy.b2body.setActive(true);
            }
        }

        for (Item item: items) {
            item.update(dt);
        }

        hud.update(dt);

        if (mario.currentState != Mario.State.DEAD) {
            if (mario.b2body.getLinearVelocity().x > 0 && mario.getX() > (16 / MarioBros.PPM) + gameCam.position.x) {
                gameCam.position.x += mario.b2body.getLinearVelocity().x * dt;
            }
        }

        gameCam.update();

        renderer.setView(gameCam);
    }

    @Override
    public void render(float delta) {
        update(delta);

        ScreenUtils.clear(0, 0, 0, 1);

        renderer.render();

        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        mario.draw(game.batch);
        for (Enemy enemy: creator.getEnemies()) enemy.draw(game.batch);
        for (Item item: items) item.draw(game.batch);
        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        if (gameOver()) {
            game.setScreen(new GameOverScreen(game));
            dispose();
        }
        if (completedLevel) {
            game.nextLevel();
            dispose();
        }
    }

    public boolean gameOver() {
        if (mario.currentState == Mario.State.DEAD && mario.getStateTimer() > 3) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void resize(int width, int height) {
        if (!Gdx.graphics.isFullscreen()) {
            width = Gdx.graphics.getWidth();
            height = Gdx.graphics.getHeight();
        }
        gamePort.update(width, height);
    }

    public TiledMap getMap() {
        return map;
    }
    public World getWorld() {
        return world;
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        hud.dispose();
    }
}
