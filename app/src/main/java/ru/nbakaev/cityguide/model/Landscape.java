package ru.nbakaev.cityguide.model;

import java.util.ArrayList;

import ru.nbakaev.cityguide.R;


public class Landscape {

	private int imageID;
	private String title;
	private String description;

	public int getImageID() {
		return imageID;
	}

	public void setImageID(int imageID) {
		this.imageID = imageID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static ArrayList<Landscape> getData() {

		ArrayList<Landscape> dataList = new ArrayList<>();

		int[] images = getImages();

		for (int i = 0; i < images.length; i++) {

			Landscape landscape = new Landscape();
			landscape.setImageID(images[i]);
			landscape.setTitle("Landscape " + i);

			dataList.add(landscape);
		}

		return dataList;
	}

	public static int[] getImages() {
		return new int[] {
				R.drawable.thumb_1_0, R.drawable.thumb_1_1, R.drawable.thumb_1_2, R.drawable.thumb_1_3,
				R.drawable.thumb_1_4, R.drawable.thumb_1_5, R.drawable.thumb_1_6, R.drawable.thumb_1_7,
				R.drawable.thumb_1_8, R.drawable.thumb_1_9,

				R.drawable.thumb_2_0, R.drawable.thumb_2_1, R.drawable.thumb_2_2, R.drawable.thumb_2_3,
				R.drawable.thumb_2_4, R.drawable.thumb_2_5, R.drawable.thumb_2_6, R.drawable.thumb_2_7,
				R.drawable.thumb_2_8, R.drawable.thumb_2_9,

				R.drawable.thumb_3_0, R.drawable.thumb_3_1, R.drawable.thumb_3_2, R.drawable.thumb_3_3,
				R.drawable.thumb_3_4, R.drawable.thumb_3_5, R.drawable.thumb_3_6, R.drawable.thumb_3_7,
				R.drawable.thumb_3_8, R.drawable.thumb_3_9,

				R.drawable.thumb_4_0, R.drawable.thumb_4_1, R.drawable.thumb_4_2, R.drawable.thumb_4_3,
				R.drawable.thumb_4_4, R.drawable.thumb_4_5, R.drawable.thumb_4_6, R.drawable.thumb_4_7,
				R.drawable.thumb_4_8, R.drawable.thumb_4_9,

				R.drawable.thumb_5_0, R.drawable.thumb_5_1, R.drawable.thumb_5_2, R.drawable.thumb_5_3,
				R.drawable.thumb_5_4, R.drawable.thumb_5_5, R.drawable.thumb_5_6, R.drawable.thumb_5_7,
				R.drawable.thumb_5_8, R.drawable.thumb_5_9,

				R.drawable.thumb_6_0, R.drawable.thumb_6_1, R.drawable.thumb_6_2, R.drawable.thumb_6_3,
				R.drawable.thumb_6_4, R.drawable.thumb_6_5, R.drawable.thumb_6_6, R.drawable.thumb_6_7,
				R.drawable.thumb_6_8, R.drawable.thumb_6_9,

				R.drawable.thumb_7_0, R.drawable.thumb_7_1, R.drawable.thumb_7_2, R.drawable.thumb_7_3,
				R.drawable.thumb_7_4
		};
	}
}