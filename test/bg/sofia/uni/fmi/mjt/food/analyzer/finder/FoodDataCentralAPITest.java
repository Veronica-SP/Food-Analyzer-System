package bg.sofia.uni.fmi.mjt.food.analyzer.finder;

import bg.sofia.uni.fmi.mjt.food.analyzer.Food;
import bg.sofia.uni.fmi.mjt.food.analyzer.exceptions.FoodDataAPIException;
import bg.sofia.uni.fmi.mjt.food.analyzer.finder.response.ErrorResponse;
import bg.sofia.uni.fmi.mjt.food.analyzer.finder.response.SuccessResponse;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FoodDataCentralAPITest {
    private static final Gson GSON = new Gson();

    private static final ErrorResponse BAD_REQUEST_RESPONSE =
            new ErrorResponse("API_KEY_MISSING", "No api_key was supplied. Get one at https://api.nal.usda.gov:443" );

    private static final SuccessResponse NO_MATCHES_RESPONSE =
            new SuccessResponse(0, List.of());

    @Mock
    private HttpClient clientMock;

    @Mock
    private HttpResponse<String> notFoundResponseMock;

    @Mock
    private HttpResponse<String> badRequestResponseMock;

    @Mock
    private HttpResponse<String> okResponseMock;

    private FoodDataFinder finder;

    @BeforeEach
    void setUp() {
        when(notFoundResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_NOT_FOUND);
        when(badRequestResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_BAD_REQUEST);
        when(badRequestResponseMock.body()).thenReturn(GSON.toJson(BAD_REQUEST_RESPONSE));
        when(okResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);

        finder = new FoodDataCentralAPI(clientMock, "1234");
    }

    @Test
    void testFindByIdNoMatches() throws IOException, InterruptedException, FoodDataAPIException {
        when(clientMock.send(any(HttpRequest.class),
                ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(notFoundResponseMock);

        assertNull(finder.findById(1), "Food data finder should return null when there are no id matches!");
    }

    @Test
    void testFindByIdBadRequest() throws IOException, InterruptedException {
        when(clientMock.send(any(HttpRequest.class),
                ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(badRequestResponseMock);

        assertThrows(FoodDataAPIException.class,
                () -> finder.findById(1),
                "When there is a bad request to the API findById should throw a FoodDataAPIException!");
    }

    @Test
    void testFindByIdWhenMatchFound() throws IOException, InterruptedException, FoodDataAPIException {
        Food match = new Food("rafaello", 415269, null, null, null);
        when(okResponseMock.body()).thenReturn(GSON.toJson(match));

        when(clientMock.send(any(HttpRequest.class),
                ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(okResponseMock);

        assertEquals(match, finder.findById(415269),
                "Wrong result when invoked with a valid FDC ID!");
    }


    @Test
    void testFindByFoodNameNoMatches() throws IOException, InterruptedException, FoodDataAPIException {
        when(okResponseMock.body()).thenReturn(GSON.toJson(NO_MATCHES_RESPONSE));

        when(clientMock.send(any(HttpRequest.class),
                ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(okResponseMock);

        assertEquals(0, finder.findByFoodName("some food").size(),
               "The returned collection should be empty when there are no food matches!");
    }

    @Test
    void testFindByFoodNameUnmodifiable() throws IOException, InterruptedException {
        when(okResponseMock.body()).thenReturn(GSON.toJson(NO_MATCHES_RESPONSE));

        when(clientMock.send(any(HttpRequest.class),
                ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(okResponseMock);

        assertThrows(UnsupportedOperationException.class,
                () -> finder.findByFoodName("some food").clear(),
                "The returned collection should be unmodifiable!");
    }

    @Test
    void testFindByFoodNameBadRequest() throws IOException, InterruptedException {
        when(clientMock.send(any(HttpRequest.class),
                ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(badRequestResponseMock);

        assertThrows(FoodDataAPIException.class,
                () -> finder.findByFoodName("some food"),
                "When there is a bad request to the API findByFoodName should throw a FoodDataAPIException!");
    }

    @Test
    void testFindByFoodNameResultsOnSinglePage() throws IOException, InterruptedException, FoodDataAPIException {
        Food match = new Food("some food", 1234, null, null, null);

        Food[] results = new Food[5];
        Arrays.fill(results, match);
        List<Food> expected = List.of(results);

        SuccessResponse successResponse = new SuccessResponse(5, expected);
        when(okResponseMock.body()).thenReturn(GSON.toJson(successResponse));

        when(clientMock.send(any(HttpRequest.class),
                ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(okResponseMock);

        Collection<Food> actual = finder.findByFoodName("some food");
        assertTrue(actual.containsAll(expected) && expected.containsAll(actual),
                "Wrong result when invoked with a valid food name!");
    }

    @Test
    void testFindByFoodNameResultsAlwaysMax10() throws IOException, InterruptedException, FoodDataAPIException {
        Food match = new Food("some food", 1234, null, null, null);

        Food[] resultsFirstPage = new Food[10];
        Arrays.fill(resultsFirstPage, match);
        List<Food> expectedFirstPage = List.of(resultsFirstPage);

        SuccessResponse successResponse = new SuccessResponse(25, expectedFirstPage);
        when(okResponseMock.body()).thenReturn(GSON.toJson(successResponse));

        when(clientMock.send(any(HttpRequest.class),
                ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(okResponseMock);

        assertEquals(10, finder.findByFoodName("some food").size(),
                "Even if the found results are more than 10, the collection must be of size maximum 10!");
    }
}