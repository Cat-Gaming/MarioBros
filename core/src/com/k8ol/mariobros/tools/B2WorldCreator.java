package com.k8ol.mariobros.tools;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.k8ol.mariobros.MarioBros;
import com.k8ol.mariobros.screens.PlayScreen;
import com.k8ol.mariobros.sprites.Enemies.Enemy;
import com.k8ol.mariobros.sprites.Enemies.Koopa;
import com.k8ol.mariobros.sprites.TileObjects.Brick;
import com.k8ol.mariobros.sprites.TileObjects.Coin;
import com.k8ol.mariobros.sprites.Enemies.Goomba;

public class B2WorldCreator {
    private Array<Goomba> goombas;
    private Array<Koopa> koopas;

    public B2WorldCreator(PlayScreen screen) {
        World world = screen.getWorld();
        TiledMap map = screen.getMap();
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        // Ground layer
        for (RectangleMapObject object: map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = object.getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth()/2) / MarioBros.PPM, (rect.getY() + rect.getHeight()/2) / MarioBros.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth()/2 / MarioBros.PPM, rect.getHeight()/2 / MarioBros.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = MarioBros.OBJECT_BIT;
            body.createFixture(fdef);
        }

        // Pipe layer
        for (RectangleMapObject object: map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = object.getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth()/2) / MarioBros.PPM, (rect.getY() + rect.getHeight()/2) / MarioBros.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth()/2 / MarioBros.PPM, rect.getHeight()/2 / MarioBros.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = MarioBros.OBJECT_BIT;
            body.createFixture(fdef);
        }

        // Brick layer
        for (RectangleMapObject object: map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = object.getRectangle();

            new Brick(screen, object);
        }

        // Coin layer
        for (RectangleMapObject object: map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = object.getRectangle();

           new Coin(screen, object);
        }

        // create goombas
        goombas = new Array<Goomba>();
        for (RectangleMapObject object: map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = object.getRectangle();

            goombas.add(new Goomba(screen, rect.getX() / MarioBros.PPM, rect.getY() / MarioBros.PPM));
        }

        // create koopas
        koopas = new Array<Koopa>();
        for (RectangleMapObject object: map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = object.getRectangle();

            koopas.add(new Koopa(screen, rect.getX() / MarioBros.PPM, rect.getY() / MarioBros.PPM));
        }

        // create flag
        for (RectangleMapObject object: map.getLayers().get(8).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = object.getRectangle();
            MarioBros.setFlagX(rect.getX());
        }
    }

    public Array<Enemy> getEnemies() {
        Array<Enemy> enemies = new Array<Enemy>();
        enemies.addAll(goombas);
        enemies.addAll(koopas);
        return enemies;
    }
}
