package com.diegorayo.readerss.sqlite;

import java.util.ArrayList;
import java.util.List;

import com.diegorayo.readerss.entitys.Category;
import com.diegorayo.readerss.exceptions.DataBaseTransactionException;
import com.diegorayo.readerss.exceptions.EntityNullException;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author Diego Rayo
 * @version 1 <br />
 *          Description
 */
public class CategorySQLite {

	/**
	 * 
	 */
	private SQLiteDatabase db;

	/**
	 * 
	 * @param db
	 */
	public CategorySQLite(SQLiteDatabase db) {
		this.db = db;
	}

	public Category createCategory(Category category)
			throws EntityNullException, DataBaseTransactionException {

		if (category != null) {
			ContentValues values = new ContentValues();

			values.put("name", category.getName());

			long idRow = db.insert("category", null, values);

			if (idRow != -1) {
				category.setId((int) idRow);
				return category;
			}

			throw new DataBaseTransactionException(
					DataBaseTransactionException.OPERATION_INSERT,
					Category.class.getSimpleName());
		}

		throw new EntityNullException(Category.class.getSimpleName());
	}

	public Category editCategory(Category category)
			throws DataBaseTransactionException, EntityNullException {

		if (category != null) {
			ContentValues values = new ContentValues();
			values.put("name", category.getName());
			String whereArgs[] = new String[] { category.getId() + "" };

			long idRow = db.update("category", values, "id = ?", whereArgs);

			if (idRow != -1) {
				return category;
			}

			throw new DataBaseTransactionException(
					DataBaseTransactionException.OPERATION_UPDATE,
					Category.class.getSimpleName());
		}

		throw new EntityNullException(Category.class.getSimpleName());
	}

	public boolean deleteCategory(int idCategory)
			throws DataBaseTransactionException {

		String whereArgs[] = new String[] { idCategory + "" };

		long idRow = db.delete("category", "id = ?", whereArgs);

		if (idRow == 1) {
			return true;
		}

		throw new DataBaseTransactionException(
				DataBaseTransactionException.OPERATION_DELETE,
				Category.class.getSimpleName());
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

		List<Category> listCategories = new ArrayList<Category>();
		String[] columns = new String[] { "id", "name" };

		Cursor selection = db.query("category", columns, "id != 1", null, null,
				null, "name asc");

		Category defaultCategory = new Category("default");
		defaultCategory.setId(1);
		listCategories.add(0, defaultCategory);

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
