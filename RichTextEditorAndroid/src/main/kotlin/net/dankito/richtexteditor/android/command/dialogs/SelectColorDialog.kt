package net.dankito.richtexteditor.android.command.dialogs

import android.app.Activity
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import net.dankito.utils.Color
import net.dankito.richtexteditor.android.RichTextEditor
import net.dankito.utils.android.keyboard.KeyboardState
import net.dankito.utils.android.extensions.asActivity
import net.dankito.utils.android.extensions.hideKeyboard


class SelectColorDialog {

    private val keyboardState = KeyboardState()


    fun select(currentColor: Color, editor: RichTextEditor, colorSelected: (Color) -> Unit) {
        val currentColorWithoutAlpha = Color(currentColor.red, currentColor.green, currentColor.blue) // remove alpha value as html doesn't support alpha

        val wasKeyboardVisible = getIsKeyboardVisibleAndCloseIfSo(editor)

        val colorPickerDialog = ColorPickerDialog.newBuilder()
                .setColor(currentColorWithoutAlpha.toInt())
                .setShowAlphaSlider(false)
//                .setDialogType(0) // 0 = PickerView, 1 = PresetsView, default is 1
                .create()

        colorPickerDialog.setColorPickerDialogListener(object : ColorPickerDialogListener {

            override fun onColorSelected(dialogId: Int, color: Int) {
                cleanUpDialog()

                val convertedColor = Color(android.graphics.Color.red(color), android.graphics.Color.green(color), android.graphics.Color.blue(color), android.graphics.Color.alpha(color))
                colorSelected(convertedColor)
            }

            override fun onDialogDismissed(dialogId: Int) {
                cleanUpDialog()

                if(wasKeyboardVisible) {
                    editor.focusEditorAndShowKeyboardDelayed(alsoCallJavaScriptFocusFunction = false) // don't call JavaScript focus() function has this would change state and just selected color would therefore get lost
                }
            }

        })

        editor.context.asActivity()?.let { activity ->
            keyboardState.init(activity)

            colorPickerDialog.show(activity.fragmentManager, "")
        }
    }

    private fun getIsKeyboardVisibleAndCloseIfSo(editor: RichTextEditor): Boolean {
        val isVisible = keyboardState.isKeyboardVisible

        if(isVisible) {
            editor.hideKeyboard()
        }

        return isVisible
    }

    private fun cleanUpDialog() {
        keyboardState.cleanUp() // to avoid memory leaks
    }

}