package com.mygdx.studymuffin;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public class BakeryUpgrade {
    private String texturePath;
    private transient Texture texture;
    private String name;
    private float moneyMultiplier;
    private float price;
    private int xPos;
    private int yPos;
    private int width;
    private int height;

    public BakeryUpgrade() {}

    public BakeryUpgrade(String menuTexturePath, String name, float moneyMultiplier, float price,
                         int xPos, int yPos, int width, int height) {
        this.texturePath = menuTexturePath;
        this.texture = new Texture(menuTexturePath);
        this.name = name;
        this.moneyMultiplier = moneyMultiplier;
        this.price = price;
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.height = height;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public void setTexturePath(String texturePath) {
        this.texturePath = texturePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getMoneyMultiplier() {
        return moneyMultiplier;
    }

    public void setMoneyMultiplier(float moneyMultiplier) {
        this.moneyMultiplier = moneyMultiplier;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getxPos() {
        return xPos;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public void setyPos(int yPos) {
        this.yPos = yPos;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void draw(Batch batch) {
        if (this.texture == null) {
            this.texture = new Texture(this.texturePath);
        }

        batch.draw(texture, xPos, yPos, width, height);
    }
}
