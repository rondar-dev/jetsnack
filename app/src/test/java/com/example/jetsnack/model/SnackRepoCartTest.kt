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

    @Before
    fun rememberCart() {
        initialCart = SnackRepo.getCart().value
    }

    @After
    fun restoreCart() {
        val initialIds = initialCart.map { it.snack.id }
        SnackRepo.getCart().value
            .filter { it.snack.id !in initialIds }
            .forEach { SnackRepo.removeFromCart(it.snack.id) }
        initialCart.forEach { SnackRepo.updateCartCount(it.snack.id, it.count) }
    }

    private fun snackNotInCart(): Snack {
        val cartIds = initialCart.map { it.snack.id }
        return SnackRepo.getSnacks().flatMap { it.snacks }.first { it.id !in cartIds }
    }

    @Test
    fun addToCart_newSnack_addsOrderLineWithCount() {
        val snack = snackNotInCart()

        SnackRepo.addToCart(snack.id, 2)

        assertEquals(initialCart + OrderLine(snack, 2), SnackRepo.getCart().value)
    }

    @Test
    fun addToCart_snackAlreadyInCart_increasesItsCount() {
        val line = initialCart.first()

        SnackRepo.addToCart(line.snack.id, 3)

        val cart = SnackRepo.getCart().value
        assertEquals(initialCart.size, cart.size)
        assertEquals(line.count + 3, cart.first { it.snack.id == line.snack.id }.count)
    }
}
