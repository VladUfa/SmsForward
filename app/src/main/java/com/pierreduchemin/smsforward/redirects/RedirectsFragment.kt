/*
 * Copyright 2017, The Android Open Source Project
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
 * limitations under the License.
 */
package com.pierreduchemin.smsforward.redirects

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.pierreduchemin.smsforward.R

const val PERMISSIONS_REQUEST_SEND_SMS = 1654

class RedirectsFragment : Fragment(), RedirectsContract.View {

    override lateinit var presenter: RedirectsContract.Presenter

    companion object {
        fun newInstance() = RedirectsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.redirects_fragment, container, false)

        val btnEnable = root.findViewById<Button>(R.id.btnEnable)
        val etSource = root.findViewById<EditText>(R.id.etSource)
        val etDestination = root.findViewById<EditText>(R.id.etDestination)
        btnEnable.setOnClickListener {
            presenter.setRedirect(etSource.text.trim().toString(), etDestination.text.trim().toString())
        }

        return root
    }

    override fun hasPermission(permissionString: String): Boolean {
        return ContextCompat.checkSelfPermission(
            requireActivity(),
            permissionString
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun askPermission(permissionString: String) {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(permissionString), PERMISSIONS_REQUEST_SEND_SMS)
    }

    override fun redirectSetConfirmation(source: String, destination: String) {
        Toast.makeText(requireContext(), requireContext().getString(R.string.redirects_info_forwarding_from_to, source, destination), Toast.LENGTH_LONG).show()
    }

    override fun showError(message: Int) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
}
