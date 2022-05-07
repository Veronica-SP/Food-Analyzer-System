package bg.sofia.uni.fmi.mjt.food.analyzer.cache;

import bg.sofia.uni.fmi.mjt.food.analyzer.Food;
import bg.sofia.uni.fmi.mjt.food.analyzer.exceptions.FoodDataAPIException;
import bg.sofia.uni.fmi.mjt.food.analyzer.finder.FoodDataFinder;
import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FoodCacheTest {
    private static final Gson GSON = new Gson();
    private static final String TEST_CACHE_FILE = "test.txt";

    @Mock
    FoodDataFinder foodDataFinderMock;

    private FoodCache foodCache;

    @BeforeEach
    void setUp() {
        foodCache = new FoodCache(foodDataFinderMock, Path.of(TEST_CACHE_FILE));
    }

    @Test
    void testGetByFoodNameReturnsEmptyCollectionWhenNotInCacheAndAPIError() throws FoodDataAPIException {
        when(foodDataFinderMock.findByFoodName("some food")).thenThrow(FoodDataAPIException.class);

        assertEquals(0, foodCache.getByFoodName("some food").size(),
                "When the food isn't found in the cache and there is an error with connecting to the API the cache should return empty collection!");
    }

    @Test
    void testGetByFoodNameReturnsEmptyCollectionWhenNotInCacheAndNotFoundByAPI() throws FoodDataAPIException {
        when(foodDataFinderMock.findByFoodName("some food")).thenReturn(List.of());

        assertEquals(0, foodCache.getByFoodName("some food").size(),
                "When the food isn't found in the cache and in the API the cache should return an empty collection!");
    }

    @Test
    void testGetByFoodNameSavesToCacheWhenNotInCacheAndNotFoundByAPI() throws FoodDataAPIException {
        when(foodDataFinderMock.findByFoodName("some food")).thenReturn(List.of());

        foodCache.getByFoodName("some food");

        assertTrue(foodCache.getByFoodNameMap().get("some food").isEmpty(),
                "When the food isn't found in the cache and in the API the cache should save the empty collection returned by the API!");
    }

    @Test
    void testGetByFoodNameReturnsResultWhenNotInCacheAndFoundByAPI() throws FoodDataAPIException {
        Food match = new Food("some food", 1234, null, null, null);

        Food[] results = new Food[5];
        Arrays.fill(results, match);
        List<Food> expected = List.of(results);

        when(foodDataFinderMock.findByFoodName("some food")).thenReturn(expected);
        Collection<Food> actual = foodCache.getByFoodName("some food");

        assertTrue(expected.containsAll(actual) && actual.containsAll(expected),
                "When the food isn't found in the cache but is found from the API the cache should return a valid collection!");
    }

    @Test
    void testGetByFoodNameSavesResultWhenNotInCacheAndFoundByAPI() throws FoodDataAPIException {
        Food match = new Food("some food", 1234, null, null, null);

        Food[] results = new Food[5];
        Arrays.fill(results, match);
        List<Food> expected = List.of(results);

        when(foodDataFinderMock.findByFoodName("some food")).thenReturn(expected);

        foodCache.getByFoodName("some food");
        Collection<Food> actual = foodCache.getByFoodNameMap().get("some food");

        assertTrue(expected.containsAll(actual) && actual.containsAll(expected),
                "When the food isn't found in the cache but is found from the API the cache should save the collection returned by the API!");
    }

    @Test
    void testGetByFoodNameSavesResultForBarcodeWhenNotInCacheAndFoundByAPI() throws FoodDataAPIException {
        Food match1 = new Food("some food 1", 1, "1234", null, null);
        Food match2 = new Food("some food 2", 2, "1235", null, null);

        List<Food> expected = List.of(match1, match2);

        when(foodDataFinderMock.findByFoodName("some food")).thenReturn(expected);

        foodCache.getByFoodName("some food");

        for (Food food : expected) {
            assertEquals(food, foodCache.getByGtinUpcMap().get(food.getGtinUpc()),
                    "Cache should save food results to barcode map!");
        }
    }

    @Test
    void testGetByFoodNameIgnoresNullBarcodeWhenNotInCacheAndFoundByAPI() throws FoodDataAPIException {
        Food match1 = new Food("some food 1", 1, "1234", null, null);
        Food match2 = new Food("some food 2", 2, null, null, null);

        List<Food> expected = List.of(match1, match2);

        when(foodDataFinderMock.findByFoodName("some food")).thenReturn(expected);

        foodCache.getByFoodName("some food");

        assertFalse(foodCache.getByGtinUpcMap().containsKey(null),
                "Foods with no barcode should not be saved to barcode map!");
    }

    @Test
    void testGetByFoodNameReturnsFromCacheWhenIsSaved() throws FoodDataAPIException {
        Food match = new Food("some food", 1234, null, null, null);

        Food[] results = new Food[5];
        Arrays.fill(results, match);
        List<Food> expected = List.of(results);

        when(foodDataFinderMock.findByFoodName("some food")).thenReturn(expected);

        foodCache.getByFoodName("some food");
        Collection<Food> actual = foodCache.getByFoodName("some food");

        assertTrue(actual.containsAll(expected) && expected.containsAll(actual),
                "Invalid result when getting directly from cache!");

        verify(foodDataFinderMock, times(1)).findByFoodName("some food");
    }


    @Test
    void testGetByIdReturnsNullWhenNotInCacheAndAPIError() throws FoodDataAPIException {
        when(foodDataFinderMock.findById(1234)).thenThrow(FoodDataAPIException.class);

        assertNull(foodCache.getById(1234),
                "When the food isn't found in the cache and there is an error with connecting to the API the cache should return null!");
    }

    @Test
    void testGetByIdReturnsNullWhenNotInCacheAndNotFoundByAPI() throws FoodDataAPIException {
        when(foodDataFinderMock.findById(1234)).thenReturn(null);

        assertNull(foodCache.getById(1234),
                "When the food isn't found in the cache and in the API the cache should return null!");
    }

    @Test
    void testGetByIdSavesToCacheWhenNotInCacheAndNotFoundByAPI() throws FoodDataAPIException {
        when(foodDataFinderMock.findById(1234)).thenReturn(null);

        foodCache.getById(1234);

        assertNull(foodCache.getByIdMap().get(1234),
                "When the food isn't found in the cache and in the API the cache should save the null Food returned by the API!");
    }

    @Test
    void testGetByIdReturnsResultWhenNotInCacheAndFoundByAPI() throws FoodDataAPIException {
        Food match = new Food("some food", 1234, null, null, null);

        when(foodDataFinderMock.findById(1234)).thenReturn(match);
        Food actual = foodCache.getById(1234);

        assertEquals(match, actual,
                "When the food isn't found in the cache but is found from the API the cache should return that result!");
    }

    @Test
    void testGetByIdSavesResultWhenNotInCacheAndFoundByAPI() throws FoodDataAPIException {
        Food match = new Food("some food", 1234, null, null, null);

        when(foodDataFinderMock.findById(1234)).thenReturn(match);

        foodCache.getById(1234);
        Food actual = foodCache.getByIdMap().get(1234);

        assertEquals(match, actual,
                "When the food isn't found in the cache but is found from the API the cache should save the result returned by the API!");
    }

    @Test
    void testGetByIdSavesResultForBarcodeWhenNotInCacheAndFoundByAPI() throws FoodDataAPIException {
        Food match = new Food("some food 1", 1234, "1234", null, null);

        when(foodDataFinderMock.findById(1234)).thenReturn(match);

        foodCache.getById(1234);

        assertEquals(match, foodCache.getByGtinUpcMap().get(match.getGtinUpc()),
                "Cache should save food with valid barcode to barcode map!");
    }

    @Test
    void testGetByIdSavesIgnoresNullBarcodeWhenNotInCacheAndFoundByAPI() throws FoodDataAPIException {
        Food match = new Food("some food 1", 1234, null, null, null);

        when(foodDataFinderMock.findById(1234)).thenReturn(match);

        foodCache.getById(1234);

        assertFalse(foodCache.getByGtinUpcMap().containsKey(null),
                "Cache shouldn't save food with null barcode to barcode map!");
    }


    @Test
    void testGetByIdReturnsFromCacheWhenIsSaved() throws FoodDataAPIException {
        Food match = new Food("some food", 1234, null, null, null);

        when(foodDataFinderMock.findById(1234)).thenReturn(match);

        foodCache.getById(1234);
        Food actual = foodCache.getById(1234);

        assertEquals(match, actual,
                "Invalid result when getting directly from cache!");

        verify(foodDataFinderMock, times(1)).findById(1234);
    }

    @Test
    void testGetByGtinUpcReturnsNullWhenNotFound() {
        assertNull(foodCache.getByGtinUpc("1234"),
                "Cache should return null when there is no food with the specified GTIN/UPC in memory");
    }

    @Test
    void testGetByGtinUpcReturnsResultWhenPreviouslyFoundByName() throws FoodDataAPIException {
        Food match = new Food("some food", 1234, "1234", null, null);

        when(foodDataFinderMock.findByFoodName("some food")).thenReturn(List.of(match));

        foodCache.getByFoodName("some food");

        Food actual = foodCache.getByGtinUpc("1234");

        assertEquals(match, actual,
                "Invalid result when invoked with a GTIN/UPC corresponding to a food previously found by name!");
    }

    @Test
    void testGetByGtinUpcReturnsResultWhenPreviouslyFoundById() throws FoodDataAPIException {
        Food match = new Food("some food", 1234, "1234", null, null);

        when(foodDataFinderMock.findById(1234)).thenReturn(match);

        foodCache.getById(1234);

        Food actual = foodCache.getByGtinUpc("1234");

        assertEquals(match, actual,
                "Invalid result when invoked with a GTIN/UPC corresponding to a food previously found by FDC ID!");
    }

    @Test
    void testStoreStoresCorrectlyWhenEmpty() throws IOException {
        foodCache.store();

        assertTrue(Files.exists(Path.of(TEST_CACHE_FILE)),
                "Cache should create storage file!");

        assertEquals(3, Files.lines(Path.of(TEST_CACHE_FILE)).count(),
                "Cache should store the 3 maps!");

        Files.lines(Path.of(TEST_CACHE_FILE)).forEach(s -> assertEquals("{}", s, "Cache should store empty maps!"));
    }

    @Test
    void testStoreStoresCorrectlyWhenSomeEmptySomeNot() throws IOException, FoodDataAPIException {
        Food match = new Food("some food", 1234, "1234", null, null);

        when(foodDataFinderMock.findById(1234)).thenReturn(match);

        foodCache.getById(1234);

        foodCache.store();

        assertTrue(Files.exists(Path.of(TEST_CACHE_FILE)),
                "Cache should create storage file!");

        assertEquals(3, Files.lines(Path.of(TEST_CACHE_FILE)).count(),
                "Cache should store the 3 maps!");

        List<String> lines = Files.lines(Path.of(TEST_CACHE_FILE)).toList();

        assertEquals("{}", lines.get(0), "First map should be empty!");
        assertEquals(GSON.toJson(foodCache.getByIdMap()), lines.get(1), "Incorrect storage for second map!");
        assertEquals(GSON.toJson(foodCache.getByGtinUpcMap()), lines.get(2), "Incorrect storage for third map!");
    }

    @Test
    void testStoreStoresCorrectlyWhenPreviouslyStored() throws IOException, FoodDataAPIException {
        Food match = new Food("some food", 1234, "1234", null, null);

        when(foodDataFinderMock.findById(1234)).thenReturn(match);

        foodCache.getById(1234);

        foodCache.store();
        foodCache.store();

        assertTrue(Files.exists(Path.of(TEST_CACHE_FILE)),
                "Cache should create storage file!");

        assertEquals(3, Files.lines(Path.of(TEST_CACHE_FILE)).count(),
                "Cache should store the 3 maps!");

        List<String> lines = Files.lines(Path.of(TEST_CACHE_FILE)).toList();

        assertEquals("{}", lines.get(0), "First map should be empty!");
        assertEquals(GSON.toJson(foodCache.getByIdMap()), lines.get(1), "Incorrect storage for second map!");
        assertEquals(GSON.toJson(foodCache.getByGtinUpcMap()), lines.get(2), "Incorrect storage for third map!");
    }

    @Test
    void testConstructorCorrectInitializationWhenNoFile() {
        assertTrue(foodCache.getByFoodNameMap().isEmpty()
                && foodCache.getByIdMap().isEmpty()
                && foodCache.getByGtinUpcMap().isEmpty(),
                "If there was no cache file to load cache should create empty maps!");
    }

    @Test
    void testConstructorCorrectInitializationWhenCacheFileExists() throws FoodDataAPIException {
        Food match = new Food("some food", 1234, "1234", null, null);

        when(foodDataFinderMock.findById(1234)).thenReturn(match);

        foodCache.getById(1234);

        foodCache.store();

        FoodCache fromFileFoodCache = new FoodCache(foodDataFinderMock, Path.of(TEST_CACHE_FILE));

        var expectedFoodMap = foodCache.getByFoodNameMap().entrySet();
        var actualFoodMap = fromFileFoodCache.getByFoodNameMap().entrySet();
        assertTrue(expectedFoodMap.containsAll(actualFoodMap) && actualFoodMap.containsAll(expectedFoodMap),
                "Incorrect loading of food name map from file!");

        var expectedIdMap = foodCache.getByIdMap().entrySet();
        var actualIdMap = fromFileFoodCache.getByIdMap().entrySet();
        assertTrue(expectedIdMap.containsAll(actualIdMap) && actualIdMap.containsAll(expectedIdMap),
                "Incorrect loading of FDC ID map from file!");

        var expectedGtinUpcMap = foodCache.getByGtinUpcMap().entrySet();
        var actualGtinUpcMap = fromFileFoodCache.getByGtinUpcMap().entrySet();

        assertTrue(expectedGtinUpcMap.containsAll(actualGtinUpcMap) && actualGtinUpcMap.containsAll(expectedGtinUpcMap),
                "Incorrect loading of GTIN/UPC map from file!");
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.list(Path.of(""))
                .filter(p -> p.getFileName().toString().startsWith("cache-logs"))
                .forEach(p -> {
                    try {
                        Files.deleteIfExists(p);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        Files.deleteIfExists(Path.of(TEST_CACHE_FILE));
    }

















}