package com.quizzers

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.quizzers.viewModels.QuizViewModel

class CategoryDialogFragment : DialogFragment() {
    private lateinit var viewModel: QuizViewModel
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage("R.string.dialog_start_game")
                .setPositiveButton("R.string.start",
                    DialogInterface.OnClickListener { dialog, id ->
                        // START THE GAME!
                        viewModel.quizOptions.value?.put("category", "16")
                    })
                .setNegativeButton("R.string.cancel",
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog
                    })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(QuizViewModel::class.java)
    }
}