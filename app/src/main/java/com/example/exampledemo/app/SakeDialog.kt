package com.example.exampledemo.app

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.exampledemo.R
import com.example.exampledemo.common.Constants
import com.example.exampledemo.databinding.DialogAppBinding


class SakeDialog : DialogFragment() {
    private var titleRes: Int? = null
    private var title: String? = null
    private var messageRes: Int? = null
    private var message: String? = null

    private var leftButtonTextRes: Int? = null
    private var leftButtonText: String? = null
    private var leftButtonListener: OnClickListener? = null
    private var leftButtonColor: Int? = null

    private var rightButtonTextRes: Int? = null
    private var rightButtonText: String? = null
    private var rightButtonListener: OnClickListener? = null
    private var rightButtonColor: Int? = null


    private lateinit var binding: DialogAppBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.SakeTheme_Dialog)
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_app, null, false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (this.title.isNullOrBlank()) {
            binding.title.setText(this.titleRes ?: R.string.tit_notice)
        } else {
            binding.title.text = this.title
        }

        if (this.message.isNullOrBlank()) {
            binding.message.setText(this.messageRes ?: R.string.msg_network_error)
        } else {
            binding.message.text = this.message
        }

        // LEFT BUTTON
        if (leftButtonText == null && leftButtonTextRes == null) {
            binding.leftButton.visibility = View.GONE
        } else {
            if (leftButtonText != null) {
                binding.leftButton.text = leftButtonText
            } else {
                binding.leftButton.setText(leftButtonTextRes!!)
            }
            binding.leftButton.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    leftButtonColor ?: R.color.red
                )
            )
            binding.leftButton.setOnClickListener {
                dismiss()
                leftButtonListener?.onClick()
            }
        }

        // RIGHT BUTTON
        if (rightButtonText == null && rightButtonTextRes == null) {
            binding.rightButton.visibility = View.GONE
        } else {
            if (rightButtonText != null) {
                binding.rightButton.text = rightButtonText
            } else {
                binding.rightButton.setText(rightButtonTextRes!!)
            }
            binding.rightButton.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    rightButtonColor ?: R.color.text
                )
            )
            binding.rightButton.setOnClickListener {
                dismiss()
                rightButtonListener?.onClick()
            }
        }
    }

    fun setTitle(res: Int?): SakeDialog {
        this.titleRes = res
        return this
    }

    fun setTitleText(title: String?): SakeDialog {
        this.title = title
        return this
    }

    fun setMessage(res: Int?): SakeDialog {
        this.messageRes = res
        return this
    }

    fun setMessageText(msg: String): SakeDialog {
        this.message = msg
        return this
    }

    fun setLeftTextButton(
        text: String?,
        listener: OnClickListener? = null,
        color: Int? = null
    ): SakeDialog {
        this.leftButtonText = text
        this.leftButtonListener = listener
        this.leftButtonColor = color
        return this

    }

    fun setLeftButton(
        res: Int?,
        listener: OnClickListener? = null,
        color: Int? = null
    ): SakeDialog {
        this.leftButtonTextRes = res ?: R.string.act_cancel
        this.leftButtonListener = listener
        this.leftButtonColor = color
        return this

    }

    fun setRightButton(
        res: Int?,
        listener: OnClickListener? = null,
        color: Int? = null
    ): SakeDialog {
        this.rightButtonTextRes = res ?: R.string.act_ok
        this.rightButtonListener = listener
        this.rightButtonColor = color
        return this
    }

    fun setRightTextButton(
        text: String?,
        listener: OnClickListener? = null,
        color: Int? = null
    ): SakeDialog {
        this.rightButtonText = text
        this.rightButtonListener = listener
        this.rightButtonColor = color
        return this
    }

    fun show(fm: FragmentManager) {
        show(fm, Constants.TAG_DIALOG_FRAGMENT)
    }

    fun show(ft: FragmentTransaction) {
        show(ft, Constants.TAG_DIALOG_FRAGMENT)
    }

    interface OnClickListener {
        fun onClick()
    }
}
