package com.example.horizontrack_mad_cw.models

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
data class DietPlan(
    val breakfastPlan: String,
    val lunchPlan: String,
    val dinnerPlan: String,
    val snacksPlan: String,
    val waterIntake: String,
    val BMIcategory: String // New field to store the BMI category (underweight, normal, overweight, obesity)
)


fun saveDietPlansToFirestore() {
    val firestore = FirebaseFirestore.getInstance()

    // Sample data for Underweight, Normal, Overweight, and Obesity categories
    val underweightPlans = listOf(
        DietPlan("Whole-grain toast with avocado and scrambled eggs", "Grilled chicken with sweet potatoes and a mixed salad", "Brown rice with lean beef stir-fried with vegetables", "Peanut butter on whole wheat crackers", "Drink at least 2 liters of water daily", "Underweight"),
        DietPlan("Smoothie with banana, spinach, protein powder, and almond milk", "Turkey sandwich with avocado, spinach, and whole-grain bread", "Grilled shrimp with quinoa and roasted vegetables", "Greek yogurt with honey and almonds", "Drink at least 2 liters of water daily", "Underweight"),
        DietPlan("Scrambled eggs with spinach and mushrooms on whole-grain toast", "Grilled chicken with mashed potatoes and green beans", "Pasta with lean ground beef and marinara sauce", "Protein bar or trail mix", "Drink at least 2 liters of water daily", "Underweight"),
        DietPlan("Peanut butter smoothie with banana and oats", "Chicken breast with brown rice and steamed veggies", "Grilled salmon with quinoa and spinach", "Almonds or Greek yogurt", "Drink at least 2 liters of water daily", "Underweight"),
        DietPlan("Whole wheat pancakes with honey and berries", "Grilled turkey with quinoa and a side of broccoli", "Shrimp stir-fry with vegetables", "Hard-boiled eggs", "Drink at least 2 liters of water daily", "Underweight")
    )

    val normalPlans = listOf(
        DietPlan("Oats with fruits and nuts", "Grilled chicken with quinoa and vegetables", "Salmon with steamed broccoli", "Almonds or Greek yogurt", "Drink at least 2 liters of water daily", "Normal"),
        DietPlan("Greek yogurt with mixed berries and granola", "Quinoa salad with chickpeas, cucumber, and olive oil", "Grilled turkey breast with roasted sweet potatoes and spinach", "Carrot sticks with hummus", "Drink at least 2 liters of water daily", "Normal"),
        DietPlan("Scrambled eggs with spinach and whole-grain toast", "Grilled chicken Caesar salad with light dressing", "Baked cod with steamed asparagus and brown rice", "A small handful of mixed nuts", "Drink at least 2 liters of water daily", "Normal"),
        DietPlan("Eggs and avocado on whole-grain toast", "Salmon with quinoa and roasted vegetables", "Chicken breast with steamed broccoli and sweet potatoes", "Apple slices with peanut butter", "Drink at least 2 liters of water daily", "Normal"),
        DietPlan("Greek yogurt with honey and chia seeds", "Chicken salad with mixed greens and vinaigrette", "Grilled shrimp with zucchini noodles", "Trail mix or a protein bar", "Drink at least 2 liters of water daily", "Normal")
    )

    val overweightPlans = listOf(
        DietPlan("Scrambled eggs with spinach and whole-grain toast", "Grilled turkey with quinoa and steamed vegetables", "Baked cod with steamed asparagus and a small portion of brown rice", "Celery with hummus", "Drink at least 2 liters of water daily", "Overweight"),
        DietPlan("Greek yogurt with chia seeds and mixed berries", "Grilled chicken with mixed greens salad and vinaigrette", "Baked salmon with roasted Brussels sprouts", "Sliced cucumber with guacamole", "Drink at least 2 liters of water daily", "Overweight"),
        DietPlan("Avocado toast with poached eggs", "Lentil soup with a side of roasted vegetables", "Grilled shrimp with zucchini noodles and cherry tomatoes", "Carrot sticks with hummus", "Drink at least 2 liters of water daily", "Overweight"),
        DietPlan("Greek yogurt with flaxseeds and honey", "Chicken with a quinoa and spinach salad", "Grilled salmon with steamed broccoli", "Almonds or Greek yogurt", "Drink at least 2 liters of water daily", "Overweight"),
        DietPlan("Vegetable omelet with whole-wheat toast", "Tuna salad with a side of cucumbers and tomatoes", "Baked chicken with steamed asparagus", "Cucumber with hummus", "Drink at least 2 liters of water daily", "Overweight")
    )

    val obesityPlans = listOf(
        DietPlan("Greek yogurt with chia seeds and berries", "Grilled chicken breast with a large green salad (no dressing)", "Grilled salmon with roasted cauliflower and sautéed spinach", "Cucumber slices with hummus", "Drink at least 2 liters of water daily", "Obesity"),
        DietPlan("Scrambled eggs with spinach and mushrooms", "Grilled turkey with roasted vegetables and quinoa", "Zucchini noodles with grilled chicken and a small serving of tomato sauce", "Celery with almond butter", "Drink at least 2 liters of water daily", "Obesity"),
        DietPlan("Avocado and egg on whole-grain toast", "Tuna salad with leafy greens, tomatoes, and olive oil", "Baked cod with steamed broccoli and cauliflower rice", "Greek yogurt with flax seeds", "Drink at least 2 liters of water daily", "Obesity"),
        DietPlan("Vegetable stir-fry with tofu", "Grilled chicken with sautéed spinach and a side of quinoa", "Baked salmon with steamed broccoli and cauliflower rice", "Hard-boiled eggs with carrot sticks", "Drink at least 2 liters of water daily", "Obesity"),
        DietPlan("Oats with chia seeds and blueberries", "Grilled turkey with a side of sautéed mushrooms and green beans", "Grilled shrimp with roasted vegetables", "Almonds or Greek yogurt", "Drink at least 2 liters of water daily", "Obesity")
    )

    // Save all plans to Firestore as separate documents
    val allPlans = underweightPlans + normalPlans + overweightPlans + obesityPlans

    // Loop through all plans and add each as a separate document
    for ((index, plan) in allPlans.withIndex()) {
        val planId = "plan${index + 1}" // ID will be plan1, plan2, etc.
        firestore.collection("dietPlans")
            .document(planId)
            .set(plan)
            .addOnSuccessListener {
                Log.d("Firestore", "Diet plan saved successfully: $planId")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error saving diet plan: $e")
            }
    }
}


