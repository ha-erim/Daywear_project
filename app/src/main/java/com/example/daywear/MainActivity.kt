package com.example.daywear

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


// xml 파일 형식을 data class로 구현
data class WEATHER (val response : RESPONSE)
data class RESPONSE(val header : HEADER, val body : BODY)
data class HEADER(val resultCode : Int, val resultMsg : String)
data class BODY(val dataType : String, val items : ITEMS)
data class ITEMS(val item : List<ITEM>)
// category : 자료 구분 코드, fcstDate : 예측 날짜, fcstTime : 예측 시간, fcstValue : 예보 값
data class ITEM(val category : String, val fcstDate : String, val fcstTime : String, val fcstValue : String)


// retrofit을 사용하기 위한 빌더 생성
private val retrofit = Retrofit.Builder()
    .baseUrl("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

object ApiObject {
    val retrofitService: WeatherInterface by lazy {
        retrofit.create(WeatherInterface::class.java)
    }
}

class MainActivity : AppCompatActivity() {
//class MainActivity : areaSetActivity() {

    //val clothList = ArrayList<Bitmap>()

    private lateinit var database: DatabaseReference

    lateinit var tvRainRatio : TextView     // 강수 확률
    lateinit var tvRainType : TextView      // 강수 형태
    lateinit var tvHumidity : TextView      // 습도
    lateinit var tvSky : TextView           // 하늘 상태
    lateinit var tvTemp : TextView          // 온도
    lateinit var btnRefresh : Button        // 새로고침 버튼
    lateinit var nowRegion : TextView // 지역

    lateinit var cloth1: ImageView
    lateinit var cloth2: ImageView
    lateinit var cloth3: ImageView
    lateinit var cloth4: ImageView
    lateinit var cloth5: ImageView //lateinit : null값 지정하지 않고 초기화

    var base_date = "20220725"  // 발표 일자
    var base_time = "1100"      // 발표 시각
    var nx = "60"               // 예보지점 X 좌표
    var ny = "127"              // 예보지점 Y 좌표
    //대구 80,91
    var user_region = ""

    fun setting(uid : String){
        database.child("users").child(uid.toString()).child("region").get().addOnCompleteListener {
            Log.i("firebase","Got value ${it.result.value}")
            user_region = it.result.value.toString()
            Log.d(TAG, "in curly user region : $user_region")
            setCoordinates(user_region)
            nowRegion.text = user_region
            // nx, ny 지점의 날씨 가져와서 설정하기
            setWeather(nx, ny)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val user = Firebase.auth.currentUser // 데이터베이스에서 현재 사용자 불러오기
        val uid = user?.uid

        database = Firebase.database.reference
        //데이터베이스에서 현재 사용자의 정보 불러오기
        //안드로이드 껐다가 다시 켰을 때, 자동로그인 상태면 현재 사용자의 정보를 불러오지 못함.
        //addOnSuccessListener -> 비동기 방식. 그래서 region = null 이었다.
//        database.child("users").child(uid.toString()).get().addOnSuccessListener {
//            Log.i("firebase", "Got value ${it.value}")
//            user_region = it.child("region").value.toString()
//            Log.d(TAG, "user region : $user_region")
//
//
//        }.addOnFailureListener{
//            Log.e("firebase", "Error getting data", it)
//        }

        var swipe = findViewById<SwipeRefreshLayout>(R.id.swipe)
        swipe.setOnRefreshListener {
            setting(uid.toString())
            swipe.isRefreshing = false
        }

//        database.child("users").child(uid.toString()).child("region").get().addOnCompleteListener {
//            Log.i("firebase","Got value ${it.result.value}")
//           user_region = it.result.value.toString()
//            Log.d(TAG, "in curly user region : $user_region")
//            setCoordinates(user_region)
//            setWeather(nx, ny)
//        }

        setting(uid.toString())

        Log.d(TAG, "user region : $user_region")

        Log.d(TAG, "nx : $nx")
        Log.d(TAG, "nx : $ny")

        val setbtn = findViewById<ImageView>(R.id.set_btn)
        setbtn.setOnClickListener {
            val intent = Intent(this, SetActivity::class.java)
            startActivity(intent)
            finish()
        }

        tvRainRatio = findViewById(R.id.tvRainRatio)
        tvRainType = findViewById(R.id.tvRainType)
        tvHumidity = findViewById(R.id.tvHumidity)
        tvSky = findViewById(R.id.tvSky)
        tvTemp = findViewById(R.id.tvTemp)
        nowRegion = findViewById(R.id.nowRegion)
        cloth1 = findViewById(R.id.cloth1)
        cloth2 = findViewById(R.id.cloth2)
        cloth3 = findViewById(R.id.cloth3)
        cloth4 = findViewById(R.id.cloth4)
        cloth5 = findViewById(R.id.cloth5)

    }

    // 날씨 가져와서 설정하기
    fun setWeather(nx : String, ny : String) {
        // 준비 단계 : base_date(발표 일자), base_time(발표 시각)
        // 현재 날짜, 시간 정보 가져오기
        val cal = Calendar.getInstance()
        base_date = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(cal.time) // 현재 날짜
        Log.d(TAG, "now date : $base_date")

        val time = SimpleDateFormat("HH", Locale.getDefault()).format(cal.time) // 현재 시간
        Log.d(TAG, "now time : $time")
        // API 가져오기 적당하게 변환
        base_time = getTime(time)
        Log.d(TAG, "now base time : $base_time")
        // 동네예보  API는 3시간마다 현재시간+4시간 뒤의 날씨 예보를 알려주기 때문에
        // 현재 시각이 00시가 넘었다면 어제 예보한 데이터를 가져와야함
        if (base_time >= "2000") {
            cal.add(Calendar.DATE, -1).toString()
            base_date = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(cal.time)
        }

        // 날씨 정보 가져오기
        // (응답 자료 형식-"JSON", 한 페이지 결과 수 = 10, 페이지 번호 = 1, 발표 날싸, 발표 시각, 예보지점 좌표)
        val call = ApiObject.retrofitService.GetWeather("JSON", 12, 1, base_date, base_time, nx, ny)

        // 비동기적으로 실행하기
        call.enqueue(object : retrofit2.Callback<WEATHER> {
            // 응답 성공 시
            override fun onResponse(call: retrofit2.Call<WEATHER>, response: Response<WEATHER>) { //
                if (response.isSuccessful) {
                    // 날씨 정보 가져오기
                    var it: List<ITEM> = response.body()!!.response.body.items.item
                    Log.i("weather", "Got value ${it[0].category}") //TMP
                    Log.i("weather", "Got value ${it[1].category}") //UUU
                    Log.i("weather", "Got value ${it[2].category}") //VVV
                    Log.i("weather", "Got value ${it[3].category}") //VEC
                    Log.i("weather", "Got value ${it[4].category}") //WSD
                    Log.i("weather", "Got value ${it[5].category}") //SKY
                    Log.i("weather", "Got value ${it[6].category}") //PTY
                    Log.i("weather", "Got value ${it[7].category}") //POP
                    Log.i("weather", "Got value ${it[8].category}") //WAV
                    Log.i("weather", "Got value ${it[9].category}") //PCP
                    Log.i("weather", "Got value ${it[10].category}") //REH
                    Log.i("weather", "Got value ${it[11].category}") //SNO


                    var rainRatio = ""      // 강수 확률
                    var rainType = ""       // 강수 형태
                    var humidity = ""       // 습도
                    var sky = ""            // 하능 상태
                    var temp = ""           // 기온
                    for (i in 0..11) {
                        when(it[i].category) {
                            "POP" -> rainRatio = it[i].fcstValue    // 강수 기온
                            "PTY" -> rainType = it[i].fcstValue     // 강수 형태
                            "REH" -> humidity = it[i].fcstValue     // 습도
                            "SKY" -> sky = it[i].fcstValue          // 하늘 상태
                            "TMP" -> temp = it[i].fcstValue         // 기온
                            else -> continue
                        }

                    }
                    // 날씨 정보 텍스트뷰에 보이게 하기
                    setWeather(rainRatio, rainType, humidity, sky, temp)

                    // 토스트 띄우기
                    //fcstTime = 예보시각
                    Toast.makeText(applicationContext, it[0].fcstDate + ", " + it[0].fcstTime + "의 날씨 정보입니다.", Toast.LENGTH_SHORT).show()
                }
            }

            // 응답 실패 시
            override fun onFailure(call: retrofit2.Call<WEATHER>, t: Throwable) {
                Log.d("api fail", t.message.toString())
            }
        })
    }

    // 텍스트 뷰에 날씨 정보 보여주기
    fun setWeather(rainRatio : String, rainType : String, humidity : String, sky : String, temp : String) {
        // 강수 확률
        tvRainRatio.text = "강수확률 $rainRatio%"

        // 강수 형태
        var result = ""
        when(rainType) {
            "0" -> result = "없음"
            "1" -> result = "비"
            "2" -> result = "비/눈"
            "3" -> result = "눈"
            "4" -> result = "소나기"
            "5" -> result = "빗방울"
            "6" -> result = "빗방울/눈날림"
            "7" -> result = "눈날림"
            else -> "오류"
        }
        tvRainType.text = "강수형태 $result"

        // 습도
        tvHumidity.text = "습도 $humidity%"
        //tvHumidity.text = humidity

        // 하늘 상태
        result = ""
        when(sky) {
            "1" -> result = "맑음"
            "3" -> result = "구름 많음"
            "4" -> result = "흐림"
            else -> "오류"
        }
        tvSky.text = "하늘상태 $result"

        // 온도
        tvTemp.text = temp + "°"
//        var tempint = temp.toInt()
//        when {
//            tempint>=20 -> {
//                tvTemp.text="20도 이상입니다."
//            }
//            tempint >= 30 -> {
//                tvTemp.text = "30도 이상입니다."
//            }
//            else -> {
//                tvTemp.text = temp + "°"
//            }
//        }
        setImage(humidity.toString(),sky.toString(),temp.toString())

    }

    // 시간 설정하기
    // 동네 예보 API는 3시간마다 현재시각+4시간 뒤의 날씨 예보를 보여줌
    // 따라서 현재 시간대의 날씨를 알기 위해서는 아래와 같은 과정이 필요함. 자세한 내용은 함께 제공된 파일 확인
    fun getTime(time: String) : String {
        var result = ""
        when(time) {
            in "00".."02" -> result = "2000"    // 00~02
            in "03".."05" -> result = "2300"    // 03~05
            in "06".."08" -> result = "0200"    // 06~08
            in "09".."11" -> result = "0500"    // 09~11
            in "12".."14" -> result = "0800"    // 12~14
            in "15".."17" -> result = "1100"    // 15~17
            in "18".."20" -> result = "1400"    // 18~20
            else -> result = "1700"             // 21~23
        }
        return result
    }

    fun setImage(humidity: String, sky: String, temp: String) {
//        when (sky) {
//            "1" -> cloth1.setImageResource(R.drawable.clothes1)
//            "3" -> cloth1.setImageResource(R.drawable.clothes2)
//            "4" -> cloth1.setImageResource(R.drawable.clothes3)
//            else -> "오류"
//        }
//        val humidity_int: Int = humidity.toInt()
//        if (humidity_int < 30) {
//            cloth3.setImageResource(R.drawable.clothes4)
//        } else if (humidity_int < 60) {
//            cloth3.setImageResource(R.drawable.clothes5)
//        } else if (humidity_int <= 100) {
//            cloth3.setImageResource(R.drawable.cloth9)
//        }
//        val humidity_int: Int = humidity.toInt()
//
//        if (humidity_int < 30) {
//            for( i in 0..5)
//            run {
////                getResources().getIdentifier("clothes1_" + (i + 1), "drawable", packageName)
//                val bitmap = resources.getIdentifier("clothes1_" + (i + 1), "drawable", packageName)
//                clothList.add(bitmap)
//            }
//            //cloth3.setImageResource(clothList[0])
//        }
//            else if (humidity_int < 60) {
//            for(i in 0..5)
//            run {
////                getResources().getIdentifier("clothes2_" + (i + 1), "drawable", packageName)
//                val bitmap = resources.getIdentifier("clothes2_" + (i + 1), "drawable", packageName)
//                clothList.add(bitmap)
//            }
//            //cloth3.setImageResource()
//        } else if (humidity_int <= 100) {
//            for(i in 0..5)
//            run {
////                getResources().getIdentifier("clothes3_" + (i + 1), "drawable", packageName)
//                val bitmap = resources.getIdentifier("clothes3_" + (i + 1), "drawable", packageName)
//                clothList.add(bitmap)
//            }
//            //cloth3.setImageResource()
//        }
        val humidity_int: Int = humidity.toInt() //문자열 숫자로 변환
        val temp_int: Int = temp.toInt()
        val random = Random() //난수 선언
        val num = random.nextInt(4) //0~3 범위 난수 생성
        if (temp_int < 30) {
            val vitmap = getResources().getIdentifier("clothes3_${num+1}", "drawable", packageName) //clothes1_?의 링크 저장
            cloth1.setImageResource(vitmap) //이미지를 vitmap의 링크로 변환
        }

//        val temp_int: Int = temp.toInt()
//        when {
//            temp_int < 10 -> {
//                cloth4.setImageResource(R.drawable.clothes1_2)
//            }
//            temp_int < 20 -> {
//                cloth4.setImageResource(R.drawable.clothes2)
//            }
//            temp_int in 21..29 -> {
//                cloth1.setImageResource(R.drawable.clothes2_1)
//                cloth2.setImageResource(R.drawable.clothes2_2)
//                cloth3.setImageResource(R.drawable.clothes2_3)
//                cloth4.setImageResource(R.drawable.clothes2_4)
//                cloth5.setImageResource(R.drawable.clothes2_5)
//            }
//            else -> {
//                cloth1.setImageResource(R.drawable.clothes3_1)
//                cloth2.setImageResource(R.drawable.clothes3_2)
//                cloth3.setImageResource(R.drawable.clothes3_3)
//                cloth4.setImageResource(R.drawable.clothes3_4)
//                cloth5.setImageResource(R.drawable.clothes3_5)
//            }
//        }
    }
    private fun setCoordinates(selected_area: String){

        when (selected_area) {
            "대구광역시" -> {
                // 좌표 설정
                this.nx = "89"
                this.ny = "90"
            }
            "경상북도" -> {
                nx = "89"
                ny = "91"
            }
            "경상남도" -> {
                nx = "91"
                ny = "77"
            }
            "서울특별시" -> {
                nx = "60"
                ny = "127"
            }
            "경기도" -> {
                nx = "60"
                ny = "120"
            }
            "강원도" -> {
                nx = "73"
                ny = "134"
            }
            "세종특별자치시" -> {
                nx = "66"
                ny = "103"
            }
            "인천광역시" -> {
                nx = "55"
                ny = "124"
            }
            "충청북도" -> {
                nx = "69"
                ny = "107"
            }
            "충청남도" -> {
                nx = "68"
                ny = "100"
            }
            "전라북도" -> {
                nx = "63"
                ny = "89"
            }
            "전라남도" -> {
                nx = "51"
                ny = "67"
            }
            "울산광역시" -> {
                nx = "102"
                ny = "84"
            }
            "부산광역시" -> {
                nx = "98"
                ny = "76"
            }
            "광주광역시" -> {
                nx = "58"
                ny = "74"
            }
            "제주특별자치도" -> {
                nx = "52"
                ny = "38"
            }
        }
    }

//        val user = Firebase.auth.currentUser // 데이터베이스에서 현재 사용자 불러오기
//
//        user?.let {
//            // Name, email address, and profile photo Url
//            val name = user.displayName
//            val email = user.email
//            val photoUrl = user.photoUrl
//
//            Log.d(TAG,"name : " + name.toString())
//            Log.d(TAG,"email : " + email.toString())
//
//            // Check if user's email is verified
//            val emailVerified = user.isEmailVerified
//
//            // The user's ID, unique to the Firebase project. Do NOT use this value to
//            // authenticate with your backend server, if you have one. Use
//            // FirebaseUser.getToken() instead.
//            val uid = user.uid
//           // Log.d(TAG,"uid : " + uid.toString())
//        }
    }


