package com.abnamro.apps.referenceandroid

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.getSystemService
import java.text.DecimalFormat

class RequestPaymentStep1Activity : AppCompatActivity() {

    private val decimalFormat = DecimalFormat("#0.00")
    private val maxAmount = 999.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_payment_step1)

        val backButton: ImageButton = findViewById(R.id.backButton)
        val infoButton: ImageButton = findViewById(R.id.infoButton)
        val amountInput: EditText = findViewById(R.id.amountInput)
        val maxAmountHint: TextView = findViewById(R.id.maxAmountHint)
        val payerChooseAmountToggle: SwitchCompat = findViewById(R.id.payerChooseAmountToggle)
        val nextButton: ImageButton = findViewById(R.id.nextButton)

        backButton.setOnClickListener {
            finish()
        }

        infoButton.setOnClickListener {
            // Show info dialog if needed
        }

        maxAmountHint.text = getString(R.string.max_amount_hint, "€999")

        setupAmountInput(amountInput)

        nextButton.setOnClickListener {
            // Format amount before proceeding
            formatAmountDisplay(amountInput)
            val amount = getAmountFromInput(amountInput)
            if (amount > 0 && amount <= maxAmount) {
                val intent = Intent(this, RequestPaymentStep2Activity::class.java)
                intent.putExtra("AMOUNT", amount)
                intent.putExtra("PAYER_CHOOSE_AMOUNT", payerChooseAmountToggle.isChecked)
                startActivity(intent)
            }
        }
    }

    private fun setupAmountInput(amountInput: EditText) {
        // Set initial text and cursor position
        amountInput.setText(".00")
        amountInput.setSelection(0) // Cursor before the "."
        
        // Show keyboard when activity starts - use multiple approaches for reliability
        amountInput.post {
            amountInput.requestFocus()
            amountInput.setSelection(0) // Ensure cursor is at position 0
            // Delay slightly to ensure view is fully laid out
            amountInput.postDelayed({
                val imm = getSystemService<InputMethodManager>()
                imm?.showSoftInput(amountInput, InputMethodManager.SHOW_IMPLICIT)
            }, 100)
        }
        
        // Handle text changes - format decimal input
        amountInput.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUpdating) return
                
                val text = s?.toString() ?: ""
                
                // If text is empty or just ".", keep ".00" as default
                if (text.isEmpty()) {
                    isUpdating = true
                    amountInput.setText(".00")
                    amountInput.setSelection(0)
                    isUpdating = false
                    return
                }
                
                // Allow only numbers and one decimal point
                val filtered = text.filterIndexed { index, char ->
                    when {
                        char.isDigit() -> true
                        char == '.' && text.indexOf('.') == index -> true
                        else -> false
                    }
                }
                
                // Remove leading zeros (except for 0.5, 0.05, etc.)
                val cleaned = if (filtered.startsWith("0") && filtered.length > 1 && !filtered.startsWith("0.")) {
                    filtered.trimStart('0').ifEmpty { "0" }
                } else {
                    filtered
                }
                
                // Parse the amount
                val amount = cleaned.toDoubleOrNull() ?: 0.0
                
                // Limit to max amount
                val finalAmount = if (amount > maxAmount) maxAmount else amount
                
                // Determine display text
                val displayText = when {
                    cleaned.isEmpty() -> ".00"
                    cleaned == "." -> "."
                    finalAmount > maxAmount -> maxAmount.toString()
                    else -> cleaned
                }
                
                if (filtered != text || cleaned != filtered || (finalAmount > maxAmount && cleaned != maxAmount.toString())) {
                    isUpdating = true
                    val cursorPosition = amountInput.selectionStart
                    amountInput.setText(displayText)
                    // Restore cursor position, adjusting for removed characters
                    val positionDiff = text.length - displayText.length
                    val newPosition = (cursorPosition - positionDiff).coerceIn(0, displayText.length)
                    amountInput.setSelection(newPosition)
                    isUpdating = false
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Handle focus changes to maintain cursor position
        amountInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // When focused, ensure cursor is at position 0 (before the ".")
                amountInput.post {
                    if (amountInput.text.toString() == ".00") {
                        amountInput.setSelection(0)
                    }
                }
            }
        }

        // Handle IME action (Done button on keyboard)
        amountInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val nextButton: ImageButton = findViewById(R.id.nextButton)
                nextButton.performClick()
                true
            } else {
                false
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            // Show keyboard when window gains focus
            val amountInput: EditText = findViewById(R.id.amountInput)
            amountInput.postDelayed({
                amountInput.requestFocus()
                val imm = getSystemService<InputMethodManager>()
                imm?.showSoftInput(amountInput, InputMethodManager.SHOW_IMPLICIT)
            }, 100)
        }
    }

    private fun formatAmountDisplay(amountInput: EditText) {
        val text = amountInput.text.toString().trim()
        if (text.isEmpty() || text == ".") {
            return
        }
        
        val amount = text.toDoubleOrNull() ?: 0.0
        if (amount > 0) {
            // Format to 2 decimal places (e.g., 23 -> 23.00, 0.5 -> 0.50)
            val formatted = decimalFormat.format(amount)
            if (formatted != text) {
                amountInput.setText(formatted)
            }
        }
    }

    private fun getAmountFromInput(amountInput: EditText): Double {
        val text = amountInput.text.toString()
        if (text.isEmpty() || text == ".") return 0.0
        return text.toDoubleOrNull() ?: 0.0
    }
}
