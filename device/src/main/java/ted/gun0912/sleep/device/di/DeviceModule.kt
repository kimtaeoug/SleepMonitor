package ted.gun0912.sleep.device.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ted.gun0912.sleep.component.Device
import ted.gun0912.sleep.component.Haptic
import ted.gun0912.sleep.component.Toaster
import ted.gun0912.sleep.component.VersionName
import ted.gun0912.sleep.device.extension.getVersionName
import ted.gun0912.sleep.device.impl.DeviceImpl
import ted.gun0912.sleep.device.impl.HapticImpl
import ted.gun0912.sleep.device.impl.ToasterImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DeviceModule {

    @Binds
    @Singleton
    abstract fun bindToaster(toaster: ToasterImpl): Toaster

    @Binds
    @Singleton
    abstract fun bindHaptic(haptic: HapticImpl): Haptic

    @Binds
    @Singleton
    abstract fun bindDevice(device: DeviceImpl): Device
}

@Module
@InstallIn(SingletonComponent::class)
internal object DeviceModuleProvide {

    @Provides
    fun provideVersionName(@ApplicationContext context: Context): VersionName =
        VersionName(context.getVersionName())

}
