package com.example.searchphone.mvp;

public interface MvpMainView extends MvpLoadingView{
    void showToast(String msg);
    void updateView();
}
