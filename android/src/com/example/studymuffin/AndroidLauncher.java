package com.example.studymuffin;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.mygdx.studymuffin.BakeryDemo;

public class AndroidLauncher extends FragmentActivity implements AndroidFragmentApplication.Callbacks {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bakery);

		// create libgdx fragment
		GameFragment libgdxFragment = new GameFragment();

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.content_frame_layout, libgdxFragment);
		transaction.commit();
	}

	public static class GameFragment extends AndroidFragmentApplication {
		private static BakeryDemo demo;
		private View view;
		public static String MONEY_FILE = "com.example.studymuffin.money_file";
		public static String UPGRADES_FILE = "com.example.studymuffin.upgrades_file";
		FirebaseDatabase database;
		DatabaseReference moneyRef;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			demo = new BakeryDemo();

			this.view = initializeForView(demo);
			this.view.setBackgroundColor(Color.TRANSPARENT);

			this.database = FirebaseDatabase.getInstance();
			moneyRef = database.getReference("money");
			moneyRef.addValueEventListener(new ValueEventListener() {
				@Override
				public void onDataChange(@NonNull DataSnapshot snapshot) {
					int value = snapshot.getValue(Integer.class);
					demo.setMoney(value);
				}

				@Override
				public void onCancelled(@NonNull DatabaseError error) {
					System.out.println("Failed to read value. " + error.toException());
				}
			});

			return this.view;
		}

		@Override
		public void onPause() {
			super.onPause();
			System.out.println("On Pause called");

			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.view.getContext());
			SharedPreferences.Editor editor = sp.edit();

			String json = new Gson().toJson(BakeryDemo.upgrades);

			editor.putFloat(MONEY_FILE, demo.getMoney());
			editor.putString(UPGRADES_FILE, json);

			editor.apply();

			moneyRef.setValue(demo.getMoney());
		}

		@Override
		public void onResume() {
			super.onResume();

			System.out.println("OnResume called");

			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.view.getContext());

			float money = sp.getFloat(MONEY_FILE, 0.0f);
			String json = sp.getString(UPGRADES_FILE, null);

			demo.setMoney(money);
			BakeryDemo.upgradesJson = json;
		}
	}

	@Override
	public void exit() {

	}
}
