package com.minhnguyen.cardui.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.minhnguyen.cardui.IDialogWriteTagsListener;
import com.minhnguyen.cardui.R;
import com.minhnguyen.cardui.activity.MainActivity;
import com.minhnguyen.cardui.constant.Keys;
import com.minhnguyen.cardui.constant.StringValue;
import com.minhnguyen.cardui.utilities.Utilities;
import com.nxp.nfclib.CardType;
import com.nxp.nfclib.KeyType;
import com.nxp.nfclib.NxpNfcLib;
import com.nxp.nfclib.defaultimpl.KeyData;
import com.nxp.nfclib.desfire.DESFireFactory;
import com.nxp.nfclib.desfire.DESFireFile;
import com.nxp.nfclib.desfire.EV1ApplicationKeySettings;
import com.nxp.nfclib.desfire.IDESFireEV2;

import java.security.Key;

import javax.crypto.spec.SecretKeySpec;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by minhnguyen on 2/14/17.
 */
public class DialogWriteTagsFragment extends DialogFragment {

    public static final String TAG = DialogWriteTagsFragment.class.getSimpleName();

    // TapLinx library instance
    private NxpNfcLib nxpNfcLib = null;

    // Card Object
    private IDESFireEV2 objDESFireEV2 = null;

    @BindView(R.id.pbWriting)
    ProgressBar pbWriting;
    @BindView(R.id.tvMessage)
    TextView tvMessage;

    private IDialogWriteTagsListener mListener;

    public DialogWriteTagsFragment() {

    }

    public static DialogWriteTagsFragment newInstance() {

        Bundle args = new Bundle();

        DialogWriteTagsFragment fragment = new DialogWriteTagsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mListener = (MainActivity) context;
        mListener.onNFCDialogDisplayed();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_write_tags, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initializeLibrary();
    }

    /**
     * Method for TapLinx library instance
     * Register activity with parameter Activity and String packgage key
     */
    @TargetApi(19)
    private void initializeLibrary() {
        nxpNfcLib = NxpNfcLib.getInstance();
        nxpNfcLib.registerActivity(getActivity(), StringValue.PACKAGE_KEY);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.onNFCDialogDismissed();
    }

    /**
     * deactivate NFC intents for the app when another app climbs the foreground
     */
    @Override
    public void onDestroyView() {
        Log.d(StringValue.TAG, "onDestroyView");
        super.onDestroyView();
    }

    public void onNFCDetected(Intent intent, byte[] data) {
        pbWriting.setVisibility(View.VISIBLE);
        tvMessage.setText(StringValue.MODE_WRITING);

        // intent contains the NFC type information
        cardLogic(intent, nxpNfcLib, data);
    }

    /**
     * Main method for card logic, only this method for all stuff
     * No other handlers need to be implemented
     *
     * @param intent
     */
    private void cardLogic(Intent intent, NxpNfcLib nxpNfcLib, byte[] data) {
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

                    cardLogicDESFireEV2(data);
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

    private void cardLogicDESFireEV2(byte[] data) {
        // Information Stuff
        byte[] appId = new byte[]{0x12, 0x00, 0x00};
        // size luu tru
        int fileSize = objDESFireEV2.getTotalMemory() - 2;
        // Data de ghi vao the
        int timeout = 2000;
        int fileNumber = 2;

        // Vi tri bat dau trong the de doc
        int startPos = 0;

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

        // Start writing
        objDESFireEV2.format();

        /**
         * Chua hieu cho nay lam
         */
        EV1ApplicationKeySettings.Builder appsetbuilder = new EV1ApplicationKeySettings.Builder();
        EV1ApplicationKeySettings appSettings = appsetbuilder.setAppKeySettingsChangeable(true)
                .setAppMasterKeyChangeable(true)
                .setAuthenticationRequiredForApplicationManagement(false)
                .setAuthenticationRequiredForDirectoryConfigurationData(false)
                .setKeyTypeOfApplicationKeys(KeyType.TWO_KEY_THREEDES).build();

        // Tao app voi appId va setting cua app
        objDESFireEV2.createApplication(appId, appSettings);
        // Tao xong roi chon app dua vao appId
        objDESFireEV2.selectApplication(appId);

        // Voi app dang chon hien tai tao file voi so file (fileNumber)
        objDESFireEV2.createFile(fileNumber, new DESFireFile.StdDataFileSettings(
                IDESFireEV2.CommunicationType.Plain, (byte) 0, (byte) 0, (byte) 0, (byte) 0, fileSize));

        objDESFireEV2.authenticate(0, IDESFireEV2.AuthType.Native,
                KeyType.TWO_KEY_THREEDES, keyData);


        // Viet vao the, writeData(so file, vi tri bat dau, du lieu ghi)
        try {
            objDESFireEV2.writeData(fileNumber, startPos, data);
            successfulCardTag();
        } catch (Throwable t) {
            t.printStackTrace();
            failedCardTag();
        }

        // Doc the xong thi dong object lai
        // (Co the khong can cung dc vi da set time out)
        objDESFireEV2.getReader().close();
    }

    private void successfulCardTag() {
        new CountDownTimer(2000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                // Dismiss dialog tag sau 2 giay
                // va toast ra ghi thanh cong
                Utilities.getInstance().setDeviceVibrate(getActivity(), 50);
//                Utilities.getInstance().toastMessage(getContext(), getString(R.string.message_write_successful));
                dismiss();
            }
        }.start();
    }

    private void failedCardTag() {
        new CountDownTimer(2000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                // Dismiss dialog tag sau 2 giay
                // va toast ra ghi that bai
                Utilities.getInstance().setDeviceVibrate(getActivity(), 200);
//                Utilities.getInstance().toastMessage(getContext(), getString(R.string.message_write_failed));
                dismiss();
            }
        }.start();
    }
}
