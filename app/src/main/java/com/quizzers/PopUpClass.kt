package com.quizzers

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow


class PopUpClass {
    private val TAG = "PopUpClass"
    //PopupWindow display method
    fun showPopupWindow(view: View) {

        Log.d(TAG, "showPopupWindow: start")
        //Create a View object yourself through inflater
        val inflater =
            view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.popup_layout, null)

        //Specify the length and width through constants
        val width = LinearLayout.LayoutParams.MATCH_PARENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT

        //Make Inactive Items Outside Of PopupWindow
        val focusable = true

        //Create a window with our parameters
        val popupWindow = PopupWindow(popupView, width, height, focusable)

        //Set the location of the window on the screen
        popupWindow.animationStyle = R.style.popup_window_animation
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

        //Initialize the elements of our window, install the handler
        val buttonEdit = popupView.findViewById<ImageButton>(R.id.cancel_button)
        buttonEdit.setOnClickListener { //As an example, display the message
//            Toast.makeText(view.context, "Wow, popup action button", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "showPopupWindow: dismiss")
            popupWindow.dismiss()
        }
    }
}