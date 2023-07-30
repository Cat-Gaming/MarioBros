package com.k8ol.mariobros;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.k8ol.mariobros.scenes.HUD;
import com.k8ol.mariobros.screens.PlayScreen;

public class MarioBros extends Game {
	public static final int V_WIDTH = 400, V_HEIGHT = 208;
	public static final float PPM = 100f;

	public static final short NOTHING_BIT = 0;
	public static final short GROUND_BIT = 1;
	public static final short MARIO_BIT = 2;
	public static final short BRICK_BIT = 4;
	public static final short COIN_BIT = 8;
	public static final short DESTROYED_BIT = 16;
	public static final short OBJECT_BIT = 32;
	public static final short ENEMY_BIT = 64;
	public static final short ENEMY_HEAD_BIT = 128;
	public static final short ITEM_BIT = 256;
	public static final short MARIO_HEAD_BIT = 512;
	public static final short FLAG_POLE_BIT = 1024;
	public static float flagX;
	public SpriteBatch batch;
	public static AssetManager manager;
	private String worldFile;
	private static int index;

	public static void setFlagX(float x) {
		flagX = x;
	}

	public static int getLevel() {
		return index;
	}

	@Override
	public void create() {
		worldFile = "level1.tmx";
		index = 1;
		batch = new SpriteBatch();
		manager = new AssetManager();
		manager.load("audio/music/mario_music.ogg", Music.class);
		manager.load("audio/music/level_complete.ogg", Music.class);

		manager.load("audio/sounds/jump.wav", Sound.class);
		manager.load("audio/sounds/coin.wav", Sound.class);
		manager.load("audio/sounds/bump.wav", Sound.class);
		manager.load("audio/sounds/breakblock.wav", Sound.class);
		manager.load("audio/sounds/powerup_spawn.wav", Sound.class);
		manager.load("audio/sounds/powerup.wav", Sound.class);
		manager.load("audio/sounds/powerdown.wav", Sound.class);
		manager.load("audio/sounds/stomp.wav", Sound.class);
		manager.load("audio/sounds/mariodie.wav", Sound.class);
		manager.load("audio/sounds/flag_pole_slide.wav", Sound.class);

		manager.finishLoading();

		setScreen(new PlayScreen(this, worldFile));
	}

	public void nextLevel() {
		index++;
		worldFile = "level" + index + ".tmx";
		HUD.updateLevel();
		setScreen(new PlayScreen(this, worldFile));
	}

	@Override
	public void dispose() {
		super.dispose();
		manager.dispose();
		batch.dispose();
	}
}
