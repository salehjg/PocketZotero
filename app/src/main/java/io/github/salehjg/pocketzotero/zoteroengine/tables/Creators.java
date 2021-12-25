package io.github.salehjg.pocketzotero.zoteroengine.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.Vector;

import io.github.salehjg.pocketzotero.zoteroengine.types.Creator;

public class Creators extends BaseData{
    protected final String TABLE_NAME = "creators";

    public void createTable(SQLiteDatabase db) {
        String CREATE_TABLE_COLLECTIONS =
                "CREATE TABLE \"" +TABLE_NAME + "\" ( " +
                        "\"creatorID\" INTEGER, " +
                        "\"firstName\" TEXT, " +
                        "\"lastName\" TEXT, " +
                        "\"fieldMode\" INTEGER" +
                        ")";
        db.execSQL(CREATE_TABLE_COLLECTIONS);
    }

    public void deleteTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + get_table_name());
    }

    public Creator getCreatorFor(int creatorId, SQLiteDatabase db){
        Cursor cursor = db.rawQuery("SELECT firstName, lastName FROM \"" + get_table_name() + "\" WHERE creatorID=\"" + creatorId + "\";", null);
        assert cursor.getCount()==1;
        String firstName = "?";
        String lastName = "?";
        while (cursor.moveToNext()){
            firstName = cursor.getString(0);
            lastName = cursor.getString(1);
        }
        cursor.close();

        return new Creator(firstName, lastName);
    }

    public Vector<Creator> getCreatorsDetails(Vector<Creator> creators, SQLiteDatabase db){
        for(Creator creator:creators){
            Cursor cursor = db.rawQuery("SELECT firstName, lastName FROM \"" + get_table_name() + "\" WHERE creatorID=\"" + creator.getCreatorId() + "\";", null);
            while (cursor.moveToNext()){
                creator.setFirstName(cursor.getString(0)); ;
                creator.setLastName(cursor.getString(1));
            }
            cursor.close();
        }
        return creators;
    }

    @Override
    public String get_table_name() {
        return TABLE_NAME;
    }
}
