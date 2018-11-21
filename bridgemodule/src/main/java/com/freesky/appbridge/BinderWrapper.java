package com.freesky.appbridge;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by letgogo on 2018/11/20.
 */
public class BinderWrapper implements Parcelable {

    public IBinder mIBinder;

    public static final Creator<BinderWrapper> CREATOR = new Creator<BinderWrapper>() {
        @Override
        public BinderWrapper createFromParcel(Parcel parcel) {
            return new BinderWrapper(parcel);
        }

        @Override
        public BinderWrapper[] newArray(int i) {
            return new BinderWrapper[0];
        }
    };

    public BinderWrapper(@NonNull IBinder iBinder) {
        mIBinder = iBinder;
    }

    public BinderWrapper(Parcel parcel) {
        mIBinder = parcel.readStrongBinder();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStrongBinder(mIBinder);
    }
}
