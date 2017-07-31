package gwind.windalarm;

/**
 * Created by giacomo on 01/07/2015.
 */
public interface AsyncPostProgramResponse {

    void processFinish(/*WindAlarmProgram wps*/Object obj, boolean error, String errorMessage);
}