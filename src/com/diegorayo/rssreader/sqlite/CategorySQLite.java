package com.diegorayo.rssreader.sqlite;

import java.util.ArrayList;
import java.util.List;

import com.diegorayo.rssreader.entitys.Category;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CategorySQLite {

	private SQLiteDatabase db;

	public CategorySQLite(SQLiteDatabase db) {
		this.db = db;
	}

	public Category createCategory(Category category) {

		if (category != null) {
			ContentValues values = new ContentValues();
			values.put("name", category.getName());
			long idRow = db.insert("category", null, values);
			if (idRow != -1) {
				category.setId((int) idRow);
				return category;
			}
		}

		return null;

	}

	public Category editCategory(Category category) {

		if (category != null) {
			ContentValues values = new ContentValues();
			values.put("name", category.getName());
			String whereArgs[] = new String[] { category.getId() + "" };
			long idRow = db.update("category", values, "id = ?", whereArgs);
			if (idRow != -1) {
				return category;
			}
		}

		return null;

	}

	public boolean deleteCategory(int idCategory) {

		String whereArgs[] = new String[] { idCategory + "" };
		long idRow = db.delete("category", "id = ?", whereArgs);
		if (idRow != 0) {
			return true;
		}

		return false;
	}

	public Category getCategoryById(int idCategory) {

		String[] columns = new String[] { "id", "name" };
		String[] whereArgs = new String[] { idCategory + "" };
		Cursor selection = db.query("category", columns, "id = ?", whereArgs,
				null, null, null);

		if (selection.moveToFirst()) {
			Category category = new Category();
			category.setId(selection.getInt(0));
			category.setName(selection.getString(1));
			return category;
		}

		return null;
	}

	public List<Category> getListAllCategories() {
		String[] columns = new String[] { "id", "name" };
		Cursor selection = db.query("category", columns, null, null, null,
				null, null);
		List<Category> listCategories = new ArrayList<Category>();

		if (selection.moveToFirst()) {
			do {
				Category category = new Category();
				category.setId(selection.getInt(0));
				category.setName(selection.getString(1));
				listCategories.add(category);
			} while (selection.moveToNext());

		}

		return listCategories;
	}

}
