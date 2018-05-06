package de.tu_darmstadt.informatik.newapp.Client;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import de.tu_darmstadt.informatik.newapp.Client.Clubber_Core.ClubberDevicesFragList;
import de.tu_darmstadt.informatik.newapp.Client.Clubber_Core.MusicPlayerFragmentClubber;
import de.tu_darmstadt.informatik.newapp.Client.Clubber_Core.WiFiClubberBroadCastReciever;
import de.tu_darmstadt.informatik.newapp.R;

/**
 * Created by Rashmi on 11/16/2016.
 */

public class uiClubber extends AppCompatActivity implements TabLayout.OnTabSelectedListener, WifiP2pManager.ChannelListener, ClubberDevicesFragList.PeerAvailable_FragListener
{
    public String MUSIC_LIST_SEPERATOR = "%%%";
    private boolean isWifiSet = false;
    public WifiP2pManager mManager;
    public WifiP2pManager.Channel mChannel;
    public WiFiClubberBroadCastReciever mReceiver;
    public IntentFilter mIntentFilter;
    private boolean chn_is_restricted = false;
    ProgressDialog prog_diag = null;


    //Tabbed Layout Details
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Pager_Client adapter;

    //Music Player
    private MusicPlayerFragmentClubber songPlay;
    private uiClientNowPlaying play_ListClubber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clubber);

        // get action bar
        ActionBar actionBar = getSupportActionBar();
        //getActionBar();

        // Enabling Up / Back navigation
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarHost);
        //setSupportActionBar(toolbar);

        tabLayout = (TabLayout) findViewById(R.id.tabLayoutClubber);
        tabLayout.addTab(tabLayout.newTab().setText("Check Party"));
        tabLayout.addTab(tabLayout.newTab().setText("Now Playing"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.vPagerClubber);
        adapter = new Pager_Client(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        tabLayout.setOnTabSelectedListener(this);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


        //WiFiDirect Clubber

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiClubberBroadCastReciever(mManager, mChannel, this);

        mIntentFilter = new IntentFilter();


        // need these intent filters to catch the Wi-fi direct events
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        //Music Player

        songPlay = new MusicPlayerFragmentClubber();

        play_ListClubber = new uiClientNowPlaying();

    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    //WiFiDirect Functions : manna Feb 2017

    @Override
    public void onResume() {
        super.onResume();
        mReceiver = new WiFiClubberBroadCastReciever(mManager, mChannel, this);
        registerReceiver(mReceiver, mIntentFilter);

        searchDevices();

    }

    public void wifiEnable() {
        WifiManager wim = (WifiManager) this.getSystemService(this.WIFI_SERVICE);

        wim.setWifiEnabled(true);
    }


    public void onInitiateDiscovery() {
        if (prog_diag != null && prog_diag.isShowing()) {
            prog_diag.dismiss();
        }

        prog_diag = ProgressDialog.show(this, "Press Back to cancel",
                "finding peers", true, true,
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        // stop discovery
                        mManager.stopPeerDiscovery(mChannel,
                                new WifiP2pManager.ActionListener() {
                                    @Override
                                    public void onFailure(int reason) {
                                    }

                                    @Override
                                    public void onSuccess() {

                                    }
                                });
                    }
                });
    }

    @Override
    public void searchDevices() {

        wifiEnable();

        chn_is_restricted = false;

        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reasonCode) {

                mManager.stopPeerDiscovery(mChannel,
                        new WifiP2pManager.ActionListener() {
                            @Override
                            public void onFailure(int reason) {

                            }

                            @Override
                            public void onSuccess() {
                                mManager.discoverPeers(mChannel,
                                        new WifiP2pManager.ActionListener() {
                                            @Override
                                            public void onSuccess() {

                                            }

                                            @Override
                                            public void onFailure(int reasonCode) {

                                            }
                                        });
                            }
                        });
            }
        });
    }

    @Override
    public void playtheSong(String url, long timing, int pos)
    {

        songPlay.startPlaying(url,timing,pos);

    }
    @Override
    public void stop_Playing()
    {
        songPlay.stopPlaying();
    }
    @Override
    public void updatePlayList(String playList)
    {

       // play_ListClubber.setPlayLists(playList);

        String[] songs = playList.split(MUSIC_LIST_SEPERATOR);
       // String listforadapter[] = new String[songs.length];
        //for(int i=0;i<songs.length;i++)
        //{
         //   listforadapter[i]=songs[i];
            //Log.d("SongName:",songs[i]);
        //}


        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, songs);
        ListView listView = (ListView)findViewById(R.id.playListClubber);

        listView.setAdapter(itemsAdapter);
    }
    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);

    }

    @Override
    public void onDestroy() {
        leave();

        super.onDestroy();
    }


    public void resetData() {
        ClubberDevicesFragList fragmentList = (ClubberDevicesFragList) getFragmentManager()
                .findFragmentById(R.id.device_list_frag_clubber);

        if (fragmentList != null) {
            fragmentList.clearPeers();
        }
    }

    @Override
    public void onChannelDisconnected() {

        if (mManager != null && !chn_is_restricted) {
            Toast.makeText(this, "Wi-fi Direct Channel lost. Trying again...",
                    Toast.LENGTH_LONG).show();
            resetData();

            chn_is_restricted = true;
            mManager.initialize(this, getMainLooper(), this);
        } else {
            Toast.makeText(this, "Channel not found. Enable Disable WiFi", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void musicPlayerDetails(WifiP2pDevice device) {

        //Do mUsic stuff here later

    }

    @Override
    public void musicPlayerInfo(WifiP2pInfo info) {

        //Do music stuff here later

    }

    @Override
    public void stopDisconnect() {

        if (mManager != null) {


            final ClubberDevicesFragList fragment = (ClubberDevicesFragList) getFragmentManager()
                    .findFragmentById(R.id.device_list_frag_clubber);

            if (fragment.getDevice() == null
                    || fragment.getDevice().status == WifiP2pDevice.CONNECTED) {

            } else if (fragment.getDevice().status == WifiP2pDevice.AVAILABLE
                    || fragment.getDevice().status == WifiP2pDevice.INVITED
                    || fragment.getDevice().status == WifiP2pDevice.CONNECTED) {
                mManager.cancelConnect(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailure(int reasonCode) {

                    }
                });
            }
        }
    }


    @Override
    public void connect(WifiP2pConfig config) {
        if (mManager == null) {
            return;
        }

        WifiP2pConfig newConfig = config;
        newConfig.groupOwnerIntent = 1;

        mManager.connect(mChannel, newConfig, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(uiClubber.this,
                        "Retrying Connection", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void leave() {
        if (mManager == null) {
            return;
        }

        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onFailure(int reasonCode) {


            }

            @Override
            public void onSuccess() {
                Toast.makeText(uiClubber.this, "One Device Disconnected",
                        Toast.LENGTH_SHORT).show();

            }
        });
    }


    public void setIsWifiSet(boolean isWifiSet) {
        this.isWifiSet = isWifiSet;
    }
}

