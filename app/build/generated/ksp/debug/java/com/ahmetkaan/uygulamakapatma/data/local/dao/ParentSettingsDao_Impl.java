package com.ahmetkaan.uygulamakapatma.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.ahmetkaan.uygulamakapatma.data.local.entity.ParentSettings;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ParentSettingsDao_Impl implements ParentSettingsDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ParentSettings> __insertionAdapterOfParentSettings;

  private final SharedSQLiteStatement __preparedStmtOfUpdateLockout;

  private final SharedSQLiteStatement __preparedStmtOfResetLockout;

  public ParentSettingsDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfParentSettings = new EntityInsertionAdapter<ParentSettings>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `parent_settings` (`id`,`pinHash`,`salt`,`failedAttempts`,`lockoutUntilMillis`,`securityQuestion`,`securityAnswerHash`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ParentSettings entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getPinHash());
        statement.bindString(3, entity.getSalt());
        statement.bindLong(4, entity.getFailedAttempts());
        statement.bindLong(5, entity.getLockoutUntilMillis());
        statement.bindString(6, entity.getSecurityQuestion());
        statement.bindString(7, entity.getSecurityAnswerHash());
      }
    };
    this.__preparedStmtOfUpdateLockout = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE parent_settings SET failedAttempts = ?, lockoutUntilMillis = ? WHERE id = 1";
        return _query;
      }
    };
    this.__preparedStmtOfResetLockout = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE parent_settings SET failedAttempts = 0, lockoutUntilMillis = 0 WHERE id = 1";
        return _query;
      }
    };
  }

  @Override
  public Object saveSettings(final ParentSettings settings,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfParentSettings.insert(settings);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateLockout(final int failedAttempts, final long lockoutUntil,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateLockout.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, failedAttempts);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, lockoutUntil);
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
          __preparedStmtOfUpdateLockout.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object resetLockout(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfResetLockout.acquire();
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
          __preparedStmtOfResetLockout.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getSettings(final Continuation<? super ParentSettings> $completion) {
    final String _sql = "SELECT * FROM parent_settings WHERE id = 1 LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ParentSettings>() {
      @Override
      @Nullable
      public ParentSettings call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPinHash = CursorUtil.getColumnIndexOrThrow(_cursor, "pinHash");
          final int _cursorIndexOfSalt = CursorUtil.getColumnIndexOrThrow(_cursor, "salt");
          final int _cursorIndexOfFailedAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "failedAttempts");
          final int _cursorIndexOfLockoutUntilMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "lockoutUntilMillis");
          final int _cursorIndexOfSecurityQuestion = CursorUtil.getColumnIndexOrThrow(_cursor, "securityQuestion");
          final int _cursorIndexOfSecurityAnswerHash = CursorUtil.getColumnIndexOrThrow(_cursor, "securityAnswerHash");
          final ParentSettings _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpPinHash;
            _tmpPinHash = _cursor.getString(_cursorIndexOfPinHash);
            final String _tmpSalt;
            _tmpSalt = _cursor.getString(_cursorIndexOfSalt);
            final int _tmpFailedAttempts;
            _tmpFailedAttempts = _cursor.getInt(_cursorIndexOfFailedAttempts);
            final long _tmpLockoutUntilMillis;
            _tmpLockoutUntilMillis = _cursor.getLong(_cursorIndexOfLockoutUntilMillis);
            final String _tmpSecurityQuestion;
            _tmpSecurityQuestion = _cursor.getString(_cursorIndexOfSecurityQuestion);
            final String _tmpSecurityAnswerHash;
            _tmpSecurityAnswerHash = _cursor.getString(_cursorIndexOfSecurityAnswerHash);
            _result = new ParentSettings(_tmpId,_tmpPinHash,_tmpSalt,_tmpFailedAttempts,_tmpLockoutUntilMillis,_tmpSecurityQuestion,_tmpSecurityAnswerHash);
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

  @Override
  public Object isPinSet(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM parent_settings WHERE id = 1 AND pinHash != ''";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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
