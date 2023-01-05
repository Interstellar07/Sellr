package com.example.sellr.adapters
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sellr.R
import com.example.sellr.data.CartModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartRVAdapter(val fragment: Fragment, courseModelArrayList: ArrayList<String>) :
    RecyclerView.Adapter<CartRVAdapter.ViewHolder>() {
    private val courseModelArrayList: ArrayList<String>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // to inflate the layout for each item of recycler view.
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.cart_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val model: String = courseModelArrayList[position]
        val courseModelArrayList: ArrayList<CartModel> = ArrayList()

        val database= FirebaseDatabase.getInstance("https://sellr-7a02b-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Items").child(model.toString())

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val items = snapshot.getValue(CartModel::class.java);
                if (items != null && items.sold == false) {
                   holder.courseNameTV.text = items.productName
                    holder.courseRatingTV.text = items.price


                     Glide.with(holder.courseIV).load(items.imagePrimary).centerCrop().into(holder.courseIV)

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        println("Fav post id are "+ model)
        // to set data to textview and imageview of each card layout

//        holder.courseNameTV.text="Sameer"
//        holder.courseRatingTV.text="99"
//       // Glide.with(holder.courseIV).load(model.item_image).centerCrop().into(holder.courseIV)
        val btn=holder.removeButton
        btn.setOnClickListener {
            println("Testing")

            val database= FirebaseDatabase.getInstance("https://sellr-7a02b-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users").child("9BBFtFinnoUNc388iUKm7AbPKrs2").child("favpost")
            database.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                   if(snapshot.exists())
                   {
                       for(userSnapshot in snapshot.children)
                       {
                           val value = userSnapshot.getValue().toString()
                           println("Value is$value")
                           if(value == model) {
                               val key = userSnapshot.key.toString()
                               val dtbremove =
                                   FirebaseDatabase.getInstance("https://sellr-7a02b-default-rtdb.asia-southeast1.firebasedatabase.app")
                                       .getReference("Users").child("9BBFtFinnoUNc388iUKm7AbPKrs2")
                                       .child("favpost").child(key)
                               dtbremove.removeValue()
                               println("Deleted")

                           }

                       }                   }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            }
            )

//            courseModelArrayList.removeAt(position)
//            notifyItemRemoved(position);
            notifyItemRangeChanged(position, courseModelArrayList.size);
//            //Toast.makeText(, "Item Removed $position",Toast.LENGTH_SHORT).show()
        }
    }
    override fun getItemCount(): Int {
        // this method is used for showing number of card items in recycler view.
        return courseModelArrayList.size
    }

    // View holder class for initializing of your views such as TextView and Imageview.
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseIV: ImageView
        val courseNameTV: TextView
        val courseRatingTV: TextView
        val removeButton : Button
        init {
            courseIV = itemView.findViewById(R.id.item_image)
            courseNameTV = itemView.findViewById(R.id.item_name)
            courseRatingTV = itemView.findViewById(R.id.item_price)
            removeButton=itemView.findViewById(R.id.remove_button)

        }

    }

    // Constructor
    init {
        this.courseModelArrayList = courseModelArrayList
    }
}
