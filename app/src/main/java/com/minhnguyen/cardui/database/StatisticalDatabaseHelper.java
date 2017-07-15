package com.minhnguyen.cardui.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.minhnguyen.cardui.constant.StringValue;
import com.minhnguyen.cardui.model.Organization;
import com.minhnguyen.cardui.model.Statistic;
import com.minhnguyen.cardui.model.StatisticItem;
import com.minhnguyen.cardui.model.User;
import com.minhnguyen.cardui.utilities.Utilities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by minhnguyen on 2/23/17.
 */

public class StatisticalDatabaseHelper extends SQLiteOpenHelper {

    private static StatisticalDatabaseHelper sInstance;

    // Database information
    private static final String DATABASE_NAME = "statistical";
    private static final int DATABASE_VERSION = 1;

    // Table name
    private static final String TABLE_USERS = "users";
    private static final String TABLE_ORGANIZATIONS = "organizations";
    private static final String TABLE_LIST_MEETING = "list_meeting";
    private static final String TABLE_STATISTICS = "statistics";

    // Users columns
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_GENDER = "userGender";
    private static final String KEY_USER_POSITION = "userPosition";
    private static final String KEY_USER_ORGANIZATION = "userOrganization";
    private static final String KEY_USER_NUMBER = "userNumber";
    private static final String KEY_USER_BIRTHDAY = "userBirthday";
    private static final String KEY_USER_ID_NUMBER = "userIDNumber";
    private static final String KEY_USER_PHONE_NUMBER = "userPhoneNumber";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_PROFILE_PIC = "userProfilePic";

    // Organizations columns
    private static final String KEY_ORGANIZATION_ID = "organizationId";
    private static final String KEY_ORGANIZATION_NAME = "organizationName";

    // List Meeting columns
    private static final String KEY_MEETING_ID = "meetingId";
    private static final String KEY_MEETING_NAME = "meetingName";
    private static final String KEY_MEETING_DATE = "meetingDate";
    private static final String KEY_MEETING_TIME_START = "meetingTimeStart";
    private static final String KEY_MEETING_TIME_END = "meetingTimeEnd";

    // Statistical columns
    private static final String KEY_STATISTIC_ID = "statisId";
    private static final String KEY_STATISTIC_DATE = "statisDate";
    private static final String KEY_STATISTIC_IS_MEETING = "isMeeting";
    private static final String KEY_STATISTIC_MEETING_ID = "meetingId";
    private static final String KEY_STATISTIC_REASON_WORK_CONTACT = "reasonWorkContact";
    private static final String KEY_STATISTIC_USER_ID = "userId";
    private static final String KEY_STATISTIC_ORGANIZATION_ID_FK = "organizationId";

    public static synchronized StatisticalDatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        if (sInstance == null) {
            sInstance = new StatisticalDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private StatisticalDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "(" +
                KEY_USER_ID + " INTEGER PRIMARY KEY," +
                KEY_USER_NAME + " TEXT," +
                KEY_USER_GENDER + " TEXT," +
                KEY_USER_POSITION + " TEXT," +
                KEY_USER_ORGANIZATION + " TEXT," +
                KEY_USER_NUMBER + " TEXT," +
                KEY_USER_BIRTHDAY + " TEXT," +
                KEY_USER_ID_NUMBER + " TEXT," +
                KEY_USER_PHONE_NUMBER + " TEXT," +
                KEY_USER_EMAIL + " TEXT," +
                KEY_USER_PROFILE_PIC + " TEXT" + ")";

        String CREATE_ORGANIZATIONS_TABLE = "CREATE TABLE " + TABLE_ORGANIZATIONS + "(" +
                KEY_ORGANIZATION_ID + " INTEGER PRIMARY KEY," +
                KEY_ORGANIZATION_NAME + " TEXT" + ")";

        String CREATE_LIST_MEETING_TABLE = "CREATE TABLE " + TABLE_LIST_MEETING + "(" +
                KEY_MEETING_ID + " INTEGER PRIMARY KEY," +
                KEY_MEETING_NAME + " TEXT," +
                KEY_MEETING_DATE + " TEXT," +
                KEY_MEETING_TIME_START + " TEXT," +
                KEY_MEETING_TIME_END + " TEXT" + ")";

        String CREATE_STATISTICS_TABLE = "CREATE TABLE " + TABLE_STATISTICS + "(" +
                KEY_STATISTIC_ID + " INTEGER PRIMARY KEY," +
                KEY_STATISTIC_DATE + " TEXT," +
                KEY_STATISTIC_IS_MEETING + " TEXT," +
                KEY_STATISTIC_MEETING_ID + " TEXT," +
                KEY_STATISTIC_REASON_WORK_CONTACT + " TEXT," +
                KEY_STATISTIC_USER_ID + " INTEGER," +
                KEY_STATISTIC_ORGANIZATION_ID_FK + " INTEGER" + ")";

        sqLiteDatabase.execSQL(CREATE_USERS_TABLE);
        sqLiteDatabase.execSQL(CREATE_ORGANIZATIONS_TABLE);
        sqLiteDatabase.execSQL(CREATE_LIST_MEETING_TABLE);
        sqLiteDatabase.execSQL(CREATE_STATISTICS_TABLE);
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ORGANIZATIONS);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_LIST_MEETING);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_STATISTICS);
            onCreate(sqLiteDatabase);
        }
    }

    // Insert or update a user in the database
    // Since SQLite doesn't support "upsert" we need to fall back on an attempt to UPDATE (in case the
    // user already exists) optionally followed by an INSERT (in case the user does not already exist).
    // Unfortunately, there is a bug with the insertOnConflict method
    // (https://code.google.com/p/android/issues/detail?id=13045) so we need to fall back to the more
    // verbose option of querying for the user's primary key if we did an update.
    private long addOrUpdateUser(User user) {
        // The database connection is cached so it's not expensive to call getWriteableDatabase() multiple times.
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        long userId = -1;

        sqLiteDatabase.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_USER_NAME, user.getUserName());
            values.put(KEY_USER_POSITION, user.getUserPosition());
            values.put(KEY_USER_ORGANIZATION, user.getUserOrganization());
            values.put(KEY_USER_BIRTHDAY, user.getUserBirthday());
            values.put(KEY_USER_ID_NUMBER, user.getUserIDNumber());

            // First try to update the user in case the user already exists in the database
            // This assumes userNames are unique
            int rows = sqLiteDatabase.update(TABLE_USERS, values, KEY_USER_NAME + "= ?", new String[]{user.getUserName()});

            // Check if update succeeded
            if (rows == 1) {
                // Get the primary key of the user we just updated
                String usersSelectQuery = String.format("SELECT %s FROM %s WHERE %s = ?",
                        KEY_USER_ID, TABLE_USERS, KEY_USER_NAME);
                Cursor cursor = sqLiteDatabase.rawQuery(usersSelectQuery, new String[]{String.valueOf(user.getUserName())});
                try {
                    if (cursor.moveToFirst()) {
                        userId = cursor.getInt(0);
                        sqLiteDatabase.setTransactionSuccessful();
                    }
                } finally {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                }
            } else {
                // user with this userName did not already exist, so insert new user
                userId = sqLiteDatabase.insertOrThrow(TABLE_USERS, null, values);
                sqLiteDatabase.setTransactionSuccessful();
            }
        } catch (Exception e) {
            Log.d(StringValue.TAG, "Error while trying to add or update user");
        } finally {
            sqLiteDatabase.endTransaction();
        }

        return userId;
    }

    private long addOrUpdateOrganization(Organization organization) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        long organizationId = -1;

        sqLiteDatabase.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_ORGANIZATION_NAME, organization.getOrganizationName());

            int rows = sqLiteDatabase.update(TABLE_ORGANIZATIONS, values, KEY_ORGANIZATION_NAME + "= ?",
                    new String[]{organization.getOrganizationName()});

            // Check if update succeeded
            if (rows == 1) {
                // Get the primary key of the organization we just updated
                String organizationsSelectQuery = String.format("SELECT %s FROM %s WHERE %s = ?",
                        KEY_ORGANIZATION_ID, TABLE_ORGANIZATIONS, KEY_ORGANIZATION_NAME);
                Cursor cursor = sqLiteDatabase.rawQuery(organizationsSelectQuery,
                        new String[]{String.valueOf(organization.getOrganizationName())});
                try {
                    if (cursor.moveToFirst()) {
                        organizationId = cursor.getInt(0);
                        sqLiteDatabase.setTransactionSuccessful();
                    }
                } finally {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                }
            } else {
                // org with this organizationName did not already exist, so insert new organization
                organizationId = sqLiteDatabase.insertOrThrow(TABLE_ORGANIZATIONS, null, values);
                sqLiteDatabase.setTransactionSuccessful();
            }
        } catch (Exception e) {
            Log.d(StringValue.TAG, "Error while trying to add or update organization");
        } finally {
            sqLiteDatabase.endTransaction();
        }
        return organizationId;
    }

    // Insert a statistic into the database
    public void addStatistic(Statistic statistic) {
        // Create and/or open the database for writing
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        sqLiteDatabase.beginTransaction();
        try {
            // The user might already exist in the database (i.e. the same user created multiple statistic).
            long userId = addOrUpdateUser(statistic.getUser());
            long organizationId = addOrUpdateOrganization(statistic.getOrganization());

            ContentValues values = new ContentValues();
            values.put(KEY_STATISTIC_DATE, Utilities.getInstance().convertDateToStringLongDatabaseFormat(statistic.getDate()));
            values.put(KEY_STATISTIC_ORGANIZATION_ID_FK, organizationId);
            values.put(KEY_STATISTIC_USER_ID, userId);

            // SQLite auto increments the primary key column.
            sqLiteDatabase.insertOrThrow(TABLE_STATISTICS, null, values);
            sqLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(StringValue.TAG, "Error while trying to add statistic to database");
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    // Select all org from database
    public List<String> getAllOrg() {
        List<String> organizationList = Organization.createOrganizationsList();

        String ORGANIZATION_SELECT_QUERY = String.format("SELECT * FROM %S",
                TABLE_ORGANIZATIONS);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(ORGANIZATION_SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    String organizationName = cursor.getString(cursor.getColumnIndex(KEY_ORGANIZATION_NAME));

                    organizationList.add(organizationName);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(StringValue.TAG, "Error while trying to get organization from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return organizationList;
    }


    // Select thong ke
    public List<StatisticItem> getStatistical(String strOrganization, Date dateStart, Date dateEnd) {
        List<StatisticItem> statisticItems = new ArrayList<>();
        String strDateStart = Utilities.getInstance().convertDateToStringShortDatabaseFormat(dateStart);
        String strDateEnd = Utilities.getInstance().convertDateToStringShortDatabaseFormat(dateEnd);

        String SELECT_QUERY;
        if (null == strOrganization) {
            SELECT_QUERY = String.format("SELECT * FROM %s LEFT OUTER JOIN %s ON %s.%s = %s.%s " +
                            "WHERE %s BETWEEN %s AND %s ORDER BY %s DESC",
                    TABLE_STATISTICS,
                    TABLE_USERS,
                    TABLE_STATISTICS, KEY_STATISTIC_USER_ID,
                    TABLE_USERS, KEY_USER_ID,
                    KEY_STATISTIC_DATE, "'" + strDateStart + " 00:00:00'",
                    "'" + strDateEnd + " 23:59:59'", KEY_STATISTIC_DATE);
        } else {
            SELECT_QUERY = String.format("SELECT * FROM %s LEFT OUTER JOIN %s ON %s.%s = %s.%s " +
                            "WHERE (%s BETWEEN %s AND %s) AND (%s.%s = %s) ORDER BY %s DESC",
                    TABLE_STATISTICS,
                    TABLE_USERS,
                    TABLE_STATISTICS, KEY_STATISTIC_USER_ID,
                    TABLE_USERS, KEY_USER_ID,
                    KEY_STATISTIC_DATE, "'" + strDateStart + " 00:00:00'",
                    "'" + strDateEnd + " 23:59:59'", TABLE_USERS,
                    KEY_USER_ORGANIZATION, "'" + strOrganization + "'",
                    KEY_STATISTIC_DATE);
        }

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    StatisticItem statisticItem = new StatisticItem();
                    statisticItem.setName(cursor.getString(cursor.getColumnIndex(KEY_USER_NAME)));
                    statisticItem.setOrganization(cursor.getString(cursor.getColumnIndex(KEY_USER_ORGANIZATION)));
                    statisticItem.setDate(Utilities.getInstance()
                            .convertStringToDateLongDatabaseFormat(cursor.getString(cursor.getColumnIndex(KEY_STATISTIC_DATE))));

                    statisticItems.add(statisticItem);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(StringValue.TAG, "Error while trying to get statistic from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return statisticItems;
    }
}
