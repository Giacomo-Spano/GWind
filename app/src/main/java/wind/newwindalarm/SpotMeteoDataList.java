package wind.newwindalarm;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import wind.newwindalarm.data.Forecast;
import wind.newwindalarm.data.MeteoStationData;

public class SpotMeteoDataList implements Serializable {

    public class SpotMeteoData implements Serializable {
        public  long spotId;
        public MeteoStationData md;
        private List<MeteoStationData> history = new ArrayList<MeteoStationData>();
        private transient Bitmap[] webcamBitmap = new Bitmap[3];
        private long[] webcamWindId = new long[3];
        private long[] webcamImageRequestInprogress = new long[3]; // windid dell'immagine richiesta
        private Forecast forecast;
    }

    private final String ClassFileName = "SpotMeteoDataListClass";
    private List<SpotMeteoData> lastMeteoData = new ArrayList<SpotMeteoData>();

    public List<MeteoStationData> getMeteoDataList() {
        List<MeteoStationData> list = new ArrayList<>();
        for (SpotMeteoData smd : lastMeteoData)
            list.add(smd.md);
        return list;
    }

    private SpotMeteoData getFromId(long spotId) {
        if (lastMeteoData == null) return null;
        for (SpotMeteoData smd : lastMeteoData) {
            if (smd.spotId == spotId)
                return smd;
        }
        return null;
    }

    public MeteoStationData getLastMeteoData(long spotId) {
        SpotMeteoData smd = getFromId(spotId);
        if (smd == null)
            return null;
        else
            return smd.md;
    }

    public void setLastMeteoData(long spotId, MeteoStationData md) {
        SpotMeteoData smd = getFromId(spotId);
        if (smd == null) {
            smd = addSpotMeteoData(spotId);
        }
        smd.md = md;
    }

    public List<MeteoStationData> getHistory(long spotId) {
        SpotMeteoData smd = getFromId(spotId);
        if (smd == null || smd.history == null || smd.history.size() == 0)
            return null;
        return smd.history;
    }

    public long getLastHistoryId(long spotId) {
        List<MeteoStationData> list = getHistory(spotId);
        if (list == null) return -1;
        return list.get(list.size() - 1).id;
    }

    public void setHistory(long spotId, List<MeteoStationData> history) {
        SpotMeteoData smd = getFromId(spotId);
        if (smd == null) {
            smd = addSpotMeteoData(spotId);
        }

        for (MeteoStationData md : history) {
            smd.history.add(md);
        }
    }

    @NonNull
    private SpotMeteoData addSpotMeteoData(long spotId) {

        if (spotId < 0) return null;

        SpotMeteoData smd;
        smd = new SpotMeteoData();
        smd.spotId = spotId;
        lastMeteoData.add(smd);
        return smd;
    }

    public long getLastWindId() {

        long windId = 0;
        for (SpotMeteoData smd : lastMeteoData) {
            if (smd.md != null && smd.md.id > windId)
                windId = smd.md.id;
        }
        return windId;
    }

    public Bitmap getWebCamImage(long spotId, int index) {

        SpotMeteoData smd = getFromId(spotId);
        if (index < 1 || index > smd.webcamBitmap.length)
            return null;
        return smd.webcamBitmap[index - 1];
    }

    public long getWebCamWindId(long spotId, int index) {

        SpotMeteoData smd = getFromId(spotId);
        if (smd == null || smd.webcamBitmap == null || index < 1 || index > smd.webcamBitmap.length)
            return -1;
        return smd.webcamWindId[index - 1];
    }

    public void setWebCamImage(long spotId, int index, Bitmap bmp, long windId) {
        SpotMeteoData smd = getFromId(spotId);
        if (smd == null) {
            smd = addSpotMeteoData(spotId);
        }
        if (smd.webcamBitmap == null || index < 1 || index > smd.webcamBitmap.length)
            return;
        smd.webcamBitmap[index - 1] = bmp;
        smd.webcamWindId[index - 1] = windId;
        smd.webcamImageRequestInprogress[index - 1] = 0;
    }

    public void setWebCamImageRequestInProgress(long spotId, int index, long requestedWindId) {
        SpotMeteoData smd = getFromId(spotId);
        if (smd == null) {
            smd = addSpotMeteoData(spotId);
        }
        if (smd.webcamBitmap == null || index < 1 || index > smd.webcamBitmap.length)
            return;
        smd.webcamImageRequestInprogress[index - 1] = requestedWindId;
    }

    public long getWebCamImageRequestInProgressId(long spotId, int index) {
        SpotMeteoData smd = getFromId(spotId);
            /*if (smd == null) {
                smd = new SpotMeteoData();
            }*/
        if (smd.webcamBitmap == null || index < 1 || index > smd.webcamBitmap.length)
            return 0;
        return smd.webcamImageRequestInprogress[index - 1];
    }

    public Forecast getForecast(long spotId) {
        SpotMeteoData smd = getFromId(spotId);
        if (smd == null)
            return null;
        else
            return smd.forecast;
    }

    public void setForecast(long spotId, Forecast f) {
        if (spotId < 0) return;
        SpotMeteoData smd = getFromId(spotId);
        if (smd == null) {
            smd = addSpotMeteoData(spotId);
        }
        smd.forecast = f;
    }

    public void saveToFile(Context context) {
        String fileName = ClassFileName;
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(this);
            os.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public SpotMeteoDataList loadFromFile(Context context) {
        String fileName = ClassFileName;
        try {
            FileInputStream fis = context.openFileInput(fileName);
            ObjectInputStream is = new ObjectInputStream(fis);
            SpotMeteoDataList spotMeteoDataClass = (SpotMeteoDataList) is.readObject();
            //lastMeteoData = spotMeteoDataClass.lastMeteoData;
            is.close();
            fis.close();
            return spotMeteoDataClass;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (java.lang.ClassCastException e) {

        }
        return null;
    }

}
