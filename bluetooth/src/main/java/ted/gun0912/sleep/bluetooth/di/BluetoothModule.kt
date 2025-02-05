package ted.gun0912.sleep.bluetooth.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ted.gun0912.sleep.bluetooth.BluetoothDeviceDeviceManagerImpl
import ted.gun0912.sleep.component.BluetoothDeviceManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class BluetoothModule {

    @Binds
    @Singleton
    abstract fun bindBleManager(repo: BluetoothDeviceDeviceManagerImpl): BluetoothDeviceManager

}
