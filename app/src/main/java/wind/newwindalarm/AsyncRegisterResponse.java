package wind.newwindalarm;

/**
 * Created by giacomo on 01/07/2015.
 */
public interface AsyncRegisterResponse {

    void processFinish(String json, boolean error, String errorMessage);
}