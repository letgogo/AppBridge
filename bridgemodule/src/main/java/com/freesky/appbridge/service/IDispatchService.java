/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package com.freesky.appbridge.service;
// Declare any non-default types here with import statements

public interface IDispatchService extends android.os.IInterface {
    /**
     * Local-side IPC implementation stub class.
     */
    public static abstract class Stub extends android.os.Binder implements IDispatchService {
        private static final String DESCRIPTOR = "com.freesky.appbridge.service.IDispatchService";

        /**
         * Construct the stub at attach it to the interface.
         */
        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        /**
         * Cast an IBinder object into an com.freesky.appbridge.service.IDispatchService interface,
         * generating a proxy if needed.
         */
        public static IDispatchService asInterface(android.os.IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof IDispatchService))) {
                return ((IDispatchService) iin);
            }
            return new Proxy(obj);
        }

        @Override
        public android.os.IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException {
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                case TRANSACTION_send: {
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0;
                    _arg0 = data.readString();
                    String _arg1;
                    _arg1 = data.readString();
                    String _result = this.send(_arg0, _arg1);
                    reply.writeNoException();
                    reply.writeString(_result);
                    return true;
                }
                case TRANSACTION_registerMessageCallback: {
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0;
                    _arg0 = data.readString();
                    IMessageCallback _arg1;
                    _arg1 = IMessageCallback.Stub.asInterface(data.readStrongBinder());
                    this.registerMessageCallback(_arg0, _arg1);
                    reply.writeNoException();
                    return true;
                }
            }
            return super.onTransact(code, data, reply, flags);
        }

        private static class Proxy implements IDispatchService {
            private android.os.IBinder mRemote;

            Proxy(android.os.IBinder remote) {
                mRemote = remote;
            }

            @Override
            public android.os.IBinder asBinder() {
                return mRemote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override
            public String send(String pkg, String msg) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                String _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(pkg);
                    _data.writeString(msg);
                    mRemote.transact(Stub.TRANSACTION_send, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.readString();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public void registerMessageCallback(String pkg, IMessageCallback msgCallback) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(pkg);
                    _data.writeStrongBinder((((msgCallback != null)) ? (msgCallback.asBinder()) : (null)));
                    mRemote.transact(Stub.TRANSACTION_registerMessageCallback, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        static final int TRANSACTION_send = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
        static final int TRANSACTION_registerMessageCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    }

    public String send(String pkg, String msg) throws android.os.RemoteException;

    public void registerMessageCallback(String pkg, IMessageCallback msgCallback) throws android.os.RemoteException;
}
