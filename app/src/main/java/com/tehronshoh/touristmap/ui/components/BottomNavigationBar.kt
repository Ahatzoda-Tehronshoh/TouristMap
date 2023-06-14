package com.tehronshoh.touristmap.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.tehronshoh.touristmap.R
import com.tehronshoh.touristmap.ui.navigation.Screen
import com.tehronshoh.touristmap.ui.screens.home.HomeScreen

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    BottomNavigation(
        backgroundColor = Color.Transparent,
        contentColor = Color.Transparent,
        elevation = 0.dp,
        modifier = Modifier.padding(start = 30.dp, end = 30.dp, bottom = 8.dp)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()

        val currentRoute = navBackStackEntry?.destination?.route

        HomeScreen.BottomNavItems.forEach { navItem ->
            val selected = (currentRoute == navItem.route || navItem.route == "nested_main")

            val labelColor = if (selected)
                colorResource(id = R.color.primary)
            else
                colorResource(id = R.color.inactive_color)

            val icon = if (selected)
                navItem.activeIcon
            else
                navItem.inactiveIcon

            val padding = if(navItem.route == "nested_main")
                PaddingValues(end = 15.dp)
            else
                PaddingValues(start = 15.dp)

            val modifier = if (selected)
                Modifier
                    .padding(padding)
                    .clip(RoundedCornerShape(10.dp))
                    .border(
                        width = 1.5.dp,
                        color = colorResource(id = R.color.primary),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .background(color = colorResource(id = R.color.primary_100))
                    .height(40.dp)
            else
                Modifier
                    .padding(padding)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color = colorResource(id = R.color.primary_100))
                    .height(40.dp)


            BottomNavigationItem(
                selected = selected,
                onClick = {
                    navController.navigate(navItem.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }

                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true

                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                },
                icon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            imageVector = ImageVector.vectorResource(id = icon),
                            contentDescription = stringResource(id = navItem.label)
                        )
                        Divider(modifier = Modifier.width(10.dp))
                        Text(
                            text =  stringResource(navItem.label),
                            fontFamily = FontFamily(Font(resId = R.font.montserrat_600)),
                            fontSize = 12.sp,
                            style = TextStyle(color = labelColor, fontWeight = FontWeight.W500)
                        )
                    }
                },
                modifier = modifier
            )
        }
    }
}