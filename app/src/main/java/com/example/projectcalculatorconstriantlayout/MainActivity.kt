package com.example.projectcalculatorconstriantlayout

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import net.objecthunter.exp4j.ExpressionBuilder

lateinit var inputTV: TextView
lateinit var outputTV: TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        inputTV = findViewById(R.id.tv_input_id)
        outputTV = findViewById(R.id.tv_output_id)

        setupNumbersButton()
        setupOperatorButton()
        setupClearButton()
        setupDeleteButton()
        setupEqualButton()
        setDotButton()
        setupPercentButton()

    }

    // دالة اضافة الارقام الي الشاشة
    private fun setupNumbersButton() {
        val numbers = listOf(
            R.id.btn_num0_id to "0",
            R.id.btn_num1_id to "1",
            R.id.btn_num2_id to "2",
            R.id.btn_num3_id to "3",
            R.id.btn_num4_id to "4",
            R.id.btn_num5_id to "5",
            R.id.btn_num6_id to "6",
            R.id.btn_num7_id to "7",
            R.id.btn_num8_id to "8",
            R.id.btn_num9_id to "9",

            )
        for ((id, value) in numbers) {
            val button = findViewById<androidx.appcompat.widget.AppCompatButton>(id)
            button.setOnClickListener {
                largestInputTV()
                inputTV.append(value)
                outputTV.append(value)
                smallerOutputTV()
            }
        }
    }

    //   اضافة / * - +
    private fun setupOperatorButton() {
        val operator = listOf(
            R.id.btn_add_id to "+",
            R.id.btn_mul_id to "*",
            R.id.btn_sub_id to "-",
            R.id.btn_divide_id to "/"
        )
        for ((id, symbol) in operator) {
            val button = findViewById<androidx.appcompat.widget.AppCompatButton>(id)
            button.setOnClickListener {
                val current = inputTV.text.toString()
                if (current.isNotEmpty() && !isOperator(current.last())) {
                    largestInputTV()
                    inputTV.append(symbol)
                    outputTV.append(symbol)
                    smallerOutputTV()

                }
            }
        }
    }
    // .
    private fun setDotButton() {
        val dotBtn = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_dot_id)
        dotBtn.setOnClickListener {
            val current = inputTV.text.toString()
            val lastNumber = getLastNumber(current)

            if (!lastNumber.contains(".")) {
                if (lastNumber.isEmpty()) {
                    largestInputTV()
                    inputTV.append("0.")
                    smallerOutputTV()
                } else {
                    largestInputTV()
                    inputTV.append(".")
                    smallerOutputTV()
                }

            }
        }
    }


    // %
    private fun setupPercentButton() {
        val percentBtn =
            findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_percent_id)
        percentBtn.setOnClickListener {
            val current = inputTV.text.toString()
            var lastNumber = getLastNumber(current)

            if (lastNumber.isNotEmpty()) {
                val percentValue =

                    try {
                        lastNumber.toDouble() / 100
                    } catch (e: Exception) {
                        return@setOnClickListener
                    }
                val updated = current.dropLast(lastNumber.length) + percentValue
                inputTV.text = updated
            }
        }
    }


    //  =
    private fun setupEqualButton() {
        val equalBtn = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_equal_id)
        equalBtn.setOnClickListener {
            val expression = inputTV.text.toString()
            if (expression.isNotEmpty() && !isOperator(expression.last())) {
                try {
                    val result = eval(expression)
                    val finalResult = if (result % 1 == 0.0) {
                        result.toInt().toString()
                    } else {
                        result.toString()
                    }
                    outputTV.text = finalResult
                    smallerInputTV()
                    largestOutputTV()

                } catch (_: Exception) {
                }
            }
        }
    }


    //دالة تنظيف textView
    private fun setupClearButton() {
        val clearBtn = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_clear_id)
        clearBtn.setOnClickListener {
            inputTV.text = ""
            outputTV.text = ""

        }
    }

    //دالة حذف بتراجع حذف اخر حرف
    private fun setupDeleteButton() {
        val deleteBtn = findViewById<ImageView>(R.id.btn_delete_id)
        deleteBtn.setOnClickListener {

            val input = inputTV.text.toString()
            val output = outputTV.text.toString()

            if (input.isNotEmpty()) {
                inputTV.text = input.dropLast(1)

            }
            if (output.isNotEmpty()) {
                outputTV.text = input.dropLast(1)

            }

        }
    }

//    //دالة مساعدة للحذف بتراجع حذف اخر حرف
//    override fun onBackPressed() {
//
//
//        val input = inputTV.text.toString()
//        val output = outputTV.text.toString()
//
//        if (input.isNotEmpty()) {
//            inputTV.text = input.dropLast(1)
//
//        }
//        if (output.isNotEmpty()) {
//            outputTV.text = input.dropLast(1)
//
//        }
//
//    }

}


// دالة eval مثل المكتبة يتم استدعائها لتقوم بالعمليات الحسابيه
private fun eval(expression: String): Double {
    return ExpressionBuilder(expression).build().evaluate()
}
// دالة الاشارات المساعدة
private fun isOperator(char: Char): Boolean {
    return char == '+' || char == '-' || char == '*' || char == '/' || char == '%'
}
//دالة فحص ان textView لا ينتهي باشاره من اجل عدم حدوث خطا يعني تمنع تكرار الاشارات
private fun getLastNumber(expression: String): String {
    val operator = listOf("+", "-", "*", "/")
    var lastOperatorIndex = -1

    for (op in operator) {
        val index = expression.lastIndexOf(op)
        if (index > lastOperatorIndex) {
            lastOperatorIndex = index
        }
    }
    return if (lastOperatorIndex != -1) {
        expression.substring(lastOperatorIndex + 1)
    } else {
        expression
    }
}

//دال لتشكيل الادخال والنتيجة تغير حجم النص
fun largestInputTV(){
//    inputTV.setTextColor(ContextCompat.getColor(this, R.color.text))
    inputTV.textSize = 25f
}
    fun smallerInputTV(){
//        inputTV.setTextColor(ContextCompat.getColor(this, R.color.grey))
        inputTV.textSize = 20f
    }
    fun largestOutputTV(){
//        outputTV.setTextColor(ContextCompat.getColor(this, R.color.text))
        outputTV.textSize = 25f
    }

    fun smallerOutputTV(){
//        outputTV.setTextColor(ContextCompat.getColor(this, R.color.grey))
        outputTV.textSize = 20f
    }


