package bg.sofia.uni.fmi.mjt.food.analyzer.formatter;

public interface ServerResponseFormatter {

    /**
     * @param request - the request for which the response was received.
     * @param response - the server response to be formatted.
     *
     * Formats a JSON server response to a readable user output
     *
     * @return the formatted response
     */
    String format(String request, String response);
}
