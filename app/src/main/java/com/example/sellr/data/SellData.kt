package com.example.sellr.data

data class SellData(val productName : String? = null,
                    val productDesc : String? = null, val category : String? = null,
                    val condition : String? = null,val price : String? = null, val imagePrimary : String? = null,
                    val imageSecond : String? = null,val imageThird : String? = null,
                    val userUID : String? = null)
