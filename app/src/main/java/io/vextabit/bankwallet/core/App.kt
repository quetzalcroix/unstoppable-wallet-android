package io.vextabit.bankwallet.core

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager
import io.horizontalsystems.bankwallet.BuildConfig
import io.horizontalsystems.bankwallet.core.factories.AccountFactory
import io.horizontalsystems.bankwallet.core.factories.AdapterFactory
import io.horizontalsystems.bankwallet.core.factories.AddressParserFactory
import io.horizontalsystems.bankwallet.core.managers.*
import io.horizontalsystems.bankwallet.core.notifications.NotificationNetworkWrapper
import io.horizontalsystems.bankwallet.core.notifications.NotificationWorker
import io.horizontalsystems.bankwallet.core.providers.AppConfigProvider
import io.horizontalsystems.bankwallet.core.providers.FeeCoinProvider
import io.horizontalsystems.bankwallet.core.providers.FeeRateProvider
import io.horizontalsystems.bankwallet.core.storage.*
import io.horizontalsystems.bankwallet.modules.keystore.KeyStoreActivity
import io.horizontalsystems.bankwallet.modules.launcher.LauncherActivity
import io.horizontalsystems.bankwallet.modules.lockscreen.LockScreenActivity
import io.horizontalsystems.bankwallet.modules.settings.theme.ThemeType
import io.horizontalsystems.bankwallet.modules.tor.TorConnectionActivity
import io.horizontalsystems.bankwallet.modules.walletconnect.WalletConnectManager
import io.horizontalsystems.bankwallet.modules.walletconnect.WalletConnectRequestManager
import io.horizontalsystems.bankwallet.modules.walletconnect.WalletConnectSessionManager
import io.horizontalsystems.coinkit.CoinKit
import io.horizontalsystems.core.BackgroundManager
import io.horizontalsystems.core.CoreApp
import io.horizontalsystems.core.ICoreApp
import io.horizontalsystems.core.security.EncryptionManager
import io.horizontalsystems.core.security.KeyStoreManager
import io.horizontalsystems.ethereumkit.core.EthereumKit
import io.horizontalsystems.pin.PinComponent
import io.reactivex.plugins.RxJavaPlugins
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.system.exitProcess

class App : CoreApp() {

    companion object : ICoreApp by CoreApp {

        lateinit var feeRateProvider: FeeRateProvider
        lateinit var localStorage: io.vextabit.bankwallet.core.ILocalStorage
        lateinit var marketStorage: io.vextabit.bankwallet.core.IMarketStorage
        lateinit var torKitManager: io.vextabit.bankwallet.core.ITorManager
        lateinit var chartTypeStorage: io.vextabit.bankwallet.core.IChartTypeStorage
        lateinit var restoreSettingsStorage: io.vextabit.bankwallet.core.IRestoreSettingsStorage

        lateinit var wordsManager: WordsManager
        lateinit var networkManager: io.vextabit.bankwallet.core.INetworkManager
        lateinit var backgroundStateChangeListener: BackgroundStateChangeListener
        lateinit var appConfigProvider: io.vextabit.bankwallet.core.IAppConfigProvider
        lateinit var adapterManager: io.vextabit.bankwallet.core.IAdapterManager
        lateinit var walletManager: io.vextabit.bankwallet.core.IWalletManager
        lateinit var walletStorage: io.vextabit.bankwallet.core.IWalletStorage
        lateinit var accountManager: io.vextabit.bankwallet.core.IAccountManager
        lateinit var accountFactory: io.vextabit.bankwallet.core.IAccountFactory
        lateinit var backupManager: io.vextabit.bankwallet.core.IBackupManager

        lateinit var xRateManager: io.vextabit.bankwallet.core.IRateManager
        lateinit var connectivityManager: ConnectivityManager
        lateinit var appDatabase: AppDatabase
        lateinit var accountsStorage: io.vextabit.bankwallet.core.IAccountsStorage
        lateinit var priceAlertManager: io.vextabit.bankwallet.core.IPriceAlertManager
        lateinit var enabledWalletsStorage: io.vextabit.bankwallet.core.IEnabledWalletStorage
        lateinit var blockchainSettingsStorage: io.vextabit.bankwallet.core.IBlockchainSettingsStorage
        lateinit var ethereumKitManager: EvmKitManager
        lateinit var binanceSmartChainKitManager: EvmKitManager
        lateinit var binanceKitManager: BinanceKitManager
        lateinit var numberFormatter: io.vextabit.bankwallet.core.IAppNumberFormatter
        lateinit var addressParserFactory: AddressParserFactory
        lateinit var feeCoinProvider: FeeCoinProvider
        lateinit var notificationNetworkWrapper: NotificationNetworkWrapper
        lateinit var notificationManager: io.vextabit.bankwallet.core.INotificationManager
        lateinit var ethereumRpcModeSettingsManager: io.vextabit.bankwallet.core.IEthereumRpcModeSettingsManager
        lateinit var initialSyncModeSettingsManager: io.vextabit.bankwallet.core.IInitialSyncModeSettingsManager
        lateinit var derivationSettingsManager: io.vextabit.bankwallet.core.IDerivationSettingsManager
        lateinit var bitcoinCashCoinTypeManager: BitcoinCashCoinTypeManager
        lateinit var accountCleaner: io.vextabit.bankwallet.core.IAccountCleaner
        lateinit var rateAppManager: io.vextabit.bankwallet.core.IRateAppManager
        lateinit var coinManager: io.vextabit.bankwallet.core.ICoinManager
        lateinit var walletConnectSessionStorage: WalletConnectSessionStorage
        lateinit var walletConnectSessionManager: WalletConnectSessionManager
        lateinit var walletConnectRequestManager: WalletConnectRequestManager
        lateinit var walletConnectManager: WalletConnectManager
        lateinit var notificationSubscriptionManager: io.vextabit.bankwallet.core.INotificationSubscriptionManager
        lateinit var termsManager: io.vextabit.bankwallet.core.ITermsManager
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
        io.vextabit.bankwallet.core.App.Companion.appConfigProvider = appConfig
        buildConfigProvider = appConfig
        languageConfigProvider = appConfig

        io.vextabit.bankwallet.core.App.Companion.coinKit = CoinKit.create(this, buildConfigProvider.testMode)

        io.vextabit.bankwallet.core.App.Companion.feeRateProvider = FeeRateProvider(io.vextabit.bankwallet.core.App.Companion.appConfigProvider)
        backgroundManager = BackgroundManager(this)

        io.vextabit.bankwallet.core.App.Companion.appDatabase = AppDatabase.getInstance(this)

        io.vextabit.bankwallet.core.App.Companion.evmNetworkManager = EvmNetworkManager(io.vextabit.bankwallet.core.App.Companion.appConfigProvider)
        io.vextabit.bankwallet.core.App.Companion.accountSettingManager = AccountSettingManager(AccountSettingRecordStorage(io.vextabit.bankwallet.core.App.Companion.appDatabase), io.vextabit.bankwallet.core.App.Companion.evmNetworkManager)

        io.vextabit.bankwallet.core.App.Companion.ethereumKitManager = EvmKitManager(appConfig.etherscanApiKey, backgroundManager, EvmNetworkProviderEth(io.vextabit.bankwallet.core.App.Companion.accountSettingManager))
        io.vextabit.bankwallet.core.App.Companion.binanceSmartChainKitManager = EvmKitManager(appConfig.bscscanApiKey, backgroundManager, EvmNetworkProviderBsc(io.vextabit.bankwallet.core.App.Companion.accountSettingManager))
        io.vextabit.bankwallet.core.App.Companion.binanceKitManager = BinanceKitManager(buildConfigProvider.testMode)

        io.vextabit.bankwallet.core.App.Companion.accountsStorage = AccountsStorage(io.vextabit.bankwallet.core.App.Companion.appDatabase)
        io.vextabit.bankwallet.core.App.Companion.restoreSettingsStorage = RestoreSettingsStorage(io.vextabit.bankwallet.core.App.Companion.appDatabase)

        io.vextabit.bankwallet.core.AppLog.logsDao = io.vextabit.bankwallet.core.App.Companion.appDatabase.logsDao()

        io.vextabit.bankwallet.core.App.Companion.coinManager = CoinManager(io.vextabit.bankwallet.core.App.Companion.coinKit, io.vextabit.bankwallet.core.App.Companion.appConfigProvider)

        io.vextabit.bankwallet.core.App.Companion.enabledWalletsStorage = EnabledWalletsStorage(io.vextabit.bankwallet.core.App.Companion.appDatabase)
        io.vextabit.bankwallet.core.App.Companion.blockchainSettingsStorage = BlockchainSettingsStorage(io.vextabit.bankwallet.core.App.Companion.appDatabase)
        io.vextabit.bankwallet.core.App.Companion.walletStorage = WalletStorage(io.vextabit.bankwallet.core.App.Companion.coinManager, io.vextabit.bankwallet.core.App.Companion.enabledWalletsStorage)

        LocalStorageManager(preferences).apply {
            io.vextabit.bankwallet.core.App.Companion.localStorage = this
            io.vextabit.bankwallet.core.App.Companion.chartTypeStorage = this
            pinStorage = this
            thirdKeyboardStorage = this
            io.vextabit.bankwallet.core.App.Companion.marketStorage = this
        }

        io.vextabit.bankwallet.core.App.Companion.torKitManager = TorManager(instance, io.vextabit.bankwallet.core.App.Companion.localStorage)

        io.vextabit.bankwallet.core.App.Companion.wordsManager = WordsManager()
        io.vextabit.bankwallet.core.App.Companion.networkManager = NetworkManager()
        io.vextabit.bankwallet.core.App.Companion.accountCleaner = AccountCleaner(buildConfigProvider.testMode)
        io.vextabit.bankwallet.core.App.Companion.accountManager = AccountManager(io.vextabit.bankwallet.core.App.Companion.accountsStorage, io.vextabit.bankwallet.core.App.Companion.accountCleaner)
        io.vextabit.bankwallet.core.App.Companion.accountFactory = AccountFactory(io.vextabit.bankwallet.core.App.Companion.accountManager)
        io.vextabit.bankwallet.core.App.Companion.backupManager = BackupManager(io.vextabit.bankwallet.core.App.Companion.accountManager)
        io.vextabit.bankwallet.core.App.Companion.walletManager = WalletManager(io.vextabit.bankwallet.core.App.Companion.accountManager, io.vextabit.bankwallet.core.App.Companion.walletStorage)

        KeyStoreManager("MASTER_KEY", KeyStoreCleaner(io.vextabit.bankwallet.core.App.Companion.localStorage, io.vextabit.bankwallet.core.App.Companion.accountManager, io.vextabit.bankwallet.core.App.Companion.walletManager)).apply {
            keyStoreManager = this
            keyProvider = this
        }

        encryptionManager = EncryptionManager(keyProvider)

        systemInfoManager = SystemInfoManager()

        languageManager = LanguageManager()
        currencyManager = CurrencyManager(io.vextabit.bankwallet.core.App.Companion.localStorage, io.vextabit.bankwallet.core.App.Companion.appConfigProvider)
        io.vextabit.bankwallet.core.App.Companion.numberFormatter = NumberFormatter(languageManager)

        io.vextabit.bankwallet.core.App.Companion.connectivityManager = ConnectivityManager(backgroundManager)

        val zcashBirthdayProvider = ZcashBirthdayProvider(this, buildConfigProvider.testMode)
        io.vextabit.bankwallet.core.App.Companion.restoreSettingsManager = RestoreSettingsManager(io.vextabit.bankwallet.core.App.Companion.restoreSettingsStorage, zcashBirthdayProvider)

        val adapterFactory = AdapterFactory(instance, buildConfigProvider.testMode, io.vextabit.bankwallet.core.App.Companion.ethereumKitManager, io.vextabit.bankwallet.core.App.Companion.binanceSmartChainKitManager, io.vextabit.bankwallet.core.App.Companion.binanceKitManager, backgroundManager, io.vextabit.bankwallet.core.App.Companion.restoreSettingsManager, io.vextabit.bankwallet.core.App.Companion.coinManager)
        io.vextabit.bankwallet.core.App.Companion.adapterManager = AdapterManager(io.vextabit.bankwallet.core.App.Companion.walletManager, adapterFactory, io.vextabit.bankwallet.core.App.Companion.ethereumKitManager, io.vextabit.bankwallet.core.App.Companion.binanceSmartChainKitManager, io.vextabit.bankwallet.core.App.Companion.binanceKitManager)

        io.vextabit.bankwallet.core.App.Companion.initialSyncModeSettingsManager = InitialSyncSettingsManager(io.vextabit.bankwallet.core.App.Companion.coinManager, io.vextabit.bankwallet.core.App.Companion.blockchainSettingsStorage, io.vextabit.bankwallet.core.App.Companion.adapterManager, io.vextabit.bankwallet.core.App.Companion.walletManager)
        io.vextabit.bankwallet.core.App.Companion.derivationSettingsManager = DerivationSettingsManager(io.vextabit.bankwallet.core.App.Companion.blockchainSettingsStorage, io.vextabit.bankwallet.core.App.Companion.adapterManager, io.vextabit.bankwallet.core.App.Companion.walletManager)
        io.vextabit.bankwallet.core.App.Companion.ethereumRpcModeSettingsManager = EthereumRpcModeSettingsManager(io.vextabit.bankwallet.core.App.Companion.blockchainSettingsStorage, io.vextabit.bankwallet.core.App.Companion.adapterManager, io.vextabit.bankwallet.core.App.Companion.walletManager)
        io.vextabit.bankwallet.core.App.Companion.bitcoinCashCoinTypeManager = BitcoinCashCoinTypeManager(io.vextabit.bankwallet.core.App.Companion.walletManager, io.vextabit.bankwallet.core.App.Companion.adapterManager, io.vextabit.bankwallet.core.App.Companion.blockchainSettingsStorage)

        adapterFactory.initialSyncModeSettingsManager = io.vextabit.bankwallet.core.App.Companion.initialSyncModeSettingsManager
        adapterFactory.ethereumRpcModeSettingsManager = io.vextabit.bankwallet.core.App.Companion.ethereumRpcModeSettingsManager

        io.vextabit.bankwallet.core.App.Companion.feeCoinProvider = FeeCoinProvider(io.vextabit.bankwallet.core.App.Companion.coinKit)
        io.vextabit.bankwallet.core.App.Companion.xRateManager = RateManager(this, io.vextabit.bankwallet.core.App.Companion.appConfigProvider)

        io.vextabit.bankwallet.core.App.Companion.addressParserFactory = AddressParserFactory()

        io.vextabit.bankwallet.core.App.Companion.notificationNetworkWrapper = NotificationNetworkWrapper(io.vextabit.bankwallet.core.App.Companion.localStorage, io.vextabit.bankwallet.core.App.Companion.networkManager, io.vextabit.bankwallet.core.App.Companion.appConfigProvider)
        io.vextabit.bankwallet.core.App.Companion.notificationManager = NotificationManager(NotificationManagerCompat.from(this), io.vextabit.bankwallet.core.App.Companion.localStorage).apply {
            backgroundManager.registerListener(this)
        }
        io.vextabit.bankwallet.core.App.Companion.notificationSubscriptionManager = NotificationSubscriptionManager(io.vextabit.bankwallet.core.App.Companion.appDatabase, io.vextabit.bankwallet.core.App.Companion.notificationNetworkWrapper)
        io.vextabit.bankwallet.core.App.Companion.priceAlertManager = PriceAlertManager(io.vextabit.bankwallet.core.App.Companion.appDatabase, io.vextabit.bankwallet.core.App.Companion.notificationSubscriptionManager, io.vextabit.bankwallet.core.App.Companion.notificationManager, io.vextabit.bankwallet.core.App.Companion.localStorage, io.vextabit.bankwallet.core.App.Companion.notificationNetworkWrapper, backgroundManager)

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

        io.vextabit.bankwallet.core.App.Companion.backgroundStateChangeListener = BackgroundStateChangeListener(systemInfoManager, keyStoreManager, pinComponent).apply {
            backgroundManager.registerListener(this)
        }

        io.vextabit.bankwallet.core.App.Companion.rateAppManager = RateAppManager(io.vextabit.bankwallet.core.App.Companion.walletManager, io.vextabit.bankwallet.core.App.Companion.adapterManager, io.vextabit.bankwallet.core.App.Companion.localStorage)
        io.vextabit.bankwallet.core.App.Companion.walletConnectSessionStorage = WalletConnectSessionStorage(io.vextabit.bankwallet.core.App.Companion.appDatabase)
        io.vextabit.bankwallet.core.App.Companion.walletConnectSessionManager = WalletConnectSessionManager(io.vextabit.bankwallet.core.App.Companion.walletConnectSessionStorage, io.vextabit.bankwallet.core.App.Companion.accountManager, io.vextabit.bankwallet.core.App.Companion.accountSettingManager)
        io.vextabit.bankwallet.core.App.Companion.walletConnectRequestManager = WalletConnectRequestManager()
        io.vextabit.bankwallet.core.App.Companion.walletConnectManager = WalletConnectManager(io.vextabit.bankwallet.core.App.Companion.accountManager, io.vextabit.bankwallet.core.App.Companion.ethereumKitManager, io.vextabit.bankwallet.core.App.Companion.binanceSmartChainKitManager)

        io.vextabit.bankwallet.core.App.Companion.termsManager = TermsManager(io.vextabit.bankwallet.core.App.Companion.localStorage)

        io.vextabit.bankwallet.core.App.Companion.marketFavoritesManager = MarketFavoritesManager(io.vextabit.bankwallet.core.App.Companion.appDatabase)

        io.vextabit.bankwallet.core.App.Companion.activateCoinManager = ActivateCoinManager(io.vextabit.bankwallet.core.App.Companion.coinKit, io.vextabit.bankwallet.core.App.Companion.walletManager, io.vextabit.bankwallet.core.App.Companion.accountManager)

        io.vextabit.bankwallet.core.App.Companion.releaseNotesManager = ReleaseNotesManager(systemInfoManager, io.vextabit.bankwallet.core.App.Companion.localStorage, io.vextabit.bankwallet.core.App.Companion.appConfigProvider)

        setAppTheme()

        registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks(io.vextabit.bankwallet.core.App.Companion.torKitManager))

        startTasks()

        NotificationWorker.startPeriodicWorker(instance)
    }

    private fun setAppTheme() {
        val nightMode = when (io.vextabit.bankwallet.core.App.Companion.localStorage.currentTheme) {
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
                    val logger = io.vextabit.bankwallet.core.AppLogger("low memory")
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
            io.vextabit.bankwallet.core.App.Companion.rateAppManager.onAppLaunch()
            io.vextabit.bankwallet.core.App.Companion.accountManager.loadAccounts()
            io.vextabit.bankwallet.core.App.Companion.walletManager.loadWallets()
            io.vextabit.bankwallet.core.App.Companion.adapterManager.preloadAdapters()
            io.vextabit.bankwallet.core.App.Companion.accountManager.clearAccounts()
            io.vextabit.bankwallet.core.App.Companion.notificationSubscriptionManager.processJobs()

            AppVersionManager(systemInfoManager, io.vextabit.bankwallet.core.App.Companion.localStorage).apply { storeAppVersion() }

        }).start()
    }
}
