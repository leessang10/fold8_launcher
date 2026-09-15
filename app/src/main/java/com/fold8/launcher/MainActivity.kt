package com.fold8.launcher

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.fold8.launcher.ui.LauncherApp
import com.fold8.launcher.ui.LauncherViewModel

/**
 * 갤럭시 Z 폴드8 런처 메인 액티비티
 * HOME 및 DEFAULT 카테고리 인텐트를 수신하며,
 * 화면이 접히고 펼쳐질 때(Fold/Unfold) 액티비티 재생성 없이 원활하게 반응함
 */
class MainActivity : ComponentActivity() {

    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LauncherApp(viewModel = viewModel)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // 홈 버튼을 다시 누르면 앱 서랍을 닫고 기본 홈으로 복귀
        if (Intent.ACTION_MAIN == intent.action && intent.hasCategory(Intent.CATEGORY_HOME)) {
            viewModel.setAppDrawerOpen(false)
            viewModel.setSearchQuery("")
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.appRepository.refreshApps()
    }
}
