package com.cabbooking.interfaces;

import com.cabbooking.Response.Bound;

import java.util.List;

public interface BoundCallback {
    void onResult(List<Bound> boundList);
    void onError(String error);
}
