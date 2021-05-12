package com.modolo.healthyplus.fitnesstracker.workoutdb;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
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
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@SuppressWarnings({"unchecked", "deprecation"})
public final class WorkoutDAO_Impl implements WorkoutDAO {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Workout> __insertionAdapterOfWorkout;

  private final EntityDeletionOrUpdateAdapter<Workout> __deletionAdapterOfWorkout;

  private final SharedSQLiteStatement __preparedStmtOfUpdateWorkout;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public WorkoutDAO_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfWorkout = new EntityInsertionAdapter<Workout>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `workouts` (`id`,`name`,`exerciseList`,`date`,`ispreset`,`isdone`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Workout value) {
        stmt.bindLong(1, value.getId());
        if (value.getName() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getName());
        }
        if (value.getExerciseList() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getExerciseList());
        }
        if (value.getDate() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getDate());
        }
        final int _tmp;
        _tmp = value.getIspreset() ? 1 : 0;
        stmt.bindLong(5, _tmp);
        final int _tmp_1;
        _tmp_1 = value.getIsdone() ? 1 : 0;
        stmt.bindLong(6, _tmp_1);
      }
    };
    this.__deletionAdapterOfWorkout = new EntityDeletionOrUpdateAdapter<Workout>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `workouts` WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Workout value) {
        stmt.bindLong(1, value.getId());
      }
    };
    this.__preparedStmtOfUpdateWorkout = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "UPDATE workouts SET name=?, exerciseList=?, date=?, ispreset=?, isdone=? WHERE id=?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM workouts";
        return _query;
      }
    };
  }

  @Override
  public Object insertWorkout(final Workout workout, final Continuation<? super Unit> p1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfWorkout.insert(workout);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, p1);
  }

  @Override
  public Object deleteWorkout(final Workout workout, final Continuation<? super Unit> p1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfWorkout.handle(workout);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, p1);
  }

  @Override
  public Object updateWorkout(final int id, final String name, final String exerciseList,
      final String data, final boolean ispreset, final boolean isdone,
      final Continuation<? super Unit> p6) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateWorkout.acquire();
        int _argIndex = 1;
        if (name == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, name);
        }
        _argIndex = 2;
        if (exerciseList == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, exerciseList);
        }
        _argIndex = 3;
        if (data == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, data);
        }
        _argIndex = 4;
        final int _tmp;
        _tmp = ispreset ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 5;
        final int _tmp_1;
        _tmp_1 = isdone ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp_1);
        _argIndex = 6;
        _stmt.bindLong(_argIndex, id);
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
          __preparedStmtOfUpdateWorkout.release(_stmt);
        }
      }
    }, p6);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> p0) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, p0);
  }

  @Override
  public Object getAll(final Continuation<? super List<Workout>> p0) {
    final String _sql = "SELECT * FROM workouts ORDER BY date";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Workout>>() {
      @Override
      public List<Workout> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfExerciseList = CursorUtil.getColumnIndexOrThrow(_cursor, "exerciseList");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfIspreset = CursorUtil.getColumnIndexOrThrow(_cursor, "ispreset");
          final int _cursorIndexOfIsdone = CursorUtil.getColumnIndexOrThrow(_cursor, "isdone");
          final List<Workout> _result = new ArrayList<Workout>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final Workout _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpExerciseList;
            if (_cursor.isNull(_cursorIndexOfExerciseList)) {
              _tmpExerciseList = null;
            } else {
              _tmpExerciseList = _cursor.getString(_cursorIndexOfExerciseList);
            }
            final String _tmpDate;
            if (_cursor.isNull(_cursorIndexOfDate)) {
              _tmpDate = null;
            } else {
              _tmpDate = _cursor.getString(_cursorIndexOfDate);
            }
            final boolean _tmpIspreset;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIspreset);
            _tmpIspreset = _tmp != 0;
            final boolean _tmpIsdone;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsdone);
            _tmpIsdone = _tmp_1 != 0;
            _item = new Workout(_tmpId,_tmpName,_tmpExerciseList,_tmpDate,_tmpIspreset,_tmpIsdone);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, p0);
  }

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
