package io.magics.baking.data.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import io.magics.baking.models.Recipes;

@Database(entities = {Recipes.class}, version = 1)
public abstract class RecipeRoomDb extends RoomDatabase {

    public abstract RecipeDao recipeDao();

    private static RecipeRoomDb INSTANCE;

    static RecipeRoomDb getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RecipeRoomDb.class) {
                if (INSTANCE == null) {

                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), RecipeRoomDb.class,
                            "recipes_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(roomDbCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback roomDbCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

        }
    };


}
