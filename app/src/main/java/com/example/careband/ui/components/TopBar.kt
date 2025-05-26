package com.example.careband.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.careband.R
import com.example.careband.data.model.UserType
import androidx.compose.material3.DrawerState
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CareBandTopBar(
    isLoggedIn: Boolean,
    userType: UserType?,
    drawerState: DrawerState,
    scope: CoroutineScope,
    onMenuClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    TopAppBar(
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.splash_icon),
                    contentDescription = "CareBand Logo",
                    modifier = Modifier.height(28.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
        },
        navigationIcon = {
            if (isLoggedIn) {
                IconButton(onClick = {
                    scope.launch { drawerState.open() }
                    onMenuClick()
                }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu Icon",
                        tint = Color.Black
                    )
                }
            }
        },
        actions = {
            if (isLoggedIn) {
                IconButton(onClick = onProfileClick) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile Icon",
                        tint = Color.Black
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFFFFF0F0), // 연한 핑크
            titleContentColor = Color.White
        )
    )
}
