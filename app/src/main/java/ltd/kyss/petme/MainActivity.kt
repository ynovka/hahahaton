package ltd.kyss.petme

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.runtime.saveable.rememberSaveable
import ltd.kyss.petme.core.model.GameLocation
import ltd.kyss.petme.core.persistence.GameStateStorage
import ltd.kyss.petme.ui.screens.*
import ltd.kyss.petme.ui.theme.PetMeTheme
import ltd.kyss.petme.ui.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {

    private val gameViewModel by lazy { GameViewModel(GameStateStorage(applicationContext)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PetMeTheme {
                PetMeGameApp(viewModel = gameViewModel)
            }
        }
    }
}

@Composable
fun PetMeGameApp(viewModel: GameViewModel) {
    val state by viewModel.state.collectAsState()
    var showTitleScreen by rememberSaveable { mutableStateOf(true) }

    BackHandler(
        enabled = !showTitleScreen && state.isGameStarted && state.currentLocation != GameLocation.MyRoom
    ) {
        viewModel.changeLocation(
            if (state.currentLocation == GameLocation.CityMap) GameLocation.MyRoom
            else GameLocation.CityMap
        )
    }

    var showBudgetDialog by remember { mutableStateOf(false) }
    var showShopDialog by remember { mutableStateOf(false) }
    var showBankDialog by remember { mutableStateOf(false) }
    var showHospitalDialog by remember { mutableStateOf(false) }
    var showFinishPeriodDialog by remember { mutableStateOf(false) }
    var showWardrobeDialog by remember { mutableStateOf(false) }
    var showCareDialog by remember { mutableStateOf(false) }
    var showAdvisorDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showFinanceDialog by remember { mutableStateOf(false) }
    var activePuzzleFriendId by remember { mutableStateOf<Int?>(null) }
    var activeFriendDialogueId by remember { mutableStateOf<Int?>(null) }
    var activeFriendProfileId by remember { mutableStateOf<Int?>(null) }

    val currentDensity = LocalDensity.current
    CompositionLocalProvider(
        LocalDensity provides Density(
            density = currentDensity.density,
            fontScale = if (state.largeFontEnabled) 1.15f else 1f
        )
    ) {
    Box(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
        if (showTitleScreen) {
            TitleScreen(
                state = state,
                onContinue = { showTitleScreen = false },
                onNewStory = {
                    viewModel.startFreshGame()
                    showTitleScreen = false
                }
            )
        } else if (!state.isGameStarted) {
            PetSetupScreen(onStartGame = viewModel::startNewGame)
        } else when (state.currentLocation) {
            GameLocation.CityMap -> {
                CityMapScreen(
                    state = state,
                    onSelectLocation = { newLoc -> viewModel.changeLocation(newLoc) },
                    onOpenFriendProfile = { friendId -> activeFriendProfileId = friendId },
                    onOpenFriendDialogue = { friendId -> activeFriendDialogueId = friendId },
                    onOpenWardrobe = { showWardrobeDialog = true },
                    onOpenBudget = { showBudgetDialog = true },
                    onOpenSettings = { showSettingsDialog = true }
                )
            }
            else -> {
                RoomScreen(
                    state = state,
                    viewModel = viewModel,
                    onOpenBudget = { showBudgetDialog = true },
                    onOpenShop = { showShopDialog = true },
                    onOpenBank = { showBankDialog = true },
                    onOpenHospital = { showHospitalDialog = true },
                    onOpenWardrobe = { showWardrobeDialog = true },
                    onOpenCare = { showCareDialog = true },
                    onOpenAdvisor = { showAdvisorDialog = true },
                    onOpenSettings = { showSettingsDialog = true },
                    onOpenFinance = { showFinanceDialog = true },
                    onFinishPeriod = { showFinishPeriodDialog = true },
                    onOpenFriendDialogue = { friendId -> activeFriendDialogueId = friendId },
                    onOpenFriendProfile = { friendId -> activeFriendProfileId = friendId }
                )
            }
        }

        // Окно раскладывания монет по сундучкам (Детский Бюджет)
        if (showBudgetDialog) {
            KidBudgetScreen(
                state = state,
                viewModel = viewModel,
                onClose = { showBudgetDialog = false },
                onPlanConfirmed = {
                    showBudgetDialog = false
                    showAdvisorDialog = true
                }
            )
        }

        // Лавка покупок
        if (showShopDialog) {
            ShopDialog(
                state = state,
                viewModel = viewModel,
                onDismiss = { showShopDialog = false }
            )
        }

        if (showBankDialog) {
            BankDialog(state, viewModel, onDismiss = { showBankDialog = false })
        }

        if (showHospitalDialog) {
            HospitalDialog(state, viewModel, onDismiss = { showHospitalDialog = false })
        }

        if (showFinishPeriodDialog) {
            PeriodFinishDialog(state, viewModel, onDismiss = { showFinishPeriodDialog = false })
        }

        if (showCareDialog) {
            PetCareDialog(state, viewModel, onDismiss = { showCareDialog = false })
        }

        if (showAdvisorDialog) {
            AdvisorLessonsDialog(state, viewModel, onDismiss = { showAdvisorDialog = false })
        }

        if (showSettingsDialog) {
            SettingsDialog(state, viewModel, onDismiss = { showSettingsDialog = false })
        }

        if (showFinanceDialog) {
            FinanceHistoryDialog(state, onDismiss = { showFinanceDialog = false })
        }

        // Гардероб питомца
        if (showWardrobeDialog) {
            WardrobeDialog(
                state = state,
                viewModel = viewModel,
                onDismiss = { showWardrobeDialog = false }
            )
        }

        // Интерактивная головоломка друга
        activePuzzleFriendId?.let { friendId ->
            KidPuzzleDialog(
                friendId = friendId,
                state = state,
                viewModel = viewModel,
                onDismiss = { activePuzzleFriendId = null }
            )
        }

        activeFriendDialogueId?.let { friendId ->
            FriendDialogueDialog(
                friendId = friendId,
                state = state,
                onStartPuzzle = {
                    activeFriendDialogueId = null
                    activePuzzleFriendId = friendId
                },
                onOpenLessons = {
                    activeFriendDialogueId = null
                    showAdvisorDialog = true
                },
                onDismiss = { activeFriendDialogueId = null }
            )
        }

        // Карточка дружбы в стиле Hello Kitty
        activeFriendProfileId?.let { friendId ->
            FriendProfileDialog(
                initialFriendId = friendId,
                state = state,
                onStartPuzzle = { puzzleFriendId ->
                    activeFriendProfileId = null
                    activePuzzleFriendId = puzzleFriendId
                },
                onOpenLessons = {
                    activeFriendProfileId = null
                    showAdvisorDialog = true
                },
                onDismiss = { activeFriendProfileId = null }
            )
        }
    }
    }
}
