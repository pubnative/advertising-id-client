package net.pubnative;

import android.os.Handler;

import net.pubnative.advertising_id_client.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class AdvertisingIdClientTest {

    @Test
    public void creates() {

        AdvertisingIdClient opener = new AdvertisingIdClient();
        assertThat(opener).isNotNull();
    }

    @Test
    public void invokeStartCallbackWithListener(){
        AdvertisingIdClient advertisingIdClient = spy(AdvertisingIdClient.class);
        AdvertisingIdClient.Listener advertisingIdClientListener = spy(AdvertisingIdClient.Listener.class);

        advertisingIdClient.mHandler = new Handler();
        advertisingIdClient.setListener(advertisingIdClientListener);
        advertisingIdClient.invokeStart();

        verify(advertisingIdClientListener, times(1)).onAdvertisingIdClientStart();
    }

    @Test
    public void invokeFinishCallbackWithListener() {

        AdvertisingIdClient advertisingIdClient = spy(AdvertisingIdClient.class);
        AdvertisingIdClient.Listener advertisingIdClientListener = spy(AdvertisingIdClient.Listener.class);

        advertisingIdClient.mHandler = new Handler();
        advertisingIdClient.setListener(advertisingIdClientListener);
        advertisingIdClient.invokeFinish(mock(AdvertisingIdClient.AdInfo.class));

        verify(advertisingIdClientListener, times(1)).onAdvertisingIdClientFinish(any(AdvertisingIdClient.AdInfo.class));
    }

    @Test
    public void invokeFailCallbackWithListener() {

        AdvertisingIdClient advertisingIdClient = spy(AdvertisingIdClient.class);
        AdvertisingIdClient.Listener advertisingIdClientListener = spy(AdvertisingIdClient.Listener.class);

        advertisingIdClient.mHandler = new Handler();
        advertisingIdClient.setListener(advertisingIdClientListener);
        advertisingIdClient.invokeFail(mock(Exception.class));

        verify(advertisingIdClientListener, times(1)).onAdvertisingIdClientFail(any(Exception.class));
    }

    @Test
    public void invokeCallbacksWithoutListener() {

        AdvertisingIdClient advertisingIdClient = spy(AdvertisingIdClient.class);

        advertisingIdClient.mHandler = new Handler();

        advertisingIdClient.invokeFail(mock(Exception.class));
        advertisingIdClient.invokeFinish(mock(AdvertisingIdClient.AdInfo.class));
        advertisingIdClient.invokeStart();
    }
}
