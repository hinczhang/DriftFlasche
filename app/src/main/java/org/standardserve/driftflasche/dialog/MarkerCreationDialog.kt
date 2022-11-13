package org.standardserve.driftflasche.dialog

import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.view.LayoutInflater
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.standardserve.driftflasche.R

object MarkerCreationDialog {
    fun create(context: Context) {

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.set_bottle, null)
        val dialog = MaterialAlertDialogBuilder(context)
            .setTitle(R.string.bottle_creation_dialog_title)
            .setNeutralButton("Confirm") { dialog, which ->
                // Respond to neutral button press
            }
            .setNegativeButton("Decline") { dialog, which ->
                // Respond to negative button press
            }
            .setView(view)
            .create()
        dialog.show()

    }
}
