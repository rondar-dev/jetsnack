/*
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetsnack.model

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SnackRepoCartTest {

    private lateinit var initialCart: List<OrderLine>

    /** A snack that the cart does not hold at the start. */
    private val newSnack = snacks[0]

    @Before
    fun rememberCart() {
        initialCart = SnackRepo.cart.value
    }

    @After
    fun restoreCart() {
        SnackRepo.removeFromCart(newSnack.id)
        initialCart.forEach { SnackRepo.updateCartCount(it.snack.id, it.count) }
    }

    @Test
    fun addToCart_newSnack_addsOrderLineAtTheEnd() {
        SnackRepo.addToCart(newSnack.id, 2)

        assertEquals(initialCart + OrderLine(newSnack, 2), SnackRepo.cart.value)
    }

    @Test
    fun addToCart_snackInCart_increasesItsCount() {
        val existing = initialCart.first()

        SnackRepo.addToCart(existing.snack.id, 3)

        assertEquals(initialCart.size, SnackRepo.cart.value.size)
        assertEquals(existing.count + 3, SnackRepo.cart.value.first().count)
    }

    @Test
    fun addToCart_zeroCount_leavesCartUnchanged() {
        SnackRepo.addToCart(newSnack.id, 0)

        assertEquals(initialCart, SnackRepo.cart.value)
    }
}
