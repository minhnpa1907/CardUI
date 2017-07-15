package com.minhnguyen.cardui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.minhnguyen.cardui.R;
import com.minhnguyen.cardui.activity.MeetingActivity;
import com.minhnguyen.cardui.activity.WorkContactActivity;
import com.minhnguyen.cardui.adapter.ItemAdapter;
import com.minhnguyen.cardui.constant.Keys;
import com.minhnguyen.cardui.constant.StringValue;
import com.minhnguyen.cardui.database.StatisticalDatabaseHelper;
import com.minhnguyen.cardui.model.Item;
import com.minhnguyen.cardui.model.Organization;
import com.minhnguyen.cardui.model.Statistic;
import com.minhnguyen.cardui.model.User;
import com.minhnguyen.cardui.utilities.Utilities;
import com.nxp.nfclib.CardType;
import com.nxp.nfclib.KeyType;
import com.nxp.nfclib.NxpNfcLib;
import com.nxp.nfclib.defaultimpl.KeyData;
import com.nxp.nfclib.desfire.DESFireFactory;
import com.nxp.nfclib.desfire.IDESFireEV2;

import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by minhnguyen on 1/16/17.
 */

public class AccessControlFragment extends Fragment implements View.OnClickListener {

    // TapLinx library instance
    private NxpNfcLib nxpNfcLib = null;

    // Card Object
    private IDESFireEV2 objDESFireEV2 = null;

    @BindView(R.id.llMain)
    LinearLayout llMain;
    @BindView(R.id.ivProfile)
    ImageView ivProfile;
    @BindView(R.id.rvUserInformation)
    RecyclerView rvUserInformation;
    @BindView(R.id.btnMeeting)
    Button btnMeeting;
    @BindView(R.id.btnWorkContact)
    Button btnWorkContact;
    @BindView(R.id.llReading)
    LinearLayout llReading;
    @BindView(R.id.llWaiting)
    LinearLayout llWaiting;

    List<Item> items;

    public static AccessControlFragment newInstance() {

        Bundle args = new Bundle();

        AccessControlFragment fragment = new AccessControlFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_access_control, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        btnMeeting.setOnClickListener(this);
        btnWorkContact.setOnClickListener(this);
    }

    private void initializeAdapter() {
        ItemAdapter adapter = new ItemAdapter(getContext(), items);
        rvUserInformation.setAdapter(adapter);
        rvUserInformation.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void onNFCDetected(Intent intent, NxpNfcLib nxpNfcLib) {
        this.nxpNfcLib = nxpNfcLib;

        clearData();
        readingView();
        cardLogic(intent);
    }

    /**
     * Main method for card logic, only this method for all stuff
     * No other handlers need to be implemented
     *
     * @param intent
     */
    private void cardLogic(Intent intent) {
        // Get care type from nfc intent and save it
        CardType cardType = nxpNfcLib.getCardType(intent);

        if (null == cardType) {
            Utilities.getInstance().toastMessage(getContext(), "Please hold the tag at least 2 second!");
            return;
        }

        Utilities.getInstance().setDeviceVibrate(getActivity(), 50);

        switch (cardType) {
            case DESFireEV2:
                Log.d(StringValue.TAG, "DESFireEV2 nè!!!!");

                /**
                 * Get a reference of the DESFireEV2 object
                 * from the library
                 */
                objDESFireEV2 = DESFireFactory.getInstance().getDESFireEV2(nxpNfcLib.getCustomModules());

                try {
                    // Call connect() to establish the card object properly
                    objDESFireEV2.getReader().connect();

                    cardLogicDESFireEV2();
                } catch (Throwable t) {
                    t.printStackTrace();
                    failedCardTag();
                }
                break;
            default:
                failedCardTag();
                break;
        }
    }

    private void cardLogicDESFireEV2() {
        // Information Stuff
        byte[] appId = new byte[]{0x12, 0x00, 0x00};
        // Data de ghi vao the
        int timeout = 2000;
        int fileNumber = 2;

        /**
         * It might be required to add a timeout to prevent a failure
         * while the authentication takes place
         * (depends on the phone type and Android version)
         */
        objDESFireEV2.getReader().setTimeout(timeout);

        // Create a temp Java Keys object and
        // initialize it with the key value from the byte array (DEFAULT_KEY_2KTDES)
        Key key = new SecretKeySpec(Keys.DEFAULT_KEY_2KTDES, "DESede");
        KeyData keyData = new KeyData();
        keyData.setKey(key);

        // Authenticate method uses the temp key object
        // card key 0 and the authentication type "Native"
        objDESFireEV2.authenticate(0, IDESFireEV2.AuthType.Native,
                KeyType.TWO_KEY_THREEDES, keyData);

        Log.d(StringValue.TAG, "DESFireEv2 authenticated");

        // Start reading
        // Chon app de doc
        objDESFireEV2.selectApplication(appId);

        objDESFireEV2.authenticate(0, IDESFireEV2.AuthType.Native,
                KeyType.TWO_KEY_THREEDES, keyData);

        // Doc data tu, readData(so file, vi tri bat dau, do dai)
        try {
            // Init value
            byte[] dataCount = getDataCount(fileNumber);
            String strData = Utilities.getInstance().convertFromByteArrayToString(dataCount);
            String strDataSplit[] =
                    Utilities.getInstance().splitString(strData, StringValue.DATA_COUNT_SEPARATE);
            // Lay datalength kieu string sau khi da tach dau : ra
            strData = strDataSplit[0];
            // Convert tu string length sang int, co duoc do dai cua Data
            int datalength = Integer.parseInt(strData);

            // Dua vao length cua data, lay data trong the ra va convert thanh chuoi string
            byte[] byteData = getDataByteArray(fileNumber, 5, datalength);
            strData = Utilities.getInstance().convertFromByteArrayToString(byteData);

            // convert json to object
            Gson gson = new Gson();
            User user = gson.fromJson(strData, User.class);
            items = getItemList(user);

            successfulCardTag();
        } catch (Throwable t) {
            t.printStackTrace();
            failedCardTag();
        }

        // Doc the xong thi dong object lai
        // (Co the khong can cung dc vi da set time out)
        objDESFireEV2.getReader().close();
    }

    private void clearData() {
        items = new ArrayList<>();
        initializeAdapter();
    }

    // Method doc so do dai cua data trong the
    private byte[] getDataCount(int fileNumber) {
        return getDataByteArray(fileNumber, 0, 5);
    }

    // Method doc data tu vi tri bat dau voi do dai cho truoc
    private byte[] getDataByteArray(int fileNumber, int startPosition, int length) {
        return objDESFireEV2.readData(fileNumber, startPosition, length);
    }


    // Method tach chuoi string data ra de lay tung object
    private List<Item> getItemList(User user) {
        List<Item> items = new ArrayList<>();
        user.setContext(getContext());

        items.add(user.getItemUserName());
        items.add(user.getItemUserGender());
        items.add(user.getItemUserPosition());
        items.add(user.getItemUserOrganization());
        items.add(user.getItemUserNumber());
        items.add(user.getItemUserBirthday());
        items.add(user.getItemUserIDNumber());
        items.add(user.getItemUserPhoneNumber());
        items.add(user.getItemUserEmail());

        return items;
    }

    private void successfulCardTag() {
        new CountDownTimer(1000, 1000) {

            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                Utilities.getInstance().setDeviceVibrate(getActivity(), 50);
                initializeAdapter();
                // Tag the thanh cong thi store lai database va doi View
//                storeDatabase();
                informationView();
            }
        }.start();
    }

    private void failedCardTag() {
        new CountDownTimer(1000, 1000) {

            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                Utilities.getInstance().setDeviceVibrate(getActivity(), 500);
                clearData();
                waitingView();
            }
        }.start();
    }

    private void storeDatabase() {
        User user = new User();
        user.setUserPosition(items.get(0).getValue());
        user.setUserOrganization(items.get(1).getValue());
        user.setUserBirthday(items.get(2).getValue());
        user.setUserIDNumber(items.get(3).getValue());

        Statistic statistic = new Statistic();
        statistic.setDate(new Date());
        statistic.setOrganization(new Organization(user.getUserOrganization()));
        statistic.setUser(user);

        StatisticalDatabaseHelper databaseHelper = StatisticalDatabaseHelper.getInstance(getContext());
        databaseHelper.addStatistic(statistic);
    }

    private void waitingView() {
        llMain.setVisibility(View.GONE);
        llReading.setVisibility(View.GONE);
        llWaiting.setVisibility(View.VISIBLE);
    }

    private void readingView() {
        llMain.setVisibility(View.GONE);
        llReading.setVisibility(View.VISIBLE);
        llWaiting.setVisibility(View.GONE);
    }

    private void informationView() {
        llMain.setVisibility(View.VISIBLE);
        llReading.setVisibility(View.GONE);
        llWaiting.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnMeeting:
                Intent intentMeeting = new Intent(getActivity(), MeetingActivity.class);
                startActivity(intentMeeting);
                break;
            case R.id.btnWorkContact:
                Intent intentWorkContact = new Intent(getActivity(), WorkContactActivity.class);
                startActivity(intentWorkContact);
                break;
            default:
                break;
        }
    }
}
