package com.nivgelbermann.fooddiarydemo.database;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.nivgelbermann.fooddiarydemo.data.database.AppDatabase;
import com.nivgelbermann.fooddiarydemo.data.database.FoodDao;
import com.nivgelbermann.fooddiarydemo.data.database.FoodEntry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Instrumentation test class for {@link FoodDao}.
 */

@RunWith(AndroidJUnit4.class)
public class FoodDaoTest {
    private static final String TAG = "FoodDaoTest";

    private AppDatabase mDatabase;
    private FoodDao mFoodDao;

    @Before
    public void setUp() throws IOException {
        // Create database instance
        Context context = InstrumentationRegistry.getContext();
        mDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        mFoodDao = mDatabase.foodDao();
    }

    @After
    public void tearDown() throws IOException {
        // Close database instance
        mDatabase.close();
    }

    @Test
    public void insertSingleEntry() throws Exception {
        Calendar time = Calendar.getInstance();
        FoodEntry entry = new FoodEntry("Schnitzel", time, 4);
        mFoodDao.insert(entry);

        /* Using LiveDataTestUtil.getValue to get value (list of entries)
         * from LiveData returned by dao, SYNCHRONOUSLY.
         * (without observing the LiveData, which didn't work for some reason.)
         * More info:
         * https://stackoverflow.com/questions/48602979/android-livedata-in-androidtest-returns-null
         * https://stackoverflow.com/questions/44270688/unit-testing-room-and-livedata/44271247 */
        List<FoodEntry> data = LiveDataTestUtil.getValue(mFoodDao.getAll());

        assertNotNull(data);
        assertFalse(data.isEmpty());
        assertTrue(data.get(0).getName().equals("Schnitzel"));
        assertTrue(data.get(0).getCategory() == 4);
        assertTrue(data.get(0).getTime().equals(time));
    }

    @Test
    public void insertMultipleEntries() throws Exception {
        Calendar time = Calendar.getInstance();
        FoodEntry entry1 = new FoodEntry("Hot fries", time, 3);
        FoodEntry entry2 = new FoodEntry("Tuna fish", time, 4);
        FoodEntry entry3 = new FoodEntry("Devil cake", time, 8);
//        mFoodDao.insertAll(entry1, entry2, entry3); TODO Write another test for this
        mFoodDao.insert(entry1);
        mFoodDao.insert(entry2);
        mFoodDao.insert(entry3);

        List<FoodEntry> data = LiveDataTestUtil.getValue(mFoodDao.getAll());
        assertNotNull(data);
        assertFalse(data.isEmpty());
        // The following lines are not going to work, because entry1/2/3 do not have a valid id
        assertTrue(data.contains(entry1));
        assertTrue(data.contains(entry2));
        assertTrue(data.contains(entry3));
    }

}


/* TODO Next tests to write:
   * FoodRepositoryTest
   * ?
 */