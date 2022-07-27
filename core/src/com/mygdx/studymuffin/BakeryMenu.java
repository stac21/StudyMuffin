package com.mygdx.studymuffin;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import java.util.ArrayList;

public class BakeryMenu extends Dialog {
    private ArrayList<BakeryUpgrade> items;

    public BakeryMenu(String title, Skin skin) {
        super(title, skin);

        this.items = new ArrayList<>();
    }

    public BakeryMenu(String title, Skin skin, String windowStyleName) {
        super(title, skin, windowStyleName);

        this.items = new ArrayList<>();
    }

    public BakeryMenu(String title, WindowStyle windowStyle) {
        super(title, windowStyle);
    }

    // this section of code is called whenever any of the constructors are called
    {
        // adds a button labeled "Cancel" to the dialog
        this.button("Cancel");

        this.getTitleLabel().setFontScale(3.0f);
    }

    @Override
    protected void result(Object object) {
        super.result(object);
    }

    public void addItem(BakeryUpgrade item) {
        // ensure that there are always two items per row
        if (this.items.size() % 2 == 0) {
            this.getContentTable().row();
        }

        this.items.add(item);

        Table group = new Table();

        Sprite sprite = new Sprite(new Texture(Gdx.files.internal(item.getTexturePath())));

        Image image = new Image(sprite);
        TextField name = new TextField(item.getName(), this.getSkin());

        group.add(image).width(300).height(300);
        group.add(name);

        final BakeryUpgrade finalItem = item;

        group.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (BakeryDemo.studyPoints >= finalItem.getPrice() && !isUpgradePurchased(finalItem)) {
                    BakeryDemo.addUpgradeToWorld(finalItem);
                    BakeryDemo.studyPoints -= finalItem.getPrice();
                } else {
                    System.out.println("Cannot purchase upragade: " + BakeryDemo.studyPoints);
                }
            }
        });

        this.getContentTable().add(group);
    }

    private static boolean isUpgradePurchased(BakeryUpgrade upgrade) {
        for (int i = 0; i < BakeryDemo.upgrades.size(); i++) {
            if (BakeryDemo.upgrades.get(i).getName().equals(upgrade.getName())) {
                return true;
            }
        }

        return false;
    }

    public ArrayList<BakeryUpgrade> getItems() {
        return this.items;
    }
}
