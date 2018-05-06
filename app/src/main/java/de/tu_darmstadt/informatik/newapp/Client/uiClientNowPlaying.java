package de.tu_darmstadt.informatik.newapp.Client;


import android.content.Context;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;


import de.tu_darmstadt.informatik.newapp.R;



/**
 *
 * Created by manna: for clubber on 28-02-2017.
 */


public class uiClientNowPlaying extends Fragment {


    private View rView2;
    private String playLists="";
    private ViewPager vPager;


    public String getPlayLists() {
        return playLists;
    }

    public void setPlayLists(String playLists) {
        this.playLists = playLists;
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rView2 = inflater.inflate(R.layout.uiclubber_nowplaying,container,false);

        vPager = (ViewPager) getActivity().findViewById(R.id.vPagerClubber);



        if (vPager != null) {

            vPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }





        return rView2;
    }




}

