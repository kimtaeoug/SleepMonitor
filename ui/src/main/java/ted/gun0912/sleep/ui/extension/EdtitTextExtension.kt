package ted.gun0912.sleep.ui.extension

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText


fun EditText.showKeyboard() {
    requestFocus()
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
        ?.showSoftInput(this, 0)
}
