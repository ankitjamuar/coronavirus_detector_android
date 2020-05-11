package in.tagbin.covid_tracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public final class DataReaderContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DataReaderContract() {}

    /* Inner class that defines the table contents */
    public static class Pass implements BaseColumns {
        public static final String TABLE_NAME = "passes";
        public static final String COLUMN_NAME_CATEGORY = "category";
        public static final String COLUMN_NAME_FROM = "from_place";
        public static final String COLUMN_NAME_TO = "to_place";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_REASON = "reason";
        public static final String COLUMN_NAME_DOC = "doc";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_DJANGO_ID = "django_id";
        public static final String COLUMN_NAME_IS_SYNCED = "is_synced";

    }
}

