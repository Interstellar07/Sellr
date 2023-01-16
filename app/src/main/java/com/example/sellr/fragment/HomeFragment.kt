package com.example.sellr.fragment

//import android.widget.SearchView
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sellr.DescriptionPage
import com.example.sellr.R
import com.example.sellr.SellActivity
import com.example.sellr.datahome.filterAdapter
import com.example.sellr.datahome.filterData
import com.example.sellr.datahome.items_home
import com.example.sellr.datahome.myAdapterhome
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.database.*
import java.util.*


class HomeFragment : Fragment() {

    //for items
    private lateinit var recylerView: RecyclerView
    private lateinit var datalist: ArrayList<items_home>
    private lateinit var searchList: ArrayList<items_home>
    private lateinit var searchView: SearchView
    private lateinit var dbref: DatabaseReference
    private lateinit var recyclerViewAdapter:myAdapterhome
    //for filer
    private lateinit var datalistforfilter : ArrayList<filterData>
    private lateinit var recylerViewfilter: RecyclerView


    //for filtered datalist in myadapterhome
    private lateinit var datalistforfilteredmyAdapter: ArrayList<items_home>



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        view.findViewById<ExtendedFloatingActionButton>(R.id.sell_button).setOnClickListener {
            val intent = Intent(activity, SellActivity::class.java)
            startActivity(intent)
        }
        return view

    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //for filter
        val layoutManagerfilter = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        recylerViewfilter = view.findViewById(R.id.filter)
        recylerViewfilter.layoutManager = layoutManagerfilter

        datalistforfilter = arrayListOf()

        datalistforfilter.add(filterData("All"))
        datalistforfilter.add(filterData("Electronics"))
        datalistforfilter.add(filterData("Books"))
        datalistforfilter.add(filterData("Vehicles"))
        datalistforfilter.add(filterData("Clothes"))
        datalistforfilter.add(filterData("Others"))

        var adapterfilter = filterAdapter(datalistforfilter)
        recylerViewfilter.adapter = adapterfilter



        // for items
        val layoutManager = GridLayoutManager(context, 2)
        recylerView = view.findViewById(R.id.Home_rc)
        recylerView.layoutManager = layoutManager

        searchView = view.findViewById(R.id.searchView)

        datalist = arrayListOf()
        searchList = arrayListOf()
        datalistforfilteredmyAdapter= arrayListOf()

        datalistforfilteredmyAdapter.addAll(datalist)

        getUserData()
        recyclerViewAdapter=myAdapterhome(this@HomeFragment,searchList)
        recylerView.adapter=recyclerViewAdapter
        //To hide floating action button
        //Not tested properly yet
        recylerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val fab= view.findViewById<ExtendedFloatingActionButton>(R.id.sell_button)
                if (dy > 10 && fab.isShown) {
                    fab.hide()
                }
                if (dy < -10 && !fab.isShown) {
                    fab.show()
                }

            }
        })

        //for product details
        recyclerViewAdapter.onItemClick = { product ->

            val value = product.pid
            val i = Intent(activity, DescriptionPage::class.java)
            i.putExtra("key", value)
            startActivity(i)
        }



        //for searching


        searchView.clearFocus()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                searchList.clear()
                val searchText = p0!!.toLowerCase(Locale.getDefault())
                if(searchText.isNotEmpty()){
                    datalist.forEach{
                        if(it.productName?.toLowerCase(Locale.getDefault())?.contains(searchText) == true){
                            searchList.add(it)
                        }
                    }
                    recylerView.adapter?.notifyDataSetChanged()
                }
                else{
                    searchList.clear()
                    searchList.addAll(datalist)
                    recylerView.adapter?.notifyDataSetChanged()
                }
                return false
            }


        })

        //koi filter ko press karega toh kya hoga uska code hai

        adapterfilter.setOnItemClickListener(object:filterAdapter.onItemClickListener{
            override fun onItemClick(category: String) {
                datalistforfilteredmyAdapter.clear()
                datalist.forEach{
                    if(it.category == category){
                        datalistforfilteredmyAdapter.add(it)
                    }
                }
                if(category == "All"){
                    datalistforfilteredmyAdapter.addAll(datalist)
                }
                recylerView.adapter?.notifyDataSetChanged()
                recylerView.adapter = myAdapterhome(this@HomeFragment,datalistforfilteredmyAdapter)

                searchView.clearFocus()
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                    override fun onQueryTextSubmit(p0: String?): Boolean {
                        searchView.clearFocus()
                        return true
                    }

                    override fun onQueryTextChange(p0: String?): Boolean {
                        searchList.clear()
                        val searchText = p0!!.lowercase(Locale.getDefault())
                        if(searchText.isNotEmpty()){
                            datalistforfilteredmyAdapter.forEach{
                                if(it.productName?.lowercase(Locale.getDefault())?.contains(searchText) == true){
                                    searchList.add(it)
                                }
                            }
                            recylerView.adapter?.notifyDataSetChanged()
                            recylerView.adapter = myAdapterhome(this@HomeFragment,searchList)
                        }
                        else{
                            searchList.clear()
                            searchList.addAll(datalistforfilteredmyAdapter)
                            recylerView.adapter?.notifyDataSetChanged()
                            recylerView.adapter = myAdapterhome(this@HomeFragment,searchList)
                        }
                        return false
                    }


                })
            }



        }


        )
    }

    private fun getUserData() {

        searchView.setQuery("", false)
        dbref = FirebaseDatabase.getInstance("https://sellr-7a02b-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("Items")

        dbref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){

                    for(userSnapshot in snapshot.children){

                        val items = userSnapshot.getValue(items_home::class.java)
                        if (items != null) {
                            if(!items.sold) {
                                datalist.add(items)
                            }
                        }
                    }



                    searchList.addAll(datalist)
                    recyclerViewAdapter.notifyDataSetChanged()

                }

            }

            override fun onCancelled(error: DatabaseError) {

                val text = "Error"
                val duration = Toast.LENGTH_SHORT

                val toast = Toast.makeText(activity, text, duration)
                toast.show()
            }

        })



    }

}

