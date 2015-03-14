package com.pushtorefresh.storio.db.unit_test.operation;

import android.database.Cursor;

import com.pushtorefresh.storio.db.StorIODb;
import com.pushtorefresh.storio.db.operation.MapFunc;
import com.pushtorefresh.storio.db.operation.get.PreparedGet;
import com.pushtorefresh.storio.db.query.Query;
import com.pushtorefresh.storio.db.query.RawQuery;
import com.pushtorefresh.storio.db.unit_test.design.User;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PreparedGetTest {

    private static class GetStub {
        final StorIODb storIODb;
        final MapFunc<Cursor, User> mapFunc;
        final Query query;
        final RawQuery rawQuery;
        final StorIODb.Internal internal;

        GetStub() {
            storIODb = mock(StorIODb.class);
            query = mock(Query.class);
            rawQuery = mock(RawQuery.class);
            internal = mockInternal();

            when(storIODb.get())
                    .thenReturn(new PreparedGet.Builder(storIODb));

            when(storIODb.internal())
                    .thenReturn(internal);

            //noinspection unchecked
            mapFunc = (MapFunc<Cursor, User>) mock(MapFunc.class);

            when(mapFunc.map(any(Cursor.class)))
                    .thenReturn(mock(User.class));
        }

        private StorIODb.Internal mockInternal() {
            final int mockObjectsSize = 3;
            final Cursor cursorStub = mock(Cursor.class);
            when(cursorStub.moveToNext()).thenAnswer(new Answer<Boolean>() {
                int invocationsCount = 0;

                @Override public Boolean answer(InvocationOnMock invocation) throws Throwable {
                    return invocationsCount++ < mockObjectsSize;
                }
            });

            StorIODb.Internal internal = mock(StorIODb.Internal.class);
            when(internal.query(query)).thenReturn(cursorStub);
            when(internal.rawQuery(rawQuery)).thenReturn(cursorStub);
            return internal;
        }

        private void verifyQueryBehavior() {
            verify(storIODb, times(1)).get();
            verify(internal, times(1)).query(any(Query.class));
        }

        private void verifyQueryBehaviorForList() {
            verify(storIODb, times(1)).get();
            verify(mapFunc, times(3)).map(any(Cursor.class));
            verify(internal, times(1)).query(any(Query.class));
        }

        private void verifyRawQueryBehavior() {
            verify(storIODb, times(1)).get();
            verify(internal, times(1)).rawQuery(any(RawQuery.class));
        }

        private void verifyRawQueryBehaviorForList() {
            verify(storIODb, times(1)).get();
            verify(mapFunc, times(3)).map(any(Cursor.class));
            verify(internal, times(1)).rawQuery(any(RawQuery.class));
        }

    }

    @Test public void getCursorBlocking() {
        final GetStub getStub = new GetStub();

        getStub.storIODb
                .get()
                .cursor()
                .withQuery(getStub.query)
                .prepare()
                .executeAsBlocking();

        getStub.verifyQueryBehavior();
    }

    @Test public void getListOfObjectsBlocking() {
        final GetStub getStub = new GetStub();

        getStub.storIODb
                .get()
                .listOfObjects(User.class)
                .withMapFunc(getStub.mapFunc)
                .withQuery(getStub.query)
                .prepare()
                .executeAsBlocking();

        getStub.verifyQueryBehaviorForList();
    }

    @Test public void getCursorObservable() {
        final GetStub getStub = new GetStub();

        getStub.storIODb
                .get()
                .cursor()
                .withQuery(getStub.query)
                .prepare()
                .createObservable()
                .toBlocking()
                .last();

        getStub.verifyQueryBehavior();
    }


    @Test public void getListOfObjectsObservable() {
        final GetStub getStub = new GetStub();

        getStub.storIODb
                .get()
                .listOfObjects(User.class)
                .withMapFunc(getStub.mapFunc)
                .withQuery(getStub.query)
                .prepare()
                .createObservable()
                .toBlocking()
                .last();

        getStub.verifyQueryBehaviorForList();
    }

    @Test public void getCursorWithRawQueryBlocking() {
        final GetStub getStub = new GetStub();

        getStub.storIODb
                .get()
                .cursor()
                .withQuery(getStub.rawQuery)
                .prepare()
                .executeAsBlocking();

        getStub.verifyRawQueryBehavior();
    }

    @Test public void getCursorWithRawQueryObservable() {
        final GetStub getStub = new GetStub();

        getStub.storIODb
                .get()
                .cursor()
                .withQuery(getStub.rawQuery)
                .prepare()
                .createObservable()
                .toBlocking()
                .last();

        getStub.verifyRawQueryBehavior();
    }

    @Test public void getListOfObjectsWithRawQueryBlocking() {
        final GetStub getStub = new GetStub();

        getStub.storIODb
                .get()
                .listOfObjects(User.class)
                .withMapFunc(getStub.mapFunc)
                .withQuery(getStub.rawQuery)
                .prepare()
                .executeAsBlocking();

        getStub.verifyRawQueryBehaviorForList();
    }

    @Test public void getListOfObjectsWithRawQueryObservable() {
        final GetStub getStub = new GetStub();

        getStub.storIODb
                .get()
                .listOfObjects(User.class)
                .withMapFunc(getStub.mapFunc)
                .withQuery(getStub.rawQuery)
                .prepare()
                .createObservable()
                .toBlocking()
                .last();

        getStub.verifyRawQueryBehaviorForList();
    }
}