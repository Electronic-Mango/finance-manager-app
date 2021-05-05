package com.example.prmproject1

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.prmproject1.Common.CATEGORY_DATA_RESULT
import com.example.prmproject1.Common.DATE_DATA_RESULT
import com.example.prmproject1.Common.DESCRIPTION_DATA_RESULT
import com.example.prmproject1.Common.VALUE_DATA_RESULT
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_add_transaction.*
import kotlinx.android.synthetic.main.content_add_transaction.*

/**
 * [AppCompatActivity] responsible for creating new [Transaction].
 */
class AddTransactionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)
        setSupportActionBar(toolbarActivityAddTransaction)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        fabActivityAddTransaction.setOnClickListener {
            val value: Float = addTransactionFragmentValue.text.toString().toFloat()
            val date: String = addTransactionFragmentDate.text.toString()
            val category: String = addTransactionFragmentCategory.text.toString()
            val description: String = addTransactionFragmentDescription.text.toString()

            val data = Intent()
            data.putExtra(VALUE_DATA_RESULT, value)
            data.putExtra(DATE_DATA_RESULT, date)
            data.putExtra(CATEGORY_DATA_RESULT, category)
            data.putExtra(DESCRIPTION_DATA_RESULT, description)
            setResult(RESULT_OK, data)
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                setResult(RESULT_CANCELED)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
