package com.edureminder.instagramclone

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.edureminder.instagramclone.databinding.ActivitySignupBinding
import com.edureminder.instagramclone.models.UserModel
import com.edureminder.instagramclone.utils.USER_NODE
import com.edureminder.instagramclone.utils.USER_PROFILE_FOLDER
import com.edureminder.instagramclone.utils.uploadImage
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    lateinit var user : UserModel
    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()){ uri ->
        uri?.let {
            uploadImage(uri, USER_PROFILE_FOLDER) {
                if(it == null) {

                } else {
                    user.image = it
                    binding.profileImage.setImageURI(uri)
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

         user = UserModel()

         binding.registerButton.setOnClickListener {
            if(binding.name.editText?.text.toString().equals("") or
                binding.email.editText?.text.toString().equals("") or
                binding.password.editText?.text.toString().equals(""))
            {
                Toast.makeText(this@SignupActivity, "Please fill all information", Toast.LENGTH_SHORT).show()
            }
            else {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    binding.email.editText?.text.toString(),
                    binding.password.editText?.text.toString()
                ).addOnCompleteListener {
                    result ->
                    if(result.isSuccessful) {
                        user.name = binding.name.editText?.text.toString()
                        user.email = binding.email.editText?.text.toString()
                        user.password = binding.password.editText?.text.toString()

                        Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).set(user)
                            .addOnSuccessListener {
//                                Toast.makeText(this@SignupActivity, "Login success", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@SignupActivity, HomeActivity::class.java))
                                finish()
                            }
                    } else {
                        Toast.makeText(this@SignupActivity, result.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        binding.addProfileImage.setOnClickListener{
            launcher.launch("image/*")
        }
    }
}