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

package com.google.android.gms.samples.wallet

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.samples.wallet.ui.theme.GoogleWalletSampleTheme
import com.google.android.gms.samples.wallet.viewmodel.WalletUiState
import com.google.android.gms.samples.wallet.viewmodel.WalletViewModel
import com.google.wallet.button.WalletButton

class MainActivity : ComponentActivity() {

    private val addToGoogleWalletRequestCode = 1000
    private val model: WalletViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val payState: WalletUiState by model.walletUiState.collectAsStateWithLifecycle()

            GoogleWalletSampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        when (payState) {
                            is WalletUiState.PassAdded -> AddToGoogleWalletSuccess()
                            is WalletUiState.Available -> AddToGoogleWallet(::requestSavePass)
                            else -> IndeterminateCircularIndicator()
                        }
                    }
                }
            }
        }
    }

    private fun requestSavePass() {
        model.savePassesJwt(model.genericObjectJwt, this, addToGoogleWalletRequestCode)
    }

    @Deprecated("Deprecated and in use by Google Pay")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == addToGoogleWalletRequestCode) {
            when (resultCode) {
                RESULT_OK -> Toast.makeText(
                    this, getString(R.string.add_google_wallet_success), Toast.LENGTH_LONG
                ).show()

                /* Handle other result scenarios
                 * Learn more at: https://developers.google.com/wallet/generic/android#5_add_the_object_to
                 */
                else -> { // Other uncaught errors }
                }
            }
        }
    }
}

@Composable
fun IndeterminateCircularIndicator() = CircularProgressIndicator(
    color = MaterialTheme.colorScheme.surfaceVariant,
    trackColor = MaterialTheme.colorScheme.secondary,
)

@Composable
fun AddToGoogleWallet(onWalletButtonClick: () -> Unit) = Column(
    modifier = Modifier
        .padding(20.dp),
    verticalArrangement = Arrangement.spacedBy(space = 10.dp),
) {
    WalletButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = onWalletButtonClick,
    )
}

@Composable
fun AddToGoogleWalletSuccess() = Column(
    modifier = Modifier
        .padding(20.dp)
        .fillMaxWidth()
        .fillMaxHeight(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
) {
    Image(
        contentDescription = null,
        painter = painterResource(R.drawable.check_circle),
        modifier = Modifier
            .width(200.dp)
            .height(200.dp)
    )
    Text(text = stringResource(id = R.string.add_google_wallet_success))
}


@Preview(showBackground = true)
@Composable
fun WalletButtonPreview() {
    WalletButton(onClick = { /*TODO*/ })
}