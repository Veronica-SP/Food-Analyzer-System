package bg.sofia.uni.fmi.mjt.food.analyzer.request;

import bg.sofia.uni.fmi.mjt.food.analyzer.exceptions.UnableToPerformOpException;
import bg.sofia.uni.fmi.mjt.food.analyzer.exceptions.InvalidRequestException;
import bg.sofia.uni.fmi.mjt.food.analyzer.exceptions.NoMatchesException;

public interface RequestHandler {

    /**
     * Executes the specified request.
     *
     * @param request - the request to be executed.
     *
     * @throws  IllegalArgumentException if the request is null or blank
     * @throws  InvalidRequestException if the request is not a valid request
     * @throws  NoMatchesException if there are no results found
     * @throws  UnableToPerformOpException if there occurs an error while trying to perform the operation
     *
     * @return a string representation of the response to the request
     */
    String execute(String request) throws InvalidRequestException,
            NoMatchesException, UnableToPerformOpException;

}
