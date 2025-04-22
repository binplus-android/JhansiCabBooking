package com.cabbooking.utils;


public interface ResponseService<T>  {

    void onResponse(T data );

  void onServerError(String errorMsg);
}
