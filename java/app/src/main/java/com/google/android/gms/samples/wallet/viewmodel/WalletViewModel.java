/*
 * Copyright 2023 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.samples.wallet.viewmodel;

import android.app.Activity;
import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.android.gms.pay.Pay;
import com.google.android.gms.pay.PayApiAvailabilityStatus;
import com.google.android.gms.pay.PayClient;

public class WalletViewModel extends AndroidViewModel {

    // A client to interact with the Google Wallet API
    private final PayClient walletClient;

    // LiveData with the result of whether the user can save passes with Google Wallet
    private final MutableLiveData<Boolean> _canAddPasses = new MutableLiveData<>();
    public final LiveData<Boolean> canAddPasses = _canAddPasses;

    public WalletViewModel(Application application) {
        super(application);
        walletClient = Pay.getClient(application);

        fetchCanAddPassesToGoogleWallet();
    }

    /**
     * Determine whether the API to save passes to Google Pay is available on the device.
     */
    private void fetchCanAddPassesToGoogleWallet() {
        walletClient
            .getPayApiAvailabilityStatus(PayClient.RequestType.SAVE_PASSES)
            .addOnSuccessListener(
                status -> _canAddPasses.setValue(status == PayApiAvailabilityStatus.AVAILABLE))
            // If the API is not available, we recommend to either:
            // 1) Hide the save button
            // 2) Fall back to a different Save Passes integration (e.g. JWT link)
            // Note that a user might become eligible in the future.

            // Google Play Services is too old. API availability can't be verified.
            .addOnFailureListener(exception -> _canAddPasses.setValue(false));
    }

    /**
     * Exposes the `savePassesJwt` method in the wallet client
     */
    public void savePassesJwt(String jwtString, Activity activity, int requestCode) {
        walletClient.savePassesJwt(jwtString, activity, requestCode);
    }

    /**
     * Exposes the `savePasses` method in the wallet client
     */
    public void savePasses(String objectString, Activity activity, int requestCode) {
        walletClient.savePasses(objectString, activity, requestCode);
    }

    // Test generic object used to be created against the API
    // See https://developers.google.com/wallet/tickets/boarding-passes/web#json_web_token_jwt for more details
    public final String genericObjectJwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiJnb29nbGUiLCJwYXlsb2FkIjp7ImdlbmVyaWNPYmplY3RzIjpbeyJpZCI6IjMzODgwMDAwMDAwMjIwOTUxNzcuZjUyZDRhZjYtMjQxMS00ZDU5LWFlNDktNzg2ZDY3N2FkOTJiIn1dfSwiaXNzIjoid2FsbGV0LWxhYi10b29sc0BhcHBzcG90LmdzZXJ2aWNlYWNjb3VudC5jb20iLCJ0eXAiOiJzYXZldG93YWxsZXQiLCJpYXQiOjE2NTA1MzI2MjN9.ZURFHaSiVe3DfgXghYKBrkPhnQy21wMR9vNp84azBSjJxENxbRBjqh3F1D9agKLOhrrflNtIicShLkH4LrFOYdnP6bvHm6IMFjqpUur0JK17ZQ3KUwQpejCgzuH4u7VJOP_LcBEnRtzZm0PyIvL3j5-eMRyRAo5Z3thGOsKjqCPotCAk4Z622XHPq5iMNVTvcQJaBVhmpmjRLGJs7qRp87sLIpYOYOkK8BD7OxLmBw9geqDJX-Y1zwxmQbzNjd9z2fuwXX66zMm7pn6GAEBmJiqollFBussu-QFEopml51_5nf4JQgSdXmlfPrVrwa6zjksctIXmJSiVpxL7awKN2w";
}