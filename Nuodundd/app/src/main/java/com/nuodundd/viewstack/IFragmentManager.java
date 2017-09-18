package com.nuodundd.viewstack;

import android.content.Intent;
import android.view.View;

/**
 * Created by nuodundd on 17/1/17.
 */

public interface IFragmentManager {
    /**
     * @param frament
     * @param pop     true pop to the frament in the stack if exist; false start anew directly
     */
    void startNewFragment(Class<? extends Fragment> frament, boolean pop);

    void startNewFragment(Class<? extends Fragment> frament);

    void startNewFragment(Intent intent, Class<? extends Fragment> frament);

    /**
     * @param intent
     * @param frament
     * @param pop     true pop to the frament in the stack if exist; false start anew directly
     */
    void startNewFragment(Intent intent, Class<? extends Fragment> frament, boolean pop);

    void startNewFragment(Intent intent, Class<? extends Fragment> frament, boolean pop, boolean anim);

    void startNewFragment(Intent intent, Class<? extends Fragment> frament, boolean pop, boolean anim, boolean addToStack);

    void startNewFragment(Intent intent, Class<? extends Fragment> frament, boolean pop, boolean anim, boolean addToStack, int requestCode);

    void finish();

    void finish(Fragment fragment, boolean anim);

    void finish(Fragment fragment);

    void popTo(Intent intent, Fragment fragment, boolean anim);

    void popTo(Intent intent, Class<? extends Fragment> fragment, boolean anim);

    View getView();

    void onLowMemory();

    void startFragmentForResult(Intent intent, Class<? extends Fragment> fragment, int requestCode);

    void setResult(Intent intent, int requestCode, int resultCode);

    void onActivityResult(int requestCode, int resultCode, Intent data);

    Fragment getNowFragment();

    boolean onBackKey();

    void onActivityStop();

    void onActivityPause();

    void onActivityResume();

    void onActivityDestroy();

    boolean isTaskRoot();

}
