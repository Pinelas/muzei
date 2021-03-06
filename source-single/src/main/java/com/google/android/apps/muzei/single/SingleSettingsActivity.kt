/*
 * Copyright 2017 Google Inc.
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

package com.google.android.apps.muzei.single

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.lifecycle.lifecycleScope
import com.google.android.apps.muzei.util.toast
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch

private class GetImage : ActivityResultContract<Unit, Uri?>() {
    private val getContent = ActivityResultContracts.GetContent()

    override fun createIntent(context: Context, input: Unit?) =
            getContent.createIntent(context, "image/*")

    override fun parseResult(resultCode: Int, intent: Intent?) =
            getContent.parseResult(resultCode, intent)
}

/**
 * Settings Activity which allows users to select a new photo
 */
class SingleSettingsActivity : ComponentActivity() {

    private val getImage = registerForActivityResult(GetImage()) { uri ->
        if (uri != null) {
            lifecycleScope.launch(NonCancellable) {
                val success = SingleArtProvider.setArtwork(
                        this@SingleSettingsActivity, uri)
                setResult(if (success) RESULT_OK else RESULT_CANCELED)
                finish()
            }
        } else {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            getImage.launch()
        } catch (e: Exception) {
            toast(R.string.single_get_content_failure)
            finish()
        }
    }
}
