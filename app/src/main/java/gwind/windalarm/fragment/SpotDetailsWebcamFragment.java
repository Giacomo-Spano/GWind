package gwind.windalarm.fragment;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import gwind.windalarm.data.MeteoStationData;
import gwind.windalarm.R;
import gwind.windalarm.cardui.WebcamCard;
import gwind.windalarm.cardui.WebcamCardItem;

public class SpotDetailsWebcamFragment extends Fragment implements SpotDetailsFragmentInterface, WebcamCardItem.WebcamCardListener {

    private MeteoStationData meteoData;
    private long spotId;
    private WebcamCardItemList webcamCards;
    private int[] ids = {R.id.webcamcard1, R.id.webcamcard2, R.id.webcamcard3};

    private class WebcamCardItemList {
        public List<WebcamCardItem> list = new ArrayList<WebcamCardItem>();
    }

    public SpotDetailsWebcamFragment() {

        webcamCards = new WebcamCardItemList();

        for (int i = 0; i < ids.length; i++) {
            WebcamCardItem wci = new WebcamCardItem(this);
            webcamCards.list.add(wci);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            meteoData = (MeteoStationData) savedInstanceState.getSerializable("meteoData");
            spotId = savedInstanceState.getLong("spotId");

            int count = 1;
            for (WebcamCardItem wci : webcamCards.list) {

                long lastWebCamWindId = savedInstanceState.getLong("lastWebcamWindId"+count);
                //Bitmap bitmap = savedInstanceState.getParcelable("bitmap"+count);
                Bitmap bitmap = getImageBitmap(getContext(), "bitmap" + count, "jpg");
                wci.setWebCamImage(bitmap,lastWebCamWindId);

                count++;
            }
        }

        View v;
        v = inflater.inflate(R.layout.fragment_webcam, container, false);
        int count = 0;
        for (WebcamCardItem wci : webcamCards.list) {

            wci.card = (WebcamCard) v.findViewById(ids[count]);
            wci.card.init();
            if (meteoData != null && meteoData.webcamurlList != null &&
                    count < (meteoData.webcamurlList.size()) &&
                    meteoData.webcamurlList.get(count) != null) {
                wci.card.setVisibility(View.VISIBLE);
            } else {
                wci.card.setVisibility(View.GONE);
            }
            count++;
        }
        refreshData();
        return v;
    }

    public void saveImage(Context context, Bitmap b, String name, String extension){
        name=name+"."+extension;
        FileOutputStream out;
        try {
            out = context.openFileOutput(name, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap getImageBitmap(Context context,String name,String extension){
        name=name+"."+extension;
        try{
            FileInputStream fis = context.openFileInput(name);
            Bitmap b = BitmapFactory.decodeStream(fis);
            fis.close();
            return b;
        }
        catch(Exception e){
        }
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("meteoData", meteoData);
        outState.putLong("spotId", spotId);

        int count = 1;
        for (WebcamCardItem wci : webcamCards.list) {
            Bitmap bitmap = wci.getWebCamImage();
            //outState.putParcelable("bitmap"+count, bitmap);
            saveImage(getContext(), bitmap, "bitmap" + count, "jpg");
            long lastWebCamWindId = wci.getlastWebcamImageWindId();
            outState.putLong("lastWebcamWindId"+count, lastWebCamWindId);
            count++;
        }

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void setMeteoData(MeteoStationData meteoData) {
        this.meteoData = meteoData;
    }

    public void setSpotId(long id) {
        spotId = id;
    }

    public void refreshData() {

        if (meteoData == null || webcamCards == null)
            return;

        for (WebcamCardItem wci : webcamCards.list) {
            wci.update();
        }
    }

    @Override
    public void cardSelected() {
    }

    public void setWebCamImage(int n, Bitmap bmp, long lastWebcamImageWindId) {

        if (n < 1 || n > webcamCards.list.size())
            return;

        webcamCards.list.get(n - 1).setWebCamImage(bmp,lastWebcamImageWindId);
    }

    public void showWebCamProgressBar(int n) {

        if (webcamCards == null)
            return;

        if (n < 1 || n > webcamCards.list.size())
            return;
    }
}
