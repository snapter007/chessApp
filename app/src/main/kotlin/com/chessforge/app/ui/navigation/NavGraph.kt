package com.chessforge.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.chessforge.app.ui.home.HomeScreen
import com.chessforge.app.ui.list.CategoryListScreen
import com.chessforge.app.ui.list.ListCategory
import com.chessforge.app.ui.trainer.TrainerContentType
import com.chessforge.app.ui.trainer.TrainerScreen

private const val ROUTE_HOME = "home"
private const val ROUTE_LIST = "list/{category}"
private const val ROUTE_TRAINER = "trainer/{type}/{id}"

@Composable
fun ChessForgeNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = ROUTE_HOME) {
        composable(ROUTE_HOME) {
            HomeScreen(
                onOpenOpenings = { navController.navigate("list/opening") },
                onOpenTraps = { navController.navigate("list/trap") },
                onOpenTactics = { navController.navigate("list/tactic") },
            )
        }
        composable(
            route = ROUTE_LIST,
            arguments = listOf(navArgument("category") { type = NavType.StringType }),
        ) { backStackEntry ->
            val categoryArg = backStackEntry.arguments?.getString("category") ?: "opening"
            val category = when (categoryArg) {
                "trap" -> ListCategory.TRAP
                "tactic" -> ListCategory.TACTIC
                else -> ListCategory.OPENING
            }
            val trainerType = if (category == ListCategory.TACTIC) "tactic" else "opening"
            CategoryListScreen(
                category = category,
                onBack = { navController.popBackStack() },
                onOpenItem = { id -> navController.navigate("trainer/$trainerType/$id") },
            )
        }
        composable(
            route = ROUTE_TRAINER,
            arguments = listOf(
                navArgument("type") { type = NavType.StringType },
                navArgument("id") { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val typeArg = backStackEntry.arguments?.getString("type") ?: "opening"
            val id = backStackEntry.arguments?.getString("id") ?: ""
            val contentType = if (typeArg == "tactic") TrainerContentType.TACTIC else TrainerContentType.OPENING
            TrainerScreen(
                contentType = contentType,
                itemId = id,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
