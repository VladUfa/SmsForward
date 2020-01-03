package com.pierreduchemin.smsforward.redirects

import android.Manifest
import android.app.Activity
import com.google.i18n.phonenumbers.NumberParseException
import com.pierreduchemin.smsforward.R
import com.pierreduchemin.smsforward.data.ForwardModel
import com.pierreduchemin.smsforward.data.ForwardModelRepository
import com.pierreduchemin.smsforward.utils.PhoneNumberUtils


class RedirectsPresenter(
    private val activity: Activity,
    private val forwardModelRepository: ForwardModelRepository,
    private val view: RedirectsContract.View
) : RedirectsContract.Presenter {

    companion object {
        private val TAG by lazy { RedirectsPresenter::class.java.simpleName }
    }

    init {
        view.presenter = this
    }

    override fun onViewCreated() {
        val forwardModel = forwardModelRepository.getForwardModel()
        if (forwardModel != null) {
            view.setSource(forwardModel.vfrom)
            view.setDestination(forwardModel.vto)
            onNumberPicked()
        }
    }

    override fun onSourceRetreived(source: String?) {
        if (source == null) {
            view.showError(R.string.redirects_error_invalid_source)
            return
        }

        val uSource = PhoneNumberUtils.toUnifiedNumber(activity, source)
        if (uSource == null) {
            view.showError(R.string.redirects_error_invalid_source)
            return
        }

        val vSource = PhoneNumberUtils.toVisualNumber(activity, source)
        if (vSource == null) {
            view.showError(R.string.redirects_error_invalid_source)
            return
        }

        var forwardModel = forwardModelRepository.getForwardModel()
        forwardModel = if (forwardModel == null) {
            ForwardModel(0, uSource, "", vSource, "", false)
        } else {
            forwardModel.from = uSource
            forwardModel.vfrom = vSource
            forwardModel
        }
        forwardModelRepository.insertForwardModel(forwardModel)

        view.setSource(vSource)
    }

    override fun onDestinationRetreived(destination: String?) {
        if (destination == null) {
            view.showError(R.string.redirects_error_invalid_destination)
            return
        }

        val uDestination = PhoneNumberUtils.toUnifiedNumber(activity, destination)
        if (uDestination == null) {
            view.showError(R.string.redirects_error_invalid_destination)
            return
        }

        val vDestination = PhoneNumberUtils.toVisualNumber(activity, destination)
        if (vDestination == null) {
            view.showError(R.string.redirects_error_invalid_destination)
            return
        }

        var forwardModel = forwardModelRepository.getForwardModel()
        forwardModel = if (forwardModel == null) {
            ForwardModel(0, "", uDestination, "", vDestination, false)
        } else {
            forwardModel.to = uDestination
            forwardModel.vto = vDestination
            forwardModel
        }
        forwardModelRepository.insertForwardModel(forwardModel)

        view.setDestination(vDestination)
    }

    override fun onButtonClicked(source: String, destination: String) {
        var forwardModel = forwardModelRepository.getForwardModel()
        if (forwardModel == null) {
            forwardModel = ForwardModel(0, "", "", "", "", false)
        }

        if (forwardModel.activated) {
            view.setButtonState(RedirectsFragment.ButtonState.DISABLED)
            view.resetFields()
            forwardModelRepository.deleteForwardModelById(forwardModel.id)

            RedirectService.stopActionRedirect(activity)
        } else {
            if (source.isEmpty()) {
                view.showError(R.string.redirects_error_empty_source)
                return
            }
            if (destination.isEmpty()) {
                view.showError(R.string.redirects_error_empty_destination)
                return
            }
            if (forwardModel.from == forwardModel.to) {
                view.showError(R.string.redirects_error_source_and_redirection_must_be_different)
                return
            }

            try {
                if (!view.hasPermission(Manifest.permission.SEND_SMS)) {
                    view.askPermission(Manifest.permission.SEND_SMS)
                }

                forwardModel.activated = true
                RedirectService.startActionRedirect(activity)
                view.setButtonState(RedirectsFragment.ButtonState.STOP)

                forwardModelRepository.insertForwardModel(forwardModel)
            } catch (e: NumberParseException) {
                view.showError(R.string.redirects_error_invalid_phone_number)
            }
        }
    }

    override fun onNumberPicked() {
        val forwardModel = forwardModelRepository.getForwardModel()
        if (forwardModel == null) {
            view.setButtonState(RedirectsFragment.ButtonState.DISABLED)
            return
        }

        if (forwardModel.activated) {
            view.setButtonState(RedirectsFragment.ButtonState.STOP)
            return
        }

        val enabled = forwardModel.from.isNotEmpty() && forwardModel.to.isNotEmpty()
        if (enabled) {
            view.setButtonState(RedirectsFragment.ButtonState.ENABLED)
            return
        }

        view.setButtonState(RedirectsFragment.ButtonState.DISABLED)
    }
}