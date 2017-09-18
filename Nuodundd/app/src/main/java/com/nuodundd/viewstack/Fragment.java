package com.nuodundd.viewstack;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

/**
 * Created by nuodundd on 17/1/17.
 */

public class Fragment implements IFragment {
    public static final String TAG = Fragment.class.getSimpleName();
    private Activity activity;
    private boolean isActive;
    private boolean isDestroy;
    private IFragmentManager manager;
    private Intent intent = new Intent();
    private View contentView;
    private int requestCode;
    private boolean finished;

    protected void onAttach(Activity activity) {
        this.activity = activity;
        Log.i(TAG, getClass().getSimpleName() + " onAttach");
    }

    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, getClass().getSimpleName() + " onCreate");
    }

    protected View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        isActive = true;
        Log.i(TAG, getClass().getSimpleName() + " onCreateView");
        return null;
    }

    protected void onViewCreated() {
        Log.i(TAG, getClass().getSimpleName() + " onViewCreated");
    }

    protected void onBecomeVisible() {
        Log.i(TAG, getClass().getSimpleName() + " onBecomeVisible");
    }

    protected void onResume() {
        isActive = true;
        Log.i(TAG, getClass().getSimpleName() + " onResume");
    }

    protected void onPause() {
        isActive = false;
        Log.i(TAG, getClass().getSimpleName() + " onPause");
    }

    protected void onStop() {
        isActive = false;
        Log.i(TAG, getClass().getSimpleName() + " onStop");
    }

    protected void onDestroyView() {
        Log.i(TAG, getClass().getSimpleName() + " onDestroyView");
        contentView = null;
    }

    protected void onDestroy() {
        isDestroy = true;
        Log.i(TAG, getClass().getSimpleName() + " onDestroy");
    }

    protected void onDetach() {
        manager = null;
        Log.i(TAG, getClass().getSimpleName() + " onDetach");
    }

    @Override
    public void finish() {
        if (null != manager) {
            manager.finish(this);
            finished = true;
        }
    }

    @Override
    public void startNewFragment(Class<? extends Fragment> frament) {
        startNewFragment(null, frament);
    }

    @Override
    public void startNewFragment(Intent intent) {
        try {
            startNewFragment(intent, (Class<? extends Fragment>) Class.forName(intent.getComponent().getClassName()));
        } catch (Exception e) {
            Log.i(TAG, e.toString());
        }
    }

    @Override
    public void startNewFragment(Intent intent, Class<? extends Fragment> frament) {
        startNewFragment(intent, frament, false);
    }

    @Override
    public void startNewFragment(Class<? extends Fragment> frament, boolean pop) {
        startNewFragment(null, frament, pop);
    }

    @Override
    public void startNewFragment(Intent intent, Class<? extends Fragment> frament, boolean pop) {
        if (manager != null) {
            manager.startNewFragment(intent, frament, pop);
        }
    }

    @Override
    public Intent getIntent() {
        return intent;
    }

    @Override
    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    @Override
    public void onNewIntent(Intent intent) {

    }

    @Override
    public boolean isDestroy() {
        return isDestroy;
    }

    @Override
    public Context getContext() {
        return activity;
    }

    @Override
    public Activity getActivity() {
        return activity;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void bindManager(IFragmentManager manager) {
        this.manager = manager;
    }

    @Override
    public void setView(View contentView) {
        this.contentView = contentView;
    }

    @Override
    public View getView() {
        return contentView;
    }

    @Override
    public void onLowMemory() {
        contentView = null;
    }

    @Override
    public void startFragmentForResult(Intent intent, Class<? extends Fragment> fragment, int requestCode) {
        if (manager != null) {
            manager.startFragmentForResult(intent, fragment, requestCode);
        }
    }

    protected void onFragmentResult(int requestCode, int resultCode, Intent data) {

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onRequest(int requestCode) {
        this.requestCode = requestCode;
    }

    @Override
    public int requestCode() {
        return requestCode;
    }

    @Override
    public Window getWindow() {
        return getActivity() == null ? null : getActivity().getWindow();
    }

    @Override
    public View findViewById(int id) {
        return contentView.findViewById(id);
    }

    @Override
    public void setContentView(int id) {
        contentView = LayoutInflater.from(getContext()).inflate(id, null);
    }

    @Override
    public void setContentView(View view) {
        contentView = view;
    }

    @Override
    public void startActivity(Intent intent) {
        getContext().startActivity(intent);
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        if (getActivity() != null) {
            try {
                return getActivity().bindService(service, conn, flags);
            } catch (Exception e) {
                Log.i(TAG, e.toString());
                return false;
            }
        }
        return true;
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        try {
            if (getActivity() != null)
                getActivity().unbindService(conn);
        } catch (Exception e) {
//            Log.i(TAG,e);
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        getActivity().startActivityForResult(intent, requestCode);
    }

    @Override
    public void startService(Intent intent) {
        getActivity().startService(intent);
    }

    @Override
    public String getString(int id) {
        return getContext().getString(id);
    }

    public String getString(int id, Object... formatArgs) {
        return getContext().getString(id, formatArgs);
    }

    public void onMove() {

    }

    public IFragmentManager getManager() {
        return manager;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean onKeyBack() {
        return false;
    }
}
