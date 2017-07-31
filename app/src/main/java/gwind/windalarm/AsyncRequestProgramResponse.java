package gwind.windalarm;

import java.util.List;

/**
 * Created by giacomo on 01/07/2015.
 */
public interface AsyncRequestProgramResponse {

    void processFinish(List<WindAlarmProgram> list, boolean error, String errorMessage);
}