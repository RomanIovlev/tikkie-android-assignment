package com.abnamro.apps.referenceandroid

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import com.abnamro.apps.referenceandroid.model.PaymentStatus
import com.abnamro.apps.referenceandroid.model.TikkiePayment
import java.util.Date

class RequestPaymentStep2Activity : AppCompatActivity() {

    private var amount: Double = 0.0
    private var payerChooseAmount: Boolean = false
    private lateinit var descriptionInput: EditText
    private lateinit var characterCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_payment_step2)

        amount = intent.getDoubleExtra("AMOUNT", 0.0)
        payerChooseAmount = intent.getBooleanExtra("PAYER_CHOOSE_AMOUNT", false)

        val backButton: ImageButton = findViewById(R.id.backButton)
        val infoButton: ImageButton = findViewById(R.id.infoButton)
        descriptionInput = findViewById(R.id.descriptionInput)
        characterCount = findViewById(R.id.characterCount)
        val whatsappButton: Button = findViewById(R.id.whatsappButton)
        val qrCodeButton: ImageButton = findViewById(R.id.qrCodeButton)
        val shareButton: ImageButton = findViewById(R.id.shareButton)

        backButton.setOnClickListener {
            finish()
        }

        infoButton.setOnClickListener {
            // Show info dialog if needed
        }

        setupDescriptionInput(descriptionInput)

        whatsappButton.setOnClickListener {
            sharePayment(descriptionInput.text.toString())
        }

        qrCodeButton.setOnClickListener {
            // Handle QR code action
            sharePayment(descriptionInput.text.toString())
        }

        shareButton.setOnClickListener {
            sharePayment(descriptionInput.text.toString())
        }
    }

    private fun sharePayment(description: String) {
        if (description.isBlank()) {
            descriptionInput.error = "Please enter a description"
            return
        }

        val now = Date()
        // Generate fake Tikkie ID (10 digits)
        val fakeTikkieId = (1000000000L..9999999999L).random().toString()
        
        // Generate fake execution date (1 day after creation)
        val calendar = java.util.Calendar.getInstance()
        calendar.time = now
        calendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 9)
        calendar.set(java.util.Calendar.MINUTE, 58)
        val fakeExecutionDate = calendar.time
        
        val newPayment = TikkiePayment(
            id = System.currentTimeMillis().toString(),
            title = description,
            amount = amount,
            date = now,
            status = PaymentStatus.UNPAID,
            tikkieId = fakeTikkieId,
            executionDate = fakeExecutionDate,
            createdAt = now
        )

        TikkieRepository.addPayment(newPayment)

        // Navigate to payment details and clear back stack
        val intent = Intent(this, PaymentDetailsActivity::class.java).apply {
            putExtra("PAYMENT_ID", newPayment.id)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finishAffinity()
    }

    private fun setupDescriptionInput(descriptionInput: EditText) {
        // Set cursor to start position
        descriptionInput.setSelection(0)
        
        // Show keyboard when activity starts - use delay for reliability
        descriptionInput.post {
            descriptionInput.requestFocus()
            descriptionInput.setSelection(0)
            descriptionInput.postDelayed({
                val imm = getSystemService<InputMethodManager>()
                imm?.showSoftInput(descriptionInput, InputMethodManager.SHOW_IMPLICIT)
            }, 100)
        }
        
        // Update character count
        descriptionInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            
            override fun afterTextChanged(s: Editable?) {
                val length = s?.length ?: 0
                if (length > 0) {
                    characterCount.text = length.toString()
                    characterCount.visibility = TextView.VISIBLE
                } else {
                    characterCount.visibility = TextView.GONE
                }
            }
        })
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            // Show keyboard when window gains focus
            descriptionInput.postDelayed({
                descriptionInput.requestFocus()
                val imm = getSystemService<InputMethodManager>()
                imm?.showSoftInput(descriptionInput, InputMethodManager.SHOW_IMPLICIT)
            }, 100)
        }
    }
}
