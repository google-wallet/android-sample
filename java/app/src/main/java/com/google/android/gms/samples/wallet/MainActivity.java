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

package com.google.android.gms.samples.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.pay.PayClient;
import com.google.android.gms.samples.wallet.databinding.ActivitySavePassBinding;
import com.google.android.gms.samples.wallet.viewmodel.WalletViewModel;

public class MainActivity extends ComponentActivity {

    private WalletViewModel model;

    private static final int ADD_TO_GOOGLE_WALLET_REQUEST_CODE = 999;

    private View addToGoogleWalletButtonContainer;
    private View addToGoogleWalletButton;

  /**
   * Initialize the Google Pay API on creation of the activity
   *
   * @see android.app.Activity#onCreate(android.os.Bundle)
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initializeUi();

    // Check Google Pay availability
    model = new ViewModelProvider(this).get(WalletViewModel.class);

    // Check out Google Wallet availability
    model.canAddPasses.observe(this, this::setAddToGoogleWalletAvailable);
  }

  private void initializeUi() {

    // Use view binding to access the UI elements
    ActivitySavePassBinding layoutBinding = ActivitySavePassBinding.inflate(getLayoutInflater());
    setContentView(layoutBinding.getRoot());

    addToGoogleWalletButton = layoutBinding.addToGoogleWalletButton.getRoot();
    addToGoogleWalletButtonContainer = layoutBinding.passContainer;
    addToGoogleWalletButton.setOnClickListener(v -> {
      addToGoogleWalletButton.setClickable(false);
      model.savePassesJwt(model.genericObjectJwt, this, ADD_TO_GOOGLE_WALLET_REQUEST_CODE);
    });
  }

  /**
   * If getPayApiAvailabilityStatus returned {@code true}, show the "Add to Google Wallet" button.
   * Otherwise, notify the user that Google Wallet is not available. Please adjust to fit in with
   * your current user flow. You are not required to explicitly let the user know if isReadyToPay
   * returns {@code false}.
   *
   * @param available isReadyToPay API response.
   */
  private void setAddToGoogleWalletAvailable(boolean available) {
    if (available) {
      addToGoogleWalletButtonContainer.setVisibility(View.VISIBLE);
    } else {
      Toast.makeText(
          this,
          R.string.google_wallet_status_unavailable,
          Toast.LENGTH_LONG).show();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == ADD_TO_GOOGLE_WALLET_REQUEST_CODE) {
      switch (resultCode) {
        case RESULT_OK: {
          Toast
              .makeText(this, getString(R.string.add_google_wallet_success), Toast.LENGTH_LONG)
              .show();
          break;
        }

        case RESULT_CANCELED: {
          // TODO Handle save canceled
          break;
        }

        case PayClient.SavePassesResult.SAVE_ERROR: {
          if (data != null) {
            String apiErrorMessage = data.getStringExtra(PayClient.EXTRA_API_ERROR_MESSAGE);
            // TODO Handle API error
          }
          break;
        }

        default: // TODO Handle other outcomes
      }

      addToGoogleWalletButton.setClickable(true);
    }
  }
}