package com.abnamro.apps.referenceandroid.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.abnamro.apps.referenceandroid.R
import com.abnamro.apps.referenceandroid.model.PaymentStatus
import com.abnamro.apps.referenceandroid.model.TikkiePayment
import java.text.SimpleDateFormat
import java.util.Locale

class PaymentAdapter(
    private val payments: List<TikkiePayment>,
    private val onPaymentClick: (TikkiePayment) -> Unit
) : RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_payment_card, parent, false)
        return PaymentViewHolder(view)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        val payment = payments[position]
        holder.bind(payment)
    }

    override fun getItemCount() = payments.size

    inner class PaymentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.title)
        private val status: TextView = itemView.findViewById(R.id.status)
        private val date: TextView = itemView.findViewById(R.id.date)
        private val amount: TextView = itemView.findViewById(R.id.amount)

        fun bind(payment: TikkiePayment) {
            title.text = payment.title
            amount.text = "${payment.currency} ${String.format("%.2f", payment.amount)}"
            date.text = dateFormat.format(payment.date)

            when (payment.status) {
                PaymentStatus.PAID_EXPIRED -> {
                    status.text = itemView.context.getString(R.string.paid_expired, 1)
                    status.visibility = View.VISIBLE
                }
                else -> {
                    status.visibility = View.GONE
                }
            }

            itemView.setOnClickListener {
                onPaymentClick(payment)
            }
        }
    }
}
