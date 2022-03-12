package com.itis.springpractice.activity

class AuthActivity : AppCompatActivity(R.layout.activity_auth) {

    private lateinit var controller: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        controller =
            (supportFragmentManager.findFragmentById(R.id.host_fragment) as NavHostFragment)
                .navController
    }
}
