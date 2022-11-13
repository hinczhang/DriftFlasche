package org.standardserve.driftflasche.dialog

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.standardserve.driftflasche.R

object MarkerCreationDialog {
    fun create(context: Context) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.set_bottle, null)
        val dialog = MaterialAlertDialogBuilder(context)
            .setView(view)
            .create()
        dialog.show()

    }
}
