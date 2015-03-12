package com.diegorayo.readerss.sqlite;

import java.util.ArrayList;
import java.util.List;

import com.diegorayo.readerss.entitys.Category;
import com.diegorayo.readerss.exceptions.DataBaseTransactionException;
import com.diegorayo.readerss.exceptions.NullEntityException;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author Diego Rayo
 * @version 2 <br />
 *          Clase que contiene los metodos (CRUD) de la entidad Category.
 *          Utiliza SQLite
 */
public class CategorySQLite {

	/**
	 * Conexion a la base de datos
	 */
	private SQLiteDatabase db;

	/**
	 * 
	 * @param db
	 *            - Conexion a la base de datos
	 */
	public CategorySQLite(SQLiteDatabase db) {

		this.db = db;
	}

	public Category create(Category category) throws NullEntityException,
			DataBaseTransactionException {

		ContentValues values = new ContentValues();
		values.put("name", category.getName());

		long idRow = db.insert("category", null, values);

		if (idRow != -1) {

			category.setId((int) idRow);
			return category;
		}

		throw new DataBaseTransactionException(
				DataBaseTransactionException.INSERT_OPERATION,
				Category.class.getSimpleName());
	}

	public Category edit(Category category)
			throws DataBaseTransactionException, NullEntityException {

		ContentValues values = new ContentValues();
		values.put("name", category.getName());
		String whereArgs[] = new String[] { category.getId() + "" };

		long idRow = db.update("category", values, "id = ?", whereArgs);

		if (idRow != -1) {

			return category;
		}

		throw new DataBaseTransactionException(
				DataBaseTransactionException.UPDATE_OPERATION,
				Category.class.getSimpleName());
	}

	public boolean deleteCategory(int idCategory)
			throws DataBaseTransactionException {

		String whereArgs[] = new String[] { idCategory + "" };

		long idRow = db.delete("category", "id = ?", whereArgs);

		if (idRow == 1) {

			return true;
		}

		throw new DataBaseTransactionException(
				DataBaseTransactionException.DELETE_OPERATION,
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

		List<Category> categoryList = new ArrayList<Category>();
		String[] columns = new String[] { "id", "name" };

		Cursor selection = db.query("category", columns, null, null, null,
				null, "name asc");

		if (selection.moveToFirst()) {

			do {

				Category category = new Category();
				category.setId(selection.getInt(0));
				category.setName(selection.getString(1));

				categoryList.add(category);

			} while (selection.moveToNext());
		}

		return categoryList;
	}

}
