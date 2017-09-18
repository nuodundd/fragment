package com.nuodundd.viewstack;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.View;
import android.view.Window;

/**
 * Created by nuodundd on 17/1/17.
 */

public interface IFragment {
    /** Standard activity result: operation canceled. */
    public static final int RESULT_CANCELED    = 0;
    /** Standard activity result: operation succeeded. */
    public static final int RESULT_OK           = -1;
    /** Start of user-defined activity results. */
    public static final int RESULT_FIRST_USER   = 1;
    /**
     * void onAttach(Activity activity);
     * <p>
     * void onCreate(Bundle savedInstanceState);
     * <p>
     * View onCreateView(LayoutInflater inflater, ViewGroup container,
     * Bundle savedInstanceState);
     * <p>
     * void onBecomeVisible();
     * <p>
     * void onResume();
     * <p>
     * void onPause();
     * <p>
     * void onStop();
     * <p>
     * void onDestroyView();
     * <p>
     * void onDestroy();
     * <p>
     * void onDetach();
     */
    void finish();

    /**
     * @param frament
     * @param pop     true pop to the frament in the stack if exist; false start anew directly
     */
    void startNewFragment(Class<? extends Fragment> frament, boolean pop);

    void startNewFragment(Class<? extends Fragment> frament);

    void startNewFragment(Intent intent, Class<? extends Fragment> frament);

    void startNewFragment(Intent intent);
    /**
     * @param intent
     * @param frament
     * @param pop     true pop to the frament in the stack if exist; false start anew directly
     */
    void startNewFragment(Intent intent, Class<? extends Fragment> frament, boolean pop);

    Intent getIntent();

    void setIntent(Intent intent);

    void onNewIntent(Intent intent);

    boolean isDestroy();

    Context getContext();

    Activity getActivity();

    boolean isActive();

    void bindManager(IFragmentManager manager);

    void setView(View contentView);

    View getView();

    void onLowMemory();

    void startFragmentForResult(Intent intent, Class<? extends Fragment> fragment, int requestCode);

//    void onFragmentResult(int requestCode, int resultCode, Intent data);

//    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onRequest(int requestCode);

    int requestCode();

    Window getWindow();

    View findViewById(int id);

    void setContentView(int id);

    void setContentView(View view);

    void startActivity(Intent intent);

    boolean bindService(Intent service, ServiceConnection conn,
                        int flags);

    void unbindService(ServiceConnection conn);

    void startActivityForResult(Intent intent, int requestCode);

    void startService(Intent intent);

    String getString(int id);
    public boolean onKeyBack();
}
