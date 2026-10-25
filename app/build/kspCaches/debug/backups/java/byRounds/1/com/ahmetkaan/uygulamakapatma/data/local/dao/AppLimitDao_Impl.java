package com.ahmetkaan.uygulamakapatma.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.ahmetkaan.uygulamakapatma.data.local.entity.AppLimit;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppLimitDao_Impl implements AppLimitDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AppLimit> __insertionAdapterOfAppLimit;

  private final EntityDeletionOrUpdateAdapter<AppLimit> __deletionAdapterOfAppLimit;

  private final EntityDeletionOrUpdateAdapter<AppLimit> __updateAdapterOfAppLimit;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByPackageName;

  private final SharedSQLiteStatement __preparedStmtOfAddExtensionMinutes;

  private final SharedSQLiteStatement __preparedStmtOfSetPermanentlyBlocked;

  public AppLimitDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAppLimit = new EntityInsertionAdapter<AppLimit>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `app_limits` (`id`,`packageName`,`dailyLimitMinutes`,`isPermanentlyBlocked`,`extensionMinutes`,`extensionDateMillis`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AppLimit entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getPackageName());
        statement.bindLong(3, entity.getDailyLimitMinutes());
        final int _tmp = entity.isPermanentlyBlocked() ? 1 : 0;
        statement.bindLong(4, _tmp);
        statement.bindLong(5, entity.getExtensionMinutes());
        statement.bindLong(6, entity.getExtensionDateMillis());
      }
    };
    this.__deletionAdapterOfAppLimit = new EntityDeletionOrUpdateAdapter<AppLimit>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `app_limits` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AppLimit entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfAppLimit = new EntityDeletionOrUpdateAdapter<AppLimit>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `app_limits` SET `id` = ?,`packageName` = ?,`dailyLimitMinutes` = ?,`isPermanentlyBlocked` = ?,`extensionMinutes` = ?,`extensionDateMillis` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AppLimit entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getPackageName());
        statement.bindLong(3, entity.getDailyLimitMinutes());
        final int _tmp = entity.isPermanentlyBlocked() ? 1 : 0;
        statement.bindLong(4, _tmp);
        statement.bindLong(5, entity.getExtensionMinutes());
        statement.bindLong(6, entity.getExtensionDateMillis());
        statement.bindLong(7, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteByPackageName = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM app_limits WHERE packageName = ?";
        return _query;
      }
    };
    this.__preparedStmtOfAddExtensionMinutes = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE app_limits SET extensionMinutes = extensionMinutes + ?, extensionDateMillis = ? WHERE packageName = ?";
        return _query;
      }
    };
    this.__preparedStmtOfSetPermanentlyBlocked = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE app_limits SET isPermanentlyBlocked = ? WHERE packageName = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertAppLimit(final AppLimit appLimit,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAppLimit.insert(appLimit);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAppLimit(final AppLimit appLimit,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfAppLimit.handle(appLimit);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateAppLimit(final AppLimit appLimit,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfAppLimit.handle(appLimit);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteByPackageName(final String packageName,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByPackageName.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, packageName);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteByPackageName.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object addExtensionMinutes(final String packageName, final int extraMinutes,
      final long dateMillis, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfAddExtensionMinutes.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, extraMinutes);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, dateMillis);
        _argIndex = 3;
        _stmt.bindString(_argIndex, packageName);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfAddExtensionMinutes.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object setPermanentlyBlocked(final String packageName, final boolean isBlocked,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetPermanentlyBlocked.acquire();
        int _argIndex = 1;
        final int _tmp = isBlocked ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindString(_argIndex, packageName);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfSetPermanentlyBlocked.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<AppLimit>> getAllLimits() {
    final String _sql = "SELECT * FROM app_limits";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"app_limits"}, new Callable<List<AppLimit>>() {
      @Override
      @NonNull
      public List<AppLimit> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfDailyLimitMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyLimitMinutes");
          final int _cursorIndexOfIsPermanentlyBlocked = CursorUtil.getColumnIndexOrThrow(_cursor, "isPermanentlyBlocked");
          final int _cursorIndexOfExtensionMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "extensionMinutes");
          final int _cursorIndexOfExtensionDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "extensionDateMillis");
          final List<AppLimit> _result = new ArrayList<AppLimit>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AppLimit _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpPackageName;
            _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            final int _tmpDailyLimitMinutes;
            _tmpDailyLimitMinutes = _cursor.getInt(_cursorIndexOfDailyLimitMinutes);
            final boolean _tmpIsPermanentlyBlocked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPermanentlyBlocked);
            _tmpIsPermanentlyBlocked = _tmp != 0;
            final int _tmpExtensionMinutes;
            _tmpExtensionMinutes = _cursor.getInt(_cursorIndexOfExtensionMinutes);
            final long _tmpExtensionDateMillis;
            _tmpExtensionDateMillis = _cursor.getLong(_cursorIndexOfExtensionDateMillis);
            _item = new AppLimit(_tmpId,_tmpPackageName,_tmpDailyLimitMinutes,_tmpIsPermanentlyBlocked,_tmpExtensionMinutes,_tmpExtensionDateMillis);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAllLimitsSync(final Continuation<? super List<AppLimit>> $completion) {
    final String _sql = "SELECT * FROM app_limits";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AppLimit>>() {
      @Override
      @NonNull
      public List<AppLimit> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfDailyLimitMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyLimitMinutes");
          final int _cursorIndexOfIsPermanentlyBlocked = CursorUtil.getColumnIndexOrThrow(_cursor, "isPermanentlyBlocked");
          final int _cursorIndexOfExtensionMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "extensionMinutes");
          final int _cursorIndexOfExtensionDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "extensionDateMillis");
          final List<AppLimit> _result = new ArrayList<AppLimit>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AppLimit _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpPackageName;
            _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            final int _tmpDailyLimitMinutes;
            _tmpDailyLimitMinutes = _cursor.getInt(_cursorIndexOfDailyLimitMinutes);
            final boolean _tmpIsPermanentlyBlocked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPermanentlyBlocked);
            _tmpIsPermanentlyBlocked = _tmp != 0;
            final int _tmpExtensionMinutes;
            _tmpExtensionMinutes = _cursor.getInt(_cursorIndexOfExtensionMinutes);
            final long _tmpExtensionDateMillis;
            _tmpExtensionDateMillis = _cursor.getLong(_cursorIndexOfExtensionDateMillis);
            _item = new AppLimit(_tmpId,_tmpPackageName,_tmpDailyLimitMinutes,_tmpIsPermanentlyBlocked,_tmpExtensionMinutes,_tmpExtensionDateMillis);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getLimitByPackageName(final String packageName,
      final Continuation<? super AppLimit> $completion) {
    final String _sql = "SELECT * FROM app_limits WHERE packageName = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, packageName);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<AppLimit>() {
      @Override
      @Nullable
      public AppLimit call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfDailyLimitMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyLimitMinutes");
          final int _cursorIndexOfIsPermanentlyBlocked = CursorUtil.getColumnIndexOrThrow(_cursor, "isPermanentlyBlocked");
          final int _cursorIndexOfExtensionMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "extensionMinutes");
          final int _cursorIndexOfExtensionDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "extensionDateMillis");
          final AppLimit _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpPackageName;
            _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            final int _tmpDailyLimitMinutes;
            _tmpDailyLimitMinutes = _cursor.getInt(_cursorIndexOfDailyLimitMinutes);
            final boolean _tmpIsPermanentlyBlocked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPermanentlyBlocked);
            _tmpIsPermanentlyBlocked = _tmp != 0;
            final int _tmpExtensionMinutes;
            _tmpExtensionMinutes = _cursor.getInt(_cursorIndexOfExtensionMinutes);
            final long _tmpExtensionDateMillis;
            _tmpExtensionDateMillis = _cursor.getLong(_cursorIndexOfExtensionDateMillis);
            _result = new AppLimit(_tmpId,_tmpPackageName,_tmpDailyLimitMinutes,_tmpIsPermanentlyBlocked,_tmpExtensionMinutes,_tmpExtensionDateMillis);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
