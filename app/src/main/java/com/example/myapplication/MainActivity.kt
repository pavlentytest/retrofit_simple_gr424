package com.example.myapplication

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.gson.annotations.SerializedName
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.gson.gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {

    companion object {
        const val BASE_URL = "https://predictor.yandex.net"
        const val METHOD_URL = "/api/v1/predict.json/complete"
        const val KEY = "pdct.1.1.20240423T090039Z.a0e1a7ba9898c947.0c44f9bc5b8f58fe892c8536fbf663d46d84591f";
        const val LIMIT = 5
        const val LANG = "en"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var retrofit: Retrofit
    private lateinit var yandexAPI: YandexAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        doKtorRequest()
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        yandexAPI = retrofit.create(YandexAPI::class.java)
        binding.editText.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable?) {
                doRequest()
            }
        })
    }
    fun doKtorRequest() {
        val client = HttpClient{
            install(ContentNegotiation) {
                gson()
            }
        }
        GlobalScope.launch {
            val httpclient = client.get("$BASE_URL$METHOD_URL?key=$KEY&q=moscow&limit=$LIMIT&lang=$LANG")
            val answer: Answer = httpclient.body()
            Log.d("RRR", answer.toString())
        }

    }

    fun doRequest() {
        yandexAPI.complete(KEY,binding.editText.text.toString(),LANG,LIMIT).enqueue(object: Callback<Answer>{
            override fun onResponse(p0: Call<Answer>, p1: Response<Answer>) {
                if(p1.isSuccessful) {
                    binding.textView.text = p1.body()?.text?.joinToString("\n")
                }
            }

            override fun onFailure(p0: Call<Answer>, p1: Throwable) {
               Log.d("RRR",p1.message.toString())
            }

        })
    }
}
data class Answer (
    @SerializedName("endOfWord") var endOfWord: Boolean? = null,
    @SerializedName("pos") var pos: Int? = null,
    @SerializedName("text") var text: ArrayList<String> = arrayListOf()
)