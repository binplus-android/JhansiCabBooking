package com.cabbooking.interfaces;

public interface WalletCallBack {

        void onWalletAmountReceived(int walletAmount);
        void onError(String error); // Optional for error handling

}
