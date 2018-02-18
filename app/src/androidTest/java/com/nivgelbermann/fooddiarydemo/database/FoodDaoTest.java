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

import static org.junit.Assert.assertEquals;
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
        FoodEntry entry = new FoodEntry("Schnitzel",
                time.getTimeInMillis(),
                time.get(Calendar.DAY_OF_MONTH),
                time.get(Calendar.MONTH),
                time.get(Calendar.YEAR),
                4);
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
        assertTrue(data.get(0).getName().equals(entry.getName()));
        assertTrue(data.get(0).getCategory() == entry.getCategory());
        assertTrue(data.get(0).getTime() == entry.getTime());
    }

    @Test
    public void insertMultipleEntries() throws Exception {
        Calendar time = Calendar.getInstance();
        FoodEntry entry1 = new FoodEntry("Hot fries",
                time.getTimeInMillis(),
                time.get(Calendar.DAY_OF_MONTH),
                time.get(Calendar.MONTH),
                time.get(Calendar.YEAR),
                3);
        FoodEntry entry2 = new FoodEntry("Tuna fish",
                time.getTimeInMillis(),
                time.get(Calendar.DAY_OF_MONTH),
                time.get(Calendar.MONTH),
                time.get(Calendar.YEAR),
                4);
        FoodEntry entry3 = new FoodEntry("Devil cake",
                time.getTimeInMillis(),
                time.get(Calendar.DAY_OF_MONTH),
                time.get(Calendar.MONTH),
                time.get(Calendar.YEAR),
                8);
        mFoodDao.insert(entry1);
        mFoodDao.insert(entry2);
        mFoodDao.insert(entry3);

        List<FoodEntry> data = LiveDataTestUtil.getValue(mFoodDao.getAll());
        assertNotNull(data);
        assertFalse(data.isEmpty());
        assertTrue(data.size() == 3);
        // The following lines are relevant if and only if FoodEntry.equals ignores entry's id.
        assertTrue(data.contains(entry1));
        assertTrue(data.contains(entry2));
        assertTrue(data.contains(entry3));
    }

    @Test
    public void insertMultipleEntriesAtOnce() throws Exception {
        Calendar time = Calendar.getInstance();
        FoodEntry entry1 = new FoodEntry("Corn dog",
                time.getTimeInMillis(),
                time.get(Calendar.DAY_OF_MONTH),
                time.get(Calendar.MONTH),
                time.get(Calendar.YEAR),
                4);
        FoodEntry entry2 = new FoodEntry("Caesar salad",
                time.getTimeInMillis(),
                time.get(Calendar.DAY_OF_MONTH),
                time.get(Calendar.MONTH),
                time.get(Calendar.YEAR),
                2);
        FoodEntry entry3 = new FoodEntry("A gallon of beer",
                time.getTimeInMillis(),
                time.get(Calendar.DAY_OF_MONTH),
                time.get(Calendar.MONTH),
                time.get(Calendar.YEAR),
                7);
        mFoodDao.insertAll(entry1, entry2, entry3);

        List<FoodEntry> data = LiveDataTestUtil.getValue(mFoodDao.getAll());
        assertNotNull(data);
        assertFalse(data.isEmpty());
        assertTrue(data.size() == 3);
        // The following lines are relevant if and only if FoodEntry.equals ignores entry's id.
        assertTrue(data.contains(entry1));
        assertTrue(data.contains(entry2));
        assertTrue(data.contains(entry3));
    }

    @Test
    public void retrieveEntriesByMonth() throws Exception {
        // =========== Set up db content =========== //
        Calendar time = Calendar.getInstance();
        time.set(Calendar.MONTH, 0);
        time.set(Calendar.YEAR, 2018);
        FoodEntry entry1 = new FoodEntry("Fruit salad",
                time.getTimeInMillis(),
                time.get(Calendar.DAY_OF_MONTH),
                time.get(Calendar.MONTH),
                time.get(Calendar.YEAR),
                6);

        time.set(Calendar.MONTH, 1);
        time.set(Calendar.YEAR, 2018);
        FoodEntry entry2 = new FoodEntry("Chicken soup",
                time.getTimeInMillis(),
                time.get(Calendar.DAY_OF_MONTH),
                time.get(Calendar.MONTH),
                time.get(Calendar.YEAR),
                4);

        time.set(Calendar.MONTH, 11);
        time.set(Calendar.YEAR, 2017);
        FoodEntry entry3 = new FoodEntry("Mashed potatoes",
                time.getTimeInMillis(),
                time.get(Calendar.DAY_OF_MONTH),
                time.get(Calendar.MONTH),
                time.get(Calendar.YEAR),
                2);

        mFoodDao.insertAll(entry1, entry2, entry3);

        // =========== Test db content =========== //
        List<FoodEntry> data = LiveDataTestUtil.getValue(mFoodDao.getByMonth(0, 2018));
        assertNotNull(data);
        assertFalse(data.isEmpty());
        assertTrue(data.size() == 1);
        assertTrue(data.get(0).getName().equals(entry1.getName()));

        data = LiveDataTestUtil.getValue(mFoodDao.getByMonth(1, 2018));
        assertNotNull(data);
        assertFalse(data.isEmpty());
        assertTrue(data.size() == 1);
        assertTrue(data.get(0).getName().equals(entry2.getName()));

        data = LiveDataTestUtil.getValue(mFoodDao.getByMonth(11, 2017));
        assertNotNull(data);
        assertFalse(data.isEmpty());
        assertTrue(data.size() == 1);
        assertTrue(data.get(0).getName().equals(entry3.getName()));
    }

    @Test
    public void retrieveEntriesByDay() throws Exception {
        // =========== Set up db content =========== //
        Calendar time = Calendar.getInstance();
        time.set(Calendar.DAY_OF_MONTH, 18);
        time.set(Calendar.MONTH, 0);
        time.set(Calendar.YEAR, 2018);
        FoodEntry entry1 = new FoodEntry("Gorgonzola cheese",
                time.getTimeInMillis(),
                time.get(Calendar.DAY_OF_MONTH),
                time.get(Calendar.MONTH),
                time.get(Calendar.YEAR),
                6);

        time.set(Calendar.DAY_OF_MONTH, 5);
        time.set(Calendar.MONTH, 11);
        time.set(Calendar.YEAR, 2017);
        FoodEntry entry2 = new FoodEntry("Orange soda",
                time.getTimeInMillis(),
                time.get(Calendar.DAY_OF_MONTH),
                time.get(Calendar.MONTH),
                time.get(Calendar.YEAR),
                4);

        mFoodDao.insertAll(entry1, entry2);

        // =========== Test db content =========== //
        List<FoodEntry> data = LiveDataTestUtil.getValue(mFoodDao.getByDay(18, 0, 2018));
        assertNotNull(data);
        assertFalse(data.isEmpty());
        assertTrue(data.size() == 1);
        assertTrue(data.get(0).getName().equals(entry1.getName()));

        data = LiveDataTestUtil.getValue(mFoodDao.getByDay(5, 11, 2017));
        assertNotNull(data);
        assertFalse(data.isEmpty());
        assertTrue(data.size() == 1);
        assertTrue(data.get(0).getName().equals(entry2.getName()));
    }

    @Test
    public void retrieveEntryById() throws Exception {
        Calendar time = Calendar.getInstance();
        FoodEntry entry = new FoodEntry("Hawaiian pizza",
                time.getTimeInMillis(),
                time.get(Calendar.DAY_OF_MONTH),
                time.get(Calendar.MONTH),
                time.get(Calendar.YEAR),
                5);
        mFoodDao.insert(entry);

        List<FoodEntry> data = LiveDataTestUtil.getValue(mFoodDao.getAll());
        assertNotNull(data);
        assertFalse(data.isEmpty());
        assertTrue(data.size() == 1);

        FoodEntry retreievedEntry = data.get(0);
        assertNotNull(retreievedEntry);
        assertTrue(retreievedEntry.getName().equals(entry.getName()));

        FoodEntry retrievedEntryById = LiveDataTestUtil.getValue(
                mFoodDao.getById(retreievedEntry.getId()));
        assertNotNull(retrievedEntryById);
        assertEquals(retreievedEntry, retrievedEntryById);
    }

    @Test
    public void deleteEntry() throws Exception {
        Calendar time = Calendar.getInstance();
        FoodEntry entry = new FoodEntry("Paella",
                time.getTimeInMillis(),
                time.get(Calendar.DAY_OF_MONTH),
                time.get(Calendar.MONTH),
                time.get(Calendar.YEAR),
                3);
        mFoodDao.insert(entry);

        FoodEntry retrievedEntry = LiveDataTestUtil.getValue(mFoodDao.getAll()).get(0);
        assertTrue(mFoodDao.delete(retrievedEntry) == 1);
    }
}