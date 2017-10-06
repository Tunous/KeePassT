/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package me.thanel.keepasst.unlock

import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_fingerprint.view.*
import me.thanel.keepasst.R
import javax.crypto.Cipher

/**
 * A dialog which uses fingerprint APIs to authenticate the user.
 */
@RequiresApi(api = Build.VERSION_CODES.M)
class FingerprintAuthenticationDialogFragment : DialogFragment(), FingerprintUiHelper.Callback {
    private var cryptoObject: FingerprintManager.CryptoObject? = null
    private lateinit var fingerprintUiHelper: FingerprintUiHelper
    private lateinit var listener: FingerprintUiHelper.Callback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Do not create a new Fragment when the Activity is re-created such as orientation changes.
        retainInstance = true
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        dialog.setTitle(getString(R.string.sign_in))
        val view = inflater.inflate(R.layout.dialog_fingerprint, container, false)
        view.cancel_button.setOnClickListener { dismiss() }

        val fingerprintManager = context.getSystemService(FingerprintManager::class.java)
        fingerprintUiHelper = FingerprintUiHelper(fingerprintManager,
                view.fingerprint_icon,
                view.fingerprint_status,
                this)

        // If fingerprint authentication is not available, switch immediately to the backup
        // (password) screen.
        if (!fingerprintUiHelper.isFingerprintAuthAvailable) {
            onError()
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        fingerprintUiHelper.startListening(cryptoObject!!)
    }

    override fun onPause() {
        super.onPause()
        fingerprintUiHelper.stopListening()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as FingerprintUiHelper.Callback
    }

    /**
     * Sets the crypto object to be passed in when authenticating with fingerprint.
     */
    fun setCryptoObject(cryptoObject: FingerprintManager.CryptoObject) {
        this.cryptoObject = cryptoObject
    }

    override fun onAuthenticated(cipher: Cipher) {
        listener.onAuthenticated(cipher)
        dismiss()
    }

    override fun onError() {
        listener.onError()
    }
}
