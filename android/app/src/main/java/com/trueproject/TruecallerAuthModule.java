package com.trueproject;

import org.json.JSONObject;
import org.json.JSONStringer;
import android.app.Activity;
import android.os.Build;
import android.widget.Toast;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.truecaller.android.sdk.ITrueCallback;
import com.truecaller.android.sdk.TrueButton;
import com.truecaller.android.sdk.TrueError;
import com.truecaller.android.sdk.TrueException;
import com.truecaller.android.sdk.TrueProfile;
import com.truecaller.android.sdk.TruecallerSDK;
import com.truecaller.android.sdk.TruecallerSdkScope;
import com.truecaller.android.sdk.clients.VerificationCallback;
import com.truecaller.android.sdk.clients.VerificationDataBundle;

import com.truecaller.android.sdk.SdkThemeOptions;
import java.net.ServerSocket;
import java.net.URI;

import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;
import static com.truecaller.android.sdk.clients.VerificationDataBundle.KEY_OTP;

public class TruecallerAuthModule extends ReactContextBaseJavaModule {

  private Promise promise = null;
  ReactContext reactContext;

  private final ITrueCallback sdkCallback = new ITrueCallback() {

    @Override
    public void onSuccessProfileShared(@NonNull final TrueProfile trueProfile) {
      System.out.println("on success profile shared");
      if (promise != null) {
        WritableMap map = Arguments.createMap();
        map.putString("status", "SUCCESS_PROFILE_SHARED");
        map.putBoolean("successful", true);
        map.putString("firstName", trueProfile.firstName);
        map.putString("lastName", trueProfile.lastName);
        map.putString("phoneNumber", trueProfile.phoneNumber);
        map.putString("gender", trueProfile.gender);
        map.putString("street", trueProfile.street);
        map.putString("city", trueProfile.city);
        map.putString("zipcode", trueProfile.zipcode);
        map.putString("countryCode", trueProfile.countryCode);
        map.putString("facebookId", trueProfile.facebookId);
        map.putString("twitterId", trueProfile.twitterId);
        map.putString("email", trueProfile.email);
        map.putString("url", trueProfile.url);
        map.putString("avatarUrl", trueProfile.avatarUrl);
        map.putBoolean("isVerified", trueProfile.isTrueName);
        map.putBoolean("isAmbassador", trueProfile.isAmbassador);
        map.putString("companyName", trueProfile.companyName);
        map.putString("jobTitle", trueProfile.jobTitle);
        map.putString("payload", trueProfile.payload);
        map.putString("signature", trueProfile.signature);
        map.putString("signatureAlgorithm", trueProfile.signatureAlgorithm);
        map.putString("requestNonce", trueProfile.requestNonce);
        promise.resolve(map);
      }
    }
    @Override
    public void onFailureProfileShared(@NonNull final TrueError trueError) {
      Log.d("TruecallerAuthModule", Integer.toString(trueError.getErrorType()));
      System.out.println("in on failure profile shared");
      if (promise != null) {
        String errorReason = null;
        switch (trueError.getErrorType()) {
          case TrueError.ERROR_TYPE_INTERNAL:
            errorReason = "ERROR_TYPE_INTERNAL";
            break;
          case TrueError.ERROR_TYPE_NETWORK:
            errorReason = "ERROR_TYPE_NETWORK";
            break;
          case TrueError.ERROR_TYPE_USER_DENIED:
            errorReason = "ERROR_TYPE_USER_DENIED";
            break;
          case TrueError.ERROR_PROFILE_NOT_FOUND:
            errorReason = "ERROR_TYPE_UNAUTHORIZED_PARTNER";
            break;
          case TrueError.ERROR_TYPE_UNAUTHORIZED_USER:
            errorReason = "ERROR_TYPE_UNAUTHORIZED_USER";
            break;
          case TrueError.ERROR_TYPE_TRUECALLER_CLOSED_UNEXPECTEDLY:
            errorReason = "ERROR_TYPE_TRUECALLER_CLOSED_UNEXPECTEDLY";
            break;
          case TrueError.ERROR_TYPE_TRUESDK_TOO_OLD:
            errorReason = "ERROR_TYPE_TRUESDK_TOO_OLD";
            break;
          case TrueError.ERROR_TYPE_POSSIBLE_REQ_CODE_COLLISION:
            errorReason = "ERROR_TYPE_POSSIBLE_REQ_CODE_COLLISION";
            break;
          case TrueError.ERROR_TYPE_RESPONSE_SIGNATURE_MISMATCH:
            errorReason = "ERROR_TYPE_RESPONSE_SIGNATURE_MISSMATCH";
            break;
          case TrueError.ERROR_TYPE_REQUEST_NONCE_MISMATCH:
            errorReason = "ERROR_TYPE_REQUEST_NONCE_MISSMATCH";
            break;
          case TrueError.ERROR_TYPE_INVALID_ACCOUNT_STATE:
            errorReason = "ERROR_TYPE_INVALID_ACCOUNT_STATE";
            break;
          case TrueError.ERROR_TYPE_TC_NOT_INSTALLED:
            errorReason = "ERROR_TYPE_TC_NOT_INSTALLED";
            break;
        }
        WritableMap map = Arguments.createMap();
        map.putString("error", errorReason != null ? errorReason : "ERROR_TYPE_NULL");
        promise.resolve(map);
      }
    }
    @Override
    public void onVerificationRequired() {
       System.out.println("in on verificatioin required");
      WritableMap map = Arguments.createMap();
      map.putString("status", "VERIFICATION_REQUIRED");
      promise.resolve(map);
    }
  };


  final VerificationCallback apiCallback = new VerificationCallback() {

    @Override
    public void onRequestSuccess(int requestCode, @Nullable VerificationDataBundle extras) {
      System.out.println("TruecallerAuthModule - :)");

      if (requestCode == VerificationCallback.TYPE_MISSED_CALL_INITIATED) {
        System.out.println("TruecallerAuthModule - Missed call initiated");
      }

      if (requestCode == VerificationCallback.TYPE_MISSED_CALL_RECEIVED) {
        System.out.println("TruecallerAuthModule - Missed call finished");

        // should return TYPE_MISSED_CALL_RECEIVED

        WritableMap map = Arguments.createMap();
        map.putString("status", "TYPE_MISSED_CALL_RECEIVED");
        promise.resolve(map);

//        promise.resolve(true);
        // TrueProfile profile = new TrueProfile.Builder(firstName, lastName).build();
        // TruecallerSDK.getInstance().verifyMissedCall(profile, apiCallback);
      }

      if (requestCode == VerificationCallback.TYPE_OTP_INITIATED) {
        System.out.println("TruecallerAuthModule - VerificationCallback.TYPE_OTP_INITIATED");
      }

      if (requestCode == VerificationCallback.TYPE_OTP_RECEIVED) {
        System.out.println("TruecallerAuthModule - VerificationCallback.TYPE_OTP_RECEIVED");
        // TrueProfile profile = new TrueProfile.Builder(firstName, lastName).build();
        // TruecallerSDK.getInstance().verifyOtp(profile, KEY_OTP, apiCallback);
        // For the case of otp
      }

      if (requestCode == VerificationCallback.TYPE_VERIFICATION_COMPLETE) {
        System.out.println("TruecallerAuthModule - VerificationCallback.TYPE_VERIFICATION_COMPLETE");

        // this will get us the access token
//        System.out.println(extras.getString(VerificationDataBundle.KEY_ACCESS_TOKEN));

        WritableMap map = Arguments.createMap();
        map.putString("status", "TYPE_VERIFICATION_COMPLETE");
        map.putString("accessToken", extras.getString(VerificationDataBundle.KEY_ACCESS_TOKEN));
        map.putString("profile", extras.getString(VerificationDataBundle.KEY_VERIFIED_PROFILE));
        promise.resolve(map);
      }

      if (requestCode == VerificationCallback.TYPE_PROFILE_VERIFIED_BEFORE) {
        System.out.println("TruecallerAuthModule - VerificationCallback.TYPE_PROFILE_VERIFIED_BEFORE");
        if (promise != null) {
          WritableMap map = Arguments.createMap();
          map.putString("status", "TYPE_PROFILE_VERIFIED_BEFORE");
          map.putBoolean("successful", true);
          map.putString("accessToken", extras.getProfile().accessToken);
          map.putString("firstName", extras.getProfile().firstName);
          map.putString("lastName", extras.getProfile().lastName);
          map.putString("phoneNumber", extras.getProfile().phoneNumber);
          map.putString("gender", extras.getProfile().gender);
          map.putString("street", extras.getProfile().street);
          map.putString("city", extras.getProfile().city);
          map.putString("zipcode", extras.getProfile().zipcode);
          map.putString("countryCode", extras.getProfile().countryCode);
          map.putString("facebookId", extras.getProfile().facebookId);
          map.putString("twitterId", extras.getProfile().twitterId);
          map.putString("email", extras.getProfile().email);
          map.putString("url", extras.getProfile().url);
          map.putString("avatarUrl", extras.getProfile().avatarUrl);
          map.putBoolean("isVerified", extras.getProfile().isTrueName);
          map.putBoolean("isAmbassador", extras.getProfile().isAmbassador);
          map.putString("companyName", extras.getProfile().companyName);
          map.putString("jobTitle", extras.getProfile().jobTitle);
          map.putString("payload", extras.getProfile().payload);
          map.putString("signature", extras.getProfile().signature);
          map.putString("signatureAlgorithm", extras.getProfile().signatureAlgorithm);
          map.putString("requestNonce", extras.getProfile().requestNonce);
          promise.resolve(map);
        }
      }
    }

    // does not work when the permissions are not allowed at the first time

    @Override
    public void onRequestFailure(final int requestCode, @NonNull final TrueException e) {
      System.out.println("------------- IN on request failure method -------------------");
      System.out.println(e.getExceptionMessage());

      WritableMap map = Arguments.createMap();
      map.putString("status", "ON_REQUEST_FAILURE");
      map.putInt("requestCode", requestCode);
      map.putString("error", e.getExceptionMessage());
      promise.resolve(map);
    }
  };

  TruecallerAuthModule(ReactApplicationContext reactContextt) {
    super(reactContextt);
    reactContext = reactContextt;
    TruecallerSdkScope trueScope = new TruecallerSdkScope.Builder(reactContext, sdkCallback)
            .consentMode(TruecallerSdkScope.CONSENT_MODE_BOTTOMSHEET)
            .consentTitleOption(TruecallerSdkScope.SDK_CONSENT_TITLE_VERIFY)
            .footerType(TruecallerSdkScope.FOOTER_TYPE_CONTINUE)
            .sdkOptions(TruecallerSdkScope.SDK_OPTION_WITH_OTP)
            .build();
    TruecallerSDK.init(trueScope);

    System.out.println("----------------------------------------- calling true caller module -----------------------------------------");
    reactContext.addActivityEventListener(mActivityEventListener);
  }

  @ReactMethod
  public void verifyNonTrueCaller(String phoneNumber, Promise promisee) {
    Log.d("TruecallerAuthModule - PhoneNumber", phoneNumber);
    promise = promisee;

    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        // My block, doing stuff on the view
        // System.out.println("IN authenticate function 2");
        // TruecallerSDK.getInstance().getUserProfile((FragmentActivity) getCurrentActivity());
        Log.d("TruecallerAuthModule -  run method", phoneNumber);
        // TruecallerSDK.getInstance().getUserProfile((FragmentActivity) getCurrentActivity());
        TruecallerSDK.getInstance().requestVerification("IN", phoneNumber, apiCallback, (FragmentActivity) getCurrentActivity());
      }
    });
  }

  @ReactMethod
  public void createNonTCUserProfile(String firstName, String lastName, Promise promise) {
    Log.d("First Name", firstName);
    Log.d("Last Name", lastName);
    promise = promise;
    TrueProfile profile = new TrueProfile.Builder(firstName, lastName).build();
    TruecallerSDK.getInstance().verifyMissedCall(profile, apiCallback);
  }

  @Override
  public String getName() {
    return "TruecallerAuthModule";
  }

  private TrueButton trueButton;

  @ReactMethod
  public void authenticate(Promise promisee) {
    System.out.println("IN authenticate function");
    try {
      promise = promisee;
      if (TruecallerSDK.getInstance() != null) {
         runOnUiThread(new Runnable() {
          @Override
          public void run() {
            // My block, doing stuff on the view
            System.out.println("IN authenticate function 2");
            TruecallerSDK.getInstance().getUserProfile((FragmentActivity) getCurrentActivity());
          }
        });
      } else {
        WritableMap map = Arguments.createMap();
        map.putString("error", "ERROR_TYPE_NOT_SUPPORTED");
        promise.resolve(map);
      }
    } catch (Exception e) {
      promise.reject(e);
    }
  }

  private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {
    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
      super.onActivityResult(activity, requestCode, resultCode, intent);
      if (requestCode == 100) {
        TruecallerSDK.getInstance().onActivityResultObtained((FragmentActivity) activity, resultCode, intent);
      }
    }
  };
}