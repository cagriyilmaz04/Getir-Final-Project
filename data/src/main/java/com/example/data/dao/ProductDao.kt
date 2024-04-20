package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.data.models.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert
    suspend fun insert(product: ProductEntity)

    @Update
    suspend fun update(product: ProductEntity)

    @Query("DELETE FROM products_table_getir WHERE productId = :productId")
    suspend fun delete(productId: String)

    @Query("SELECT * FROM products_table_getir WHERE productId = :productId")
    fun getProduct(productId: String): Flow<ProductEntity>

    @Query("SELECT * FROM products_table_getir")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("DELETE FROM products_table_getir")
    suspend fun deleteAllProducts()

    @Query("UPDATE products_table_getir SET quantity = quantity + :amount WHERE productId = :productId")
    suspend fun increaseProductQuantity(productId: String, amount: Int)

    @Query("UPDATE products_table_getir SET quantity = quantity - :amount WHERE productId = :productId")
    suspend fun decreaseProductQuantity(productId: String, amount: Int)
}
