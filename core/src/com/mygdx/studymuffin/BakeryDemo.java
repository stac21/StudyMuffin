package com.mygdx.studymuffin;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.HashMap;

public class BakeryDemo extends ApplicationAdapter {
	private SpriteBatch batch;
	private Stage stage;
	private Skin skin;
	// the upgrades that the user has purchased and that are active in the game world
	public static ArrayList<BakeryUpgrade> upgrades;
	// the list of customers currently in the bakery
	public static ArrayList<Customer> customers;
	// gets the json file from the onResume method and loads the upgrades from it
	public static String upgradesJson;
	private Label moneyLabel;
	private static int money;
	private static float moneyMultiplier;
	private float time;
	public static int WORLD_WIDTH;
	public static int WORLD_HEIGHT;
	private Texture background;
	// the min and max amounts of time, in seconds, that the user must wait until a customer comes
	private int minWait = 5;
	private int maxWait = 15;
	// the actual amount of time, in seconds, that the user must wait until a customer comes
	private int waitTime;
	// the amount of time that has elapsed since the last customer came in
	private int timeSinceCustomer;
	// the amount of time that a customer will stay in the store before leaving
	public static final int CUSTOMER_STAY_TIME = 8;

	// paths of the image files
	public static final String BACKGROUND_IMG = "background.jpg";
	public static final String BRONZE_OVEN_IMG = "bronze_oven.png";
	public static final String SILVER_OVEN_IMG = "silver_oven.png";
	public static final String GOLD_OVEN_IMG = "gold_oven.png";
	public static final String MALE_CHEF_IMG = "male_chef.png";
	public static final String FEMALE_CHEF_IMG = "female_chef.png";

	// names of the upgrades
	public static final String BRONZE_OVEN_NAME = "Bronze Oven";
	public static final String SILVER_OVEN_NAME = "Silver Oven";
	public static final String CHEF_NAME = "Chef";

	// money multipliers
	public static final int BRONZE_OVEN_MULTIPLIER = 1;
	public static final int SILVER_OVEN_MULTIPLIER = 2;
	public static final int CHEF_MULTIPLIER = 3;

	// prices
	public static final int BRONZE_OVEN_PRICE = 0;
	public static final int SILVER_OVEN_PRICE = 10;
	public static final int CHEF_PRICE = 20;

	// coordinates

	// widths and heights
	public static final int OVEN_WIDTH = 200, OVEN_HEIGHT = 200;
	public static final int CHEF_WIDTH = 90, CHEF_HEIGHT = 200;

	@Override
	public void create () {
		this.batch = new SpriteBatch();
		Sprite sprite = new Sprite(new Texture(Gdx.files.internal("badlogic.jpg")));
		sprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		this.skin = new Skin(Gdx.files.internal("uiskin.json"));
		this.stage = new Stage(new ScreenViewport());

		WORLD_WIDTH = (int) this.stage.getWidth();
		WORLD_HEIGHT = (int) this.stage.getHeight();
		background = new Texture(BACKGROUND_IMG);

		Table table = new Table();
		table.setWidth(WORLD_WIDTH);
		table.align(Align.center | Align.top);
		table.padTop(30);
		// table.setBackground(new SpriteDrawable(new Sprite(
				// new Texture(Gdx.files.internal(BACKGROUND_IMG)))));

		table.setFillParent(true);

		final TextButton textButton = new TextButton("Upgrade Shop", skin, "default");
		textButton.getLabel().setFontScale(3.0f);

		this.waitTime = (int) (Math.random() * (maxWait - minWait + 1) + minWait);
		this.timeSinceCustomer = 0;

		this.time = 0;

		moneyMultiplier = 1;

		// read the upgrades from the
		if (upgradesJson != null) {
			System.out.println(upgradesJson);
			Json json = new Json();
			json.setUsePrototypes(false);
			json.setIgnoreUnknownFields(true);
			json.setOutputType(JsonWriter.OutputType.json);

			upgrades = json.fromJson(ArrayList.class, BakeryUpgrade.class, upgradesJson);

			System.out.println(upgrades);
		} else {
			upgrades = new ArrayList<>();
		}


		if (upgrades.size() == 0) {
			upgrades.add(new BakeryUpgrade(BRONZE_OVEN_IMG, BRONZE_OVEN_NAME, BRONZE_OVEN_MULTIPLIER,
					BRONZE_OVEN_PRICE, WORLD_WIDTH / 2, WORLD_HEIGHT - 2 * OVEN_HEIGHT,
					OVEN_WIDTH, OVEN_HEIGHT));
		}

		for (BakeryUpgrade upgrade : upgrades) {
			moneyMultiplier *= upgrade.getMoneyMultiplier();
			System.out.println("moneyMultiplier == " + moneyMultiplier);
			System.out.println("upgrade's multiplier == " + upgrade.getMoneyMultiplier());
		}

		customers = new ArrayList<Customer>();

		this.moneyLabel = new Label("$" + money, skin, "default"	);
		this.moneyLabel.setFontScale(2.5f);

		final BakeryMenu bakeryMenu = new BakeryMenu("Bakery Menu", this.skin);
		bakeryMenu.addItem(new BakeryUpgrade(SILVER_OVEN_IMG, SILVER_OVEN_NAME,
				SILVER_OVEN_MULTIPLIER, SILVER_OVEN_PRICE, WORLD_WIDTH / 4,
				WORLD_HEIGHT - 2 * OVEN_HEIGHT, OVEN_WIDTH, OVEN_HEIGHT));
		bakeryMenu.addItem(new BakeryUpgrade(MALE_CHEF_IMG, CHEF_NAME, CHEF_MULTIPLIER,
				CHEF_PRICE, (int) (WORLD_WIDTH / 1.5), WORLD_HEIGHT - CHEF_HEIGHT, CHEF_WIDTH,
				CHEF_HEIGHT));

		textButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				bakeryMenu.show(stage);
			}
		});

		// TODO make the size of the UI elements resolution dependent
		table.row().colspan(3).expandX().fillX();
		table.add(textButton).width(300).height(100).expandX().fillX();
		table.add(moneyLabel).expandX().fillX();

		this.stage.addActor(table);

		Gdx.input.setInputProcessor(this.stage);
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		float delta = Gdx.graphics.getDeltaTime();

		// draw the background and upgrades
		batch.begin();

		// draw the background
		batch.draw(background, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);

		for (BakeryUpgrade upgrade : upgrades) {
			upgrade.draw(batch);
		}

		for (int i = 0; i < customers.size(); i++) {
			Customer customer = customers.get(i);
			customer.addTimeSinceArrived(delta);

			if (customer.getTimeSinceArrived() >= CUSTOMER_STAY_TIME) {
				customers.remove(customer);
				i--;
			} else {
				customer.draw(batch);
			}
		}

		batch.end();

		// draw the UI elements
		this.stage.act(delta);
		this.time += delta;

		if (this.time >= 1) {
			this.time -= 1;
			this.timeSinceCustomer++;

			if (this.timeSinceCustomer == this.waitTime) {
				// reset the time since a customer has arrived back to 0
				this.timeSinceCustomer = 0;
				// recalculate the wait time
				this.waitTime = (int) (Math.random() * ((maxWait - minWait) + 1) + minWait);

				// add the customer image to the game world
				customers.add(new Customer());

				money += moneyMultiplier;
				this.moneyLabel.setText("$" + money);
			}
		}

		this.stage.draw();
	}
	
	@Override
	public void dispose () {
		this.stage.dispose();
		this.skin.dispose();
	}

	public static void addUpgradeToWorld(BakeryUpgrade upgrade) {
		if (!upgrades.contains(upgrade)) {
			upgrades.add(upgrade);
			moneyMultiplier *= upgrade.getMoneyMultiplier();
		} else {
			System.out.println("Upgrade already purchased");
		}
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int mMoney) {
		money = mMoney;
	}
}
