package com.charlye934.jetpackdogs.presenter.view

import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.telephony.SmsManager
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.charlye934.jetpackdogs.MainActivity
import com.charlye934.jetpackdogs.R
import com.charlye934.jetpackdogs.databinding.FragmentDetailBinding
import com.charlye934.jetpackdogs.databinding.SendSmsDialogBinding
import com.charlye934.jetpackdogs.data.model.DogBreed
import com.charlye934.jetpackdogs.data.model.DogPalette
import com.charlye934.jetpackdogs.data.model.SmsInfo
import com.charlye934.jetpackdogs.presenter.viewmodel.DetailsViewModel


class DetailFragment : Fragment() {

    private val viewModel : DetailsViewModel by activityViewModels()
    private var dogUuid = 0

    private lateinit var dataBinding: FragmentDetailBinding
    private var sendSmsStarted = false
    private var currentDog : DogBreed? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        dataBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_detail,container,false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            dogUuid = DetailFragmentArgs.fromBundle(it).dogUuid
        }

        viewModel.fetch(dogUuid)

        observerViewModel()
    }

    private fun observerViewModel() {
        viewModel.dogLiveData.observe(viewLifecycleOwner, Observer { dog ->
            currentDog = dog
            dog?.let {
                dataBinding.dog = dog

                it.imageUrl?.let {url ->
                    setupBacgroundColor(url)
                }
                }
            }
        )
    }

    private fun setupBacgroundColor(url:String){
        Glide.with(this)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>(){
                override fun onLoadCleared(placeholder: Drawable?) {}

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    Palette.from(resource)
                        .generate{ palette ->
                            val intColor = palette?.lightMutedSwatch?.rgb ?: 0
                            val myPalette = DogPalette(intColor)
                            dataBinding.palette = myPalette
                        }
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.detail_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_send_sms ->{
                sendSmsStarted = true
                (activity as MainActivity).checkSmsPermission()
            }
            R.id.action_share ->{
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "Text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, "Check out this dog bread")
                intent.putExtra(Intent.EXTRA_TEXT, "${currentDog?.dogBreed} bred for ${currentDog?.bredFor}")
                intent.putExtra(Intent.EXTRA_STREAM, currentDog?.imageUrl)
                startActivity(Intent.createChooser(intent, "Share with"))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun onPermissionResult(permisionGranted:Boolean){
        if(sendSmsStarted && permisionGranted){
            context?.let {
                val smsInfo = SmsInfo("", "${currentDog?.dogBreed} bred for ${currentDog?.bredFor}",currentDog?.imageUrl)
                val dialogBinding = DataBindingUtil.inflate<SendSmsDialogBinding>(LayoutInflater.from(it), R.layout.send_sms_dialog, null, false)

                androidx.appcompat.app.AlertDialog.Builder(it)
                    .setView(dialogBinding.root)
                    .setPositiveButton("Send sms"){ dialog, which ->
                        if(!dialogBinding.smsDestination.text.isNullOrEmpty()){
                            smsInfo.to = dialogBinding.smsDestination.text.toString()
                            sendSms(smsInfo)
                        }
                    }
                    .setNegativeButton("Cancel"){ dialog, which ->}
                    .show()

                dialogBinding.smsInfo = smsInfo
            }
        }
    }

    private fun sendSms(smsInfo: SmsInfo){
        val intet = Intent(context, MainActivity::class.java)
        val pi = PendingIntent.getActivity(context, 0, intet, 0)
        val sms = SmsManager.getDefault()

        sms.sendTextMessage(smsInfo.to, null, smsInfo.text, pi, null)
    }

}