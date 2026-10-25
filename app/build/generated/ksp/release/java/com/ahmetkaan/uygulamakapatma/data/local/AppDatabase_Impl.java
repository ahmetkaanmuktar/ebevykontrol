package com.ahmetkaan.uygulamakapatma.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.ahmetkaan.uygulamakapatma.data.local.dao.AppLimitDao;
import com.ahmetkaan.uygulamakapatma.data.local.dao.AppLimitDao_Impl;
import com.ahmetkaan.uygulamakapatma.data.local.dao.ParentSettingsDao;
import com.ahmetkaan.uygulamakapatma.data.local.dao.ParentSettingsDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile AppLimitDao _appLimitDao;

  private volatile ParentSettingsDao _parentSettingsDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `app_limits` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `packageName` TEXT NOT NULL, `dailyLimitMinutes` INTEGER NOT NULL, `isPermanentlyBlocked` INTEGER NOT NULL, `extensionMinutes` INTEGER NOT NULL, `extensionDateMillis` INTEGER NOT NULL)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_app_limits_packageName` ON `app_limits` (`packageName`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `parent_settings` (`id` INTEGER NOT NULL, `pinHash` TEXT NOT NULL, `salt` TEXT NOT NULL, `failedAttempts` INTEGER NOT NULL, `lockoutUntilMillis` INTEGER NOT NULL, `securityQuestion` TEXT NOT NULL, `securityAnswerHash` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '80dca97792212088fe54df0c3a6ee849')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `app_limits`");
        db.execSQL("DROP TABLE IF EXISTS `parent_settings`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsAppLimits = new HashMap<String, TableInfo.Column>(6);
        _columnsAppLimits.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppLimits.put("packageName", new TableInfo.Column("packageName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppLimits.put("dailyLimitMinutes", new TableInfo.Column("dailyLimitMinutes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppLimits.put("isPermanentlyBlocked", new TableInfo.Column("isPermanentlyBlocked", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppLimits.put("extensionMinutes", new TableInfo.Column("extensionMinutes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAppLimits.put("extensionDateMillis", new TableInfo.Column("extensionDateMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAppLimits = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAppLimits = new HashSet<TableInfo.Index>(1);
        _indicesAppLimits.add(new TableInfo.Index("index_app_limits_packageName", true, Arrays.asList("packageName"), Arrays.asList("ASC")));
        final TableInfo _infoAppLimits = new TableInfo("app_limits", _columnsAppLimits, _foreignKeysAppLimits, _indicesAppLimits);
        final TableInfo _existingAppLimits = TableInfo.read(db, "app_limits");
        if (!_infoAppLimits.equals(_existingAppLimits)) {
          return new RoomOpenHelper.ValidationResult(false, "app_limits(com.ahmetkaan.uygulamakapatma.data.local.entity.AppLimit).\n"
                  + " Expected:\n" + _infoAppLimits + "\n"
                  + " Found:\n" + _existingAppLimits);
        }
        final HashMap<String, TableInfo.Column> _columnsParentSettings = new HashMap<String, TableInfo.Column>(7);
        _columnsParentSettings.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsParentSettings.put("pinHash", new TableInfo.Column("pinHash", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsParentSettings.put("salt", new TableInfo.Column("salt", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsParentSettings.put("failedAttempts", new TableInfo.Column("failedAttempts", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsParentSettings.put("lockoutUntilMillis", new TableInfo.Column("lockoutUntilMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsParentSettings.put("securityQuestion", new TableInfo.Column("securityQuestion", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsParentSettings.put("securityAnswerHash", new TableInfo.Column("securityAnswerHash", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysParentSettings = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesParentSettings = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoParentSettings = new TableInfo("parent_settings", _columnsParentSettings, _foreignKeysParentSettings, _indicesParentSettings);
        final TableInfo _existingParentSettings = TableInfo.read(db, "parent_settings");
        if (!_infoParentSettings.equals(_existingParentSettings)) {
          return new RoomOpenHelper.ValidationResult(false, "parent_settings(com.ahmetkaan.uygulamakapatma.data.local.entity.ParentSettings).\n"
                  + " Expected:\n" + _infoParentSettings + "\n"
                  + " Found:\n" + _existingParentSettings);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "80dca97792212088fe54df0c3a6ee849", "6c53dbbcfa05024ad22c60801aa9552e");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "app_limits","parent_settings");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `app_limits`");
      _db.execSQL("DELETE FROM `parent_settings`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(AppLimitDao.class, AppLimitDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ParentSettingsDao.class, ParentSettingsDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public AppLimitDao appLimitDao() {
    if (_appLimitDao != null) {
      return _appLimitDao;
    } else {
      synchronized(this) {
        if(_appLimitDao == null) {
          _appLimitDao = new AppLimitDao_Impl(this);
        }
        return _appLimitDao;
      }
    }
  }

  @Override
  public ParentSettingsDao parentSettingsDao() {
    if (_parentSettingsDao != null) {
      return _parentSettingsDao;
    } else {
      synchronized(this) {
        if(_parentSettingsDao == null) {
          _parentSettingsDao = new ParentSettingsDao_Impl(this);
        }
        return _parentSettingsDao;
      }
    }
  }
}
