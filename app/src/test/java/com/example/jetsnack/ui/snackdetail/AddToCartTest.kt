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

package com.example.jetsnack.ui.snackdetail

import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.jetsnack.model.OrderLine
import com.example.jetsnack.model.SnackRepo
import com.example.jetsnack.ui.MainActivity
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [35], qualifiers = "w411dp-h891dp")
class AddToCartTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var initialCart: List<OrderLine>

    @Before
    fun rememberCart() {
        initialCart = SnackRepo.getCart().value
    }

    @After
    fun restoreCart() {
        SnackRepo.getCart().value
            .filter { line -> initialCart.none { it.snack.id == line.snack.id } }
            .forEach { SnackRepo.removeFromCart(it.snack.id) }
    }

    private fun countOf(snackName: String) =
        SnackRepo.getCart().value.firstOrNull { it.snack.name == snackName }?.count

    private fun openSnackDetail(snackName: String) {
        composeTestRule.onNodeWithText(snackName).performClick()
        composeTestRule.waitForIdle()
    }

    private fun increaseQuantity() {
        composeTestRule.onNodeWithContentDescription("Increase").performClick()
    }

    private fun decreaseQuantity() {
        composeTestRule.onNodeWithContentDescription("Decrease").performClick()
    }

    private fun addToCartButton() = composeTestRule.onNodeWithText("ADD TO CART")

    @Test
    fun addToCart_putsSnackWithChosenQuantityInCartScreen() {
        openSnackDetail("Chips")
        increaseQuantity()

        addToCartButton().performClick()
        composeTestRule.waitForIdle()

        assertEquals(2, countOf("Chips"))

        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("MY CART").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Order (4 items)").assertExists()
        composeTestRule.onAllNodesWithText("Chips").onFirst().assertExists()
    }

    @Test
    fun addToCart_quantityZero_cannotAdd() {
        openSnackDetail("Chips")
        decreaseQuantity()

        addToCartButton().assertIsNotEnabled()
    }
}
