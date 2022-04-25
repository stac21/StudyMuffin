package com.mygdx.studymuffin;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public class Customer {
    private final Texture texture;
    private final int xPos, yPos;
    public static int WIDTH = 90, HEIGHT = 200;
    public static final String[] PERSON_IMGS = {
            "person1.png", "person2.png", "person3.png", "person4.png",
            "person5.png", "person6.png", "person7.png", "person8.png"
    };
    // the amount of time since this customer arrived at the store
    private float timeSinceArrived;

    public Customer() {
        int customerNum = (int) (Math.random() * PERSON_IMGS.length);

        this.texture = new Texture(PERSON_IMGS[customerNum]);
        this.xPos = (int) (Math.random() * BakeryDemo.WORLD_WIDTH - WIDTH);
        this.yPos = (int) (Math.random() * (BakeryDemo.WORLD_HEIGHT / 3));
        this.timeSinceArrived = 0;
    }

    public float getTimeSinceArrived() {
        return this.timeSinceArrived;
    }

    public void addTimeSinceArrived(float delta) {
        this.timeSinceArrived += delta;
    }

    public void draw(Batch batch) {
        batch.draw(this.texture, this.xPos, this.yPos, WIDTH, HEIGHT);
    }
}
