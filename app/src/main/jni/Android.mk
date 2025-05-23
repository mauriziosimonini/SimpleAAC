LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := espeak_jni
LOCAL_SRC_FILES := espeak_jni.cpp
LOCAL_C_INCLUDES := $(LOCAL_PATH)        # for speak_lib.h location

# If linking static:
LOCAL_STATIC_LIBRARIES := espeak

# If linking shared:
# LOCAL_SHARED_LIBRARIES := espeak

# If you have other dependencies (log, etc)
LOCAL_LDLIBS    := -llog

include $(BUILD_SHARED_LIBRARY)

# Static library for espeak
include $(CLEAR_VARS)
LOCAL_MODULE := espeak
LOCAL_SRC_FILES := libespeak.a
include $(PREBUILT_STATIC_LIBRARY)

# If you built a shared library (.so) instead, use:
# include $(CLEAR_VARS)
# LOCAL_MODULE := espeak
# LOCAL_SRC_FILES := libespeak.so
# include $(PREBUILT_SHARED_LIBRARY)