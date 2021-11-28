package io.vextabit.wallet.core

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager
import io.horizontalsystems.coinkit.CoinKit
import io.horizontalsystems.core.BackgroundManager
import io.horizontalsystems.core.CoreApp
import io.horizontalsystems.core.ICoreApp
import io.horizontalsystems.core.security.EncryptionManager
import io.horizontalsystems.core.security.KeyStoreManager
import io.horizontalsystems.ethereumkit.core.EthereumKit
import io.horizontalsystems.pin.PinComponent
import io.reactivex.plugins.RxJavaPlugins
import io.vextabit.wallet.BuildConfig
import io.vextabit.wallet.core.factories.AccountFactory
import io.vextabit.wallet.core.factories.AdapterFactory
import io.vextabit.wallet.core.factories.AddressParserFactory
import io.vextabit.wallet.core.managers.*
import io.vextabit.wallet.core.notifications.NotificationNetworkWrapper
import io.vextabit.wallet.core.notifications.NotificationWorker
import io.vextabit.wallet.core.providers.AppConfigProvider
import io.vextabit.wallet.core.providers.FeeCoinProvider
import io.vextabit.wallet.core.providers.FeeRateProvider
import io.vextabit.wallet.core.storage.*
import io.vextabit.wallet.modules.keystore.KeyStoreActivity
import io.vextabit.wallet.modules.launcher.LauncherActivity
import io.vextabit.wallet.modules.lockscreen.LockScreenActivity
import io.vextabit.wallet.modules.settings.theme.ThemeType
import io.vextabit.wallet.modules.tor.TorConnectionActivity
import io.vextabit.wallet.modules.walletconnect.WalletConnectManager
import io.vextabit.wallet.modules.walletconnect.WalletConnectRequestManager
import io.vextabit.wallet.modules.walletconnect.WalletConnectSessionManager
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.system.exitProcess

class App : CoreApp() {

    companion object : ICoreApp by CoreApp {

        lateinit var feeRateProvider: FeeRateProvider
        lateinit var localStorage: ILocalStorage
        lateinit var marketStorage: IMarketStorage
        lateinit var torKitManager: ITorManager
        lateinit var chartTypeStorage: IChartTypeStorage
        lateinit var restoreSettingsStorage: IRestoreSettingsStorage

        lateinit var wordsManager: WordsManager
        lateinit var networkManager: INetworkManager
        lateinit var backgroundStateChangeListener: BackgroundStateChangeListener
        lateinit var appConfigProvider: IAppConfigProvider
        lateinit var adapterManager: IAdapterManager
        lateinit var walletManager: io.vextabit.wallet.core.IWalletManager
        lateinit var walletStorage: io.vextabit.wallet.core.IWalletStorage
        lateinit var accountManager: io.vextabit.wallet.core.IAccountManager
        lateinit var accountFactory: io.vextabit.wallet.core.IAccountFactory
        lateinit var backupManager: io.vextabit.wallet.core.IBackupManager

        lateinit var xRateManager: io.vextabit.wallet.core.IRateManager
        lateinit var connectivityManager: ConnectivityManager
        lateinit var appDatabase: AppDatabase
        lateinit var accountsStorage: io.vextabit.wallet.core.IAccountsStorage
        lateinit var priceAlertManager: io.vextabit.wallet.core.IPriceAlertManager
        lateinit var enabledWalletsStorage: io.vextabit.wallet.core.IEnabledWalletStorage
        lateinit var blockchainSettingsStorage: io.vextabit.wallet.core.IBlockchainSettingsStorage
        lateinit var ethereumKitManager: EvmKitManager
        lateinit var binanceSmartChainKitManager: EvmKitManager
        lateinit var binanceKitManager: BinanceKitManager
        lateinit var numberFormatter: io.vextabit.wallet.core.IAppNumberFormatter
        lateinit var addressParserFactory: AddressParserFactory
        lateinit var feeCoinProvider: FeeCoinProvider
        lateinit var notificationNetworkWrapper: NotificationNetworkWrapper
        lateinit var notificationManager: io.vextabit.wallet.core.INotificationManager
        lateinit var ethereumRpcModeSettingsManager: io.vextabit.wallet.core.IEthereumRpcModeSettingsManager
        lateinit var initialSyncModeSettingsManager: io.vextabit.wallet.core.IInitialSyncModeSettingsManager
        lateinit var derivationSettingsManager: io.vextabit.wallet.core.IDerivationSettingsManager
        lateinit var bitcoinCashCoinTypeManager: BitcoinCashCoinTypeManager
        lateinit var accountCleaner: io.vextabit.wallet.core.IAccountCleaner
        lateinit var rateAppManager: io.vextabit.wallet.core.IRateAppManager
        lateinit var coinManager: io.vextabit.wallet.core.ICoinManager
        lateinit var walletConnectSessionStorage: WalletConnectSessionStorage
        lateinit var walletConnectSessionManager: WalletConnectSessionManager
        lateinit var walletConnectRequestManager: WalletConnectRequestManager
        lateinit var walletConnectManager: WalletConnectManager
        lateinit var notificationSubscriptionManager: io.vextabit.wallet.core.INotificationSubscriptionManager
        lateinit var termsManager: io.vextabit.wallet.core.ITermsManager
        lateinit var marketFavoritesManager: MarketFavoritesManager
        lateinit var coinKit: CoinKit
        lateinit var activateCoinManager: ActivateCoinManager
        lateinit var releaseNotesManager: ReleaseNotesManager
        lateinit var restoreSettingsManager: RestoreSettingsManager
        lateinit var evmNetworkManager: EvmNetworkManager
        lateinit var accountSettingManager: AccountSettingManager
    }

    override fun onCreate() {
        super.onCreate()

        if (!BuildConfig.DEBUG) {
            //Disable logging for lower levels in Release build
            Logger.getLogger("").level = Level.SEVERE
        }

        RxJavaPlugins.setErrorHandler { e: Throwable? ->
            Log.w("RxJava ErrorHandler", e)
        }

        EthereumKit.init()

        instance = this
        preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        val appConfig = AppConfigProvider()
        io.vextabit.wallet.core.App.Companion.appConfigProvider = appConfig
        buildConfigProvider = appConfig
        languageConfigProvider = appConfig

        io.vextabit.wallet.core.App.Companion.coinKit = CoinKit.create(this, buildConfigProvider.testMode)

        io.vextabit.wallet.core.App.Companion.feeRateProvider = FeeRateProvider(io.vextabit.wallet.core.App.Companion.appConfigProvider)
        backgroundManager = BackgroundManager(this)

        io.vextabit.wallet.core.App.Companion.appDatabase = AppDatabase.getInstance(this)

        io.vextabit.wallet.core.App.Companion.evmNetworkManager = EvmNetworkManager(io.vextabit.wallet.core.App.Companion.appConfigProvider)
        io.vextabit.wallet.core.App.Companion.accountSettingManager = AccountSettingManager(AccountSettingRecordStorage(io.vextabit.wallet.core.App.Companion.appDatabase), io.vextabit.wallet.core.App.Companion.evmNetworkManager)

        io.vextabit.wallet.core.App.Companion.ethereumKitManager = EvmKitManager(appConfig.etherscanApiKey, backgroundManager, EvmNetworkProviderEth(io.vextabit.wallet.core.App.Companion.accountSettingManager))
        io.vextabit.wallet.core.App.Companion.binanceSmartChainKitManager = EvmKitManager(appConfig.bscscanApiKey, backgroundManager, EvmNetworkProviderBsc(io.vextabit.wallet.core.App.Companion.accountSettingManager))
        io.vextabit.wallet.core.App.Companion.binanceKitManager = BinanceKitManager(buildConfigProvider.testMode)

        io.vextabit.wallet.core.App.Companion.accountsStorage = AccountsStorage(io.vextabit.wallet.core.App.Companion.appDatabase)
        io.vextabit.wallet.core.App.Companion.restoreSettingsStorage = RestoreSettingsStorage(io.vextabit.wallet.core.App.Companion.appDatabase)

        io.vextabit.wallet.core.AppLog.logsDao = io.vextabit.wallet.core.App.Companion.appDatabase.logsDao()

        io.vextabit.wallet.core.App.Companion.coinManager = CoinManager(io.vextabit.wallet.core.App.Companion.coinKit, io.vextabit.wallet.core.App.Companion.appConfigProvider)

        io.vextabit.wallet.core.App.Companion.enabledWalletsStorage = EnabledWalletsStorage(io.vextabit.wallet.core.App.Companion.appDatabase)
        io.vextabit.wallet.core.App.Companion.blockchainSettingsStorage = BlockchainSettingsStorage(io.vextabit.wallet.core.App.Companion.appDatabase)
        io.vextabit.wallet.core.App.Companion.walletStorage = WalletStorage(io.vextabit.wallet.core.App.Companion.coinManager, io.vextabit.wallet.core.App.Companion.enabledWalletsStorage)

        LocalStorageManager(preferences).apply {
            localStorage = this
            io.vextabit.wallet.core.App.Companion.chartTypeStorage = this
            pinStorage = this
            thirdKeyboardStorage = this
            io.vextabit.wallet.core.App.Companion.marketStorage = this
        }

        io.vextabit.wallet.core.App.Companion.torKitManager = TorManager(instance, localStorage)

        wordsManager = WordsManager()
        networkManager = NetworkManager()
        accountCleaner = AccountCleaner(buildConfigProvider.testMode)
        accountManager = AccountManager(io.vextabit.wallet.core.App.Companion.accountsStorage, accountCleaner)
        io.vextabit.wallet.core.App.Companion.accountFactory = AccountFactory(accountManager)
        io.vextabit.wallet.core.App.Companion.backupManager = BackupManager(accountManager)
        io.vextabit.wallet.core.App.Companion.walletManager = WalletManager(accountManager, io.vextabit.wallet.core.App.Companion.walletStorage)

        KeyStoreManager("MASTER_KEY", KeyStoreCleaner(localStorage, accountManager, io.vextabit.wallet.core.App.Companion.walletManager)).apply {
            keyStoreManager = this
            keyProvider = this
        }

        encryptionManager = EncryptionManager(keyProvider)

        systemInfoManager = SystemInfoManager()

        languageManager = LanguageManager()
        currencyManager = CurrencyManager(localStorage, io.vextabit.wallet.core.App.Companion.appConfigProvider)
        io.vextabit.wallet.core.App.Companion.numberFormatter = NumberFormatter(languageManager)

        io.vextabit.wallet.core.App.Companion.connectivityManager = ConnectivityManager(backgroundManager)

        val zcashBirthdayProvider = ZcashBirthdayProvider(this, buildConfigProvider.testMode)
        io.vextabit.wallet.core.App.Companion.restoreSettingsManager = RestoreSettingsManager(io.vextabit.wallet.core.App.Companion.restoreSettingsStorage, zcashBirthdayProvider)

        val adapterFactory = AdapterFactory(instance, buildConfigProvider.testMode, io.vextabit.wallet.core.App.Companion.ethereumKitManager, io.vextabit.wallet.core.App.Companion.binanceSmartChainKitManager, io.vextabit.wallet.core.App.Companion.binanceKitManager, backgroundManager, io.vextabit.wallet.core.App.Companion.restoreSettingsManager, io.vextabit.wallet.core.App.Companion.coinManager)
        io.vextabit.wallet.core.App.Companion.adapterManager = AdapterManager(io.vextabit.wallet.core.App.Companion.walletManager, adapterFactory, io.vextabit.wallet.core.App.Companion.ethereumKitManager, io.vextabit.wallet.core.App.Companion.binanceSmartChainKitManager, io.vextabit.wallet.core.App.Companion.binanceKitManager)

        io.vextabit.wallet.core.App.Companion.initialSyncModeSettingsManager = InitialSyncSettingsManager(io.vextabit.wallet.core.App.Companion.coinManager, io.vextabit.wallet.core.App.Companion.blockchainSettingsStorage, io.vextabit.wallet.core.App.Companion.adapterManager, io.vextabit.wallet.core.App.Companion.walletManager)
        io.vextabit.wallet.core.App.Companion.derivationSettingsManager = DerivationSettingsManager(io.vextabit.wallet.core.App.Companion.blockchainSettingsStorage, io.vextabit.wallet.core.App.Companion.adapterManager, io.vextabit.wallet.core.App.Companion.walletManager)
        io.vextabit.wallet.core.App.Companion.ethereumRpcModeSettingsManager = EthereumRpcModeSettingsManager(io.vextabit.wallet.core.App.Companion.blockchainSettingsStorage, io.vextabit.wallet.core.App.Companion.adapterManager, io.vextabit.wallet.core.App.Companion.walletManager)
        io.vextabit.wallet.core.App.Companion.bitcoinCashCoinTypeManager = BitcoinCashCoinTypeManager(io.vextabit.wallet.core.App.Companion.walletManager, io.vextabit.wallet.core.App.Companion.adapterManager, io.vextabit.wallet.core.App.Companion.blockchainSettingsStorage)

        adapterFactory.initialSyncModeSettingsManager = io.vextabit.wallet.core.App.Companion.initialSyncModeSettingsManager
        adapterFactory.ethereumRpcModeSettingsManager = io.vextabit.wallet.core.App.Companion.ethereumRpcModeSettingsManager

        io.vextabit.wallet.core.App.Companion.feeCoinProvider = FeeCoinProvider(io.vextabit.wallet.core.App.Companion.coinKit)
        io.vextabit.wallet.core.App.Companion.xRateManager = RateManager(this, io.vextabit.wallet.core.App.Companion.appConfigProvider)

        io.vextabit.wallet.core.App.Companion.addressParserFactory = AddressParserFactory()

        io.vextabit.wallet.core.App.Companion.notificationNetworkWrapper = NotificationNetworkWrapper(localStorage, networkManager, io.vextabit.wallet.core.App.Companion.appConfigProvider)
        io.vextabit.wallet.core.App.Companion.notificationManager = NotificationManager(NotificationManagerCompat.from(this), localStorage).apply {
            backgroundManager.registerListener(this)
        }
        io.vextabit.wallet.core.App.Companion.notificationSubscriptionManager = NotificationSubscriptionManager(io.vextabit.wallet.core.App.Companion.appDatabase, io.vextabit.wallet.core.App.Companion.notificationNetworkWrapper)
        io.vextabit.wallet.core.App.Companion.priceAlertManager = PriceAlertManager(io.vextabit.wallet.core.App.Companion.appDatabase, io.vextabit.wallet.core.App.Companion.notificationSubscriptionManager, io.vextabit.wallet.core.App.Companion.notificationManager, localStorage, io.vextabit.wallet.core.App.Companion.notificationNetworkWrapper, backgroundManager)

        pinComponent = PinComponent(
                pinStorage = pinStorage,
                encryptionManager = encryptionManager,
                excludedActivityNames = listOf(
                        KeyStoreActivity::class.java.name,
                        LockScreenActivity::class.java.name,
                        LauncherActivity::class.java.name,
                        TorConnectionActivity::class.java.name
                )
        )

        io.vextabit.wallet.core.App.Companion.backgroundStateChangeListener = BackgroundStateChangeListener(systemInfoManager, keyStoreManager, pinComponent).apply {
            backgroundManager.registerListener(this)
        }

        io.vextabit.wallet.core.App.Companion.rateAppManager = RateAppManager(io.vextabit.wallet.core.App.Companion.walletManager, io.vextabit.wallet.core.App.Companion.adapterManager, localStorage)
        io.vextabit.wallet.core.App.Companion.walletConnectSessionStorage = WalletConnectSessionStorage(io.vextabit.wallet.core.App.Companion.appDatabase)
        io.vextabit.wallet.core.App.Companion.walletConnectSessionManager = WalletConnectSessionManager(io.vextabit.wallet.core.App.Companion.walletConnectSessionStorage, accountManager, io.vextabit.wallet.core.App.Companion.accountSettingManager)
        io.vextabit.wallet.core.App.Companion.walletConnectRequestManager = WalletConnectRequestManager()
        io.vextabit.wallet.core.App.Companion.walletConnectManager = WalletConnectManager(accountManager, io.vextabit.wallet.core.App.Companion.ethereumKitManager, io.vextabit.wallet.core.App.Companion.binanceSmartChainKitManager)

        io.vextabit.wallet.core.App.Companion.termsManager = TermsManager(localStorage)

        io.vextabit.wallet.core.App.Companion.marketFavoritesManager = MarketFavoritesManager(io.vextabit.wallet.core.App.Companion.appDatabase)

        io.vextabit.wallet.core.App.Companion.activateCoinManager = ActivateCoinManager(io.vextabit.wallet.core.App.Companion.coinKit, io.vextabit.wallet.core.App.Companion.walletManager, accountManager)

        io.vextabit.wallet.core.App.Companion.releaseNotesManager = ReleaseNotesManager(systemInfoManager, localStorage, io.vextabit.wallet.core.App.Companion.appConfigProvider)

        setAppTheme()

        registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks(io.vextabit.wallet.core.App.Companion.torKitManager))

        startTasks()

        NotificationWorker.startPeriodicWorker(instance)
    }

    private fun setAppTheme() {
        val nightMode = when (localStorage.currentTheme) {
            ThemeType.Light -> AppCompatDelegate.MODE_NIGHT_NO
            ThemeType.Dark -> AppCompatDelegate.MODE_NIGHT_YES
            ThemeType.System -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

        if (AppCompatDelegate.getDefaultNightMode() != nightMode) {
            AppCompatDelegate.setDefaultNightMode(nightMode)
        }
    }

    override fun onTrimMemory(level: Int) {
        when (level) {
            TRIM_MEMORY_BACKGROUND,
            TRIM_MEMORY_MODERATE,
            TRIM_MEMORY_COMPLETE -> {
                /*
                   Release as much memory as the process can.

                   The app is on the LRU list and the system is running low on memory.
                   The event raised indicates where the app sits within the LRU list.
                   If the event is TRIM_MEMORY_COMPLETE, the process will be one of
                   the first to be terminated.
                */
                if (backgroundManager.inBackground) {
                    val logger = io.vextabit.wallet.core.AppLogger("low memory")
                    logger.info("Kill app due to low memory, level: $level")
                    exitProcess(0)
                }
            }
            else -> {  /*do nothing*/
            }
        }
        super.onTrimMemory(level)
    }

    override fun localizedContext(): Context {
        return localeAwareContext(this)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(localeAwareContext(base))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        localeAwareContext(this)
    }

    private fun startTasks() {
        Thread(Runnable {
            io.vextabit.wallet.core.App.Companion.rateAppManager.onAppLaunch()
            accountManager.loadAccounts()
            io.vextabit.wallet.core.App.Companion.walletManager.loadWallets()
            io.vextabit.wallet.core.App.Companion.adapterManager.preloadAdapters()
            accountManager.clearAccounts()
            io.vextabit.wallet.core.App.Companion.notificationSubscriptionManager.processJobs()

            AppVersionManager(systemInfoManager, localStorage).apply { storeAppVersion() }

        }).start()
    }
}
