package me.rerere.awara.ui.page.index.layout

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import me.rerere.awara.R
import me.rerere.awara.ui.LocalRouterProvider
import me.rerere.awara.ui.component.iwara.Avatar
import me.rerere.awara.ui.page.index.IndexDrawer
import me.rerere.awara.ui.page.index.IndexVM
import me.rerere.awara.ui.page.index.indexNavigations
import me.rerere.awara.ui.stores.LocalUserStore
import me.rerere.awara.ui.stores.collectAsState

@Composable
fun IndexPagePhoneLayout(vm: IndexVM) {
    val userStore = LocalUserStore.current
    val userState = userStore.collectAsState()
    val navController = LocalRouterProvider.current
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                IndexDrawer()
            }
        },
        drawerState = drawerState,
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = userState.user?.name ?: stringResource(R.string.app_name)
                        )
                    },
                    navigationIcon = {
                        Avatar(
                            user = userState.user,
                            onClick = {
                                scope.launch { drawerState.open() }
                            }
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                navController.navigate("search")
                            }
                        ) {
                            Icon(Icons.Outlined.Search, "Search")
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    indexNavigations.forEachIndexed { index, indexNavigation ->
                        NavigationBarItem(
                            icon = {
                                indexNavigation.icon()
                            },
                            label = {
                                indexNavigation.title()
                            },
                            selected = pagerState.currentPage == index,
                            onClick = {
                                scope.launch { pagerState.animateScrollToPage(index) }
                            }
                        )
                    }
                }
            },
        ) { innerPadding ->
            HorizontalPager(
                pageCount = indexNavigations.size,
                state = pagerState,
                modifier = Modifier.padding(innerPadding)
            ) { page ->
                when (page) {
                    0 -> {
                        // TODO
                    }

                    1 -> {
                        // TODO
                    }

                    2 -> {
                        // TODO
                    }

                    3 -> {
                        // TODO
                    }
                }
            }
        }
    }
}